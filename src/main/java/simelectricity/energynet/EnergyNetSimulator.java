package simelectricity.energynet;

import simelectricity.api.node.ISESimulatable;
import simelectricity.common.ConfigManager;
import simelectricity.common.SELogger;
import simelectricity.energynet.components.*;
import simelectricity.energynet.matrix.IMatrixSolver;

import java.util.Iterator;
import java.util.LinkedList;


public class EnergyNetSimulator {
    //The absolute tolerance
    protected final double epsilon;
    //The conductance placed between each PN junction(to alleviate convergence problem)
    protected final double Gpn;
    //Diode parameters for regulator controllers
    protected final double Vt = 26e-6;
    protected final double Is = 1e-6;
    //Matrix solving algorithm used to solve the problem
    protected final IMatrixSolver matrix;
    //Contains information about the grid
    protected final EnergyNetDataProvider dataProvider;
    //Records the number of iterations during last iterating process
    protected int iterations;

    protected EnergyNetSimulator(double epsilon, double Gpn,
    		IMatrixSolver matrixSolver, EnergyNetDataProvider dataProvider) {
        this.epsilon = epsilon;
        this.Gpn = Gpn;
        matrix = matrixSolver;
        this.dataProvider = dataProvider;
    }

    public static final synchronized double getVoltage(ISESimulatable Tile) {
        SEComponent node = (SEComponent) Tile;
        if (node.eliminated) {
            if (node.optimizedNeighbors.size() == 2) {
                SEComponent A = node.optimizedNeighbors.getFirst();
                SEComponent B = node.optimizedNeighbors.getLast();
                double vA = A.voltageCache;
                double vB = B.voltageCache;
                double rA = node.optimizedResistance.getFirst();
                double rB = node.optimizedResistance.getLast();
                return vA - (vA - vB) * rA / (rA + rB);
            } else if (node.optimizedNeighbors.size() == 1) {
                return node.optimizedNeighbors.getFirst().voltageCache;
            } else if (node.optimizedNeighbors.size() == 0) {
                return 0;
            } else {
                throw new RuntimeException("WTF mate whats going on?!");
            }
        } else {
            return node.voltageCache;
        }
    }

    public static final synchronized double getCurrentMagnitude(ISESimulatable Tile) {
        SEComponent node = (SEComponent) Tile;
        if (node.eliminated) {
            if (node.optimizedNeighbors.size() == 2) {
                SEComponent A = node.optimizedNeighbors.getFirst();
                SEComponent B = node.optimizedNeighbors.getLast();
                double vA = A.voltageCache;
                double vB = B.voltageCache;
                double rA = node.optimizedResistance.getFirst();
                double rB = node.optimizedResistance.getLast();
                return Math.abs((vA - vB) / (rA + rB));
            } else if (node.optimizedNeighbors.size() == 1) {
                return 0;
            } else if (node.optimizedNeighbors.size() == 0) {
                return 0;
            } else {
                throw new RuntimeException("WTF mate whats going on?!");
            }
        } else if (node instanceof SwitchA) {
            SwitchA switchA = (SwitchA) node;
            double vA = switchA.voltageCache;
            double vB = switchA.getComplement().voltageCache;
            return Math.abs((vA - vB) / switchA.getResistance());
        } else if (node instanceof SwitchB) {
            SwitchB switchB = (SwitchB) node;
            double vA = switchB.voltageCache;
            double vB = switchB.getComplement().voltageCache;
            return Math.abs((vA - vB) / switchB.getResistance());
        } else if (node instanceof VoltageSource) {
            VoltageSource vs = (VoltageSource) node;
            return Math.abs((vs.voltageCache - vs.getOutputVoltage()) / vs.getResistance());
        }

        return Double.NaN;
    }

    /**
     * @param voltages input, node voltage array from last iteration
     * @param currents output, return the new current mismatch
     * @param iterator An iterator instance of the unknown voltage node linked list.
     */
    private final void calcCurrents(double[] voltages, double[] currents, Iterator<SEComponent> iterator) {
        //Calculate the current flow into each node using their voltage
        while (iterator.hasNext()) {
            SEComponent columnNode = iterator.next();

            //Node - Node
            Iterator<SEComponent> iteratorON = columnNode.optimizedNeighbors.iterator();
            Iterator<Double> iteratorR = columnNode.optimizedResistance.iterator();
            while (iteratorON.hasNext()) {
                SEComponent neighbor = iteratorON.next();
                double R = iteratorR.next();
                currents[columnNode.index] -= (voltages[columnNode.index] - voltages[neighbor.index]) / R;
            }


            if (columnNode instanceof Cable) {
                Cable cable = (Cable) columnNode;

                if (cable.hasShuntResistance())
                    currents[columnNode.index] -= voltages[cable.index] / cable.getShuntResistance();

                //Cable - GridNode interconnection
                if (cable.connectedGridNode != null && cable.isGridLinkEnabled())
                    currents[columnNode.index] -= (voltages[cable.index] - voltages[cable.connectedGridNode.index]) / cable.getResistance();
            } else if (columnNode instanceof GridNode) {
                GridNode gridNode = (GridNode) columnNode;

                //Cable - GridNode interconnection
                if (gridNode.interConnection != null && gridNode.interConnection.isGridLinkEnabled())
                    currents[columnNode.index] -= (voltages[gridNode.index] - voltages[gridNode.interConnection.index]) / gridNode.interConnection.getResistance();

                if (gridNode.type == GridNode.ISEGridNode_TransformerPrimary) {
                    GridNode pri = gridNode;
                    GridNode sec = pri.complement;
                    double ratio = pri.ratio;
                    double res = pri.resistance;
                    currents[columnNode.index] -= voltages[pri.index] * ratio * ratio / res - voltages[sec.index] * ratio / res;
                }

                if (gridNode.type == GridNode.ISEGridNode_TransformerSecondary) {
                    GridNode sec = gridNode;
                    GridNode pri = sec.complement;
                    double ratio = pri.ratio;
                    double res = pri.resistance;
                    currents[columnNode.index] -= -(voltages[pri.index] * ratio / res) + voltages[sec.index] / res;
                }
            }


            //Node - shunt and two port networks
            else if (columnNode instanceof VoltageSource) {
                VoltageSource vs = (VoltageSource) columnNode;
                currents[columnNode.index] -= (voltages[vs.index] - vs.getOutputVoltage()) / vs.getResistance();
            } else if (columnNode instanceof ConstantPowerLoad) {
                ConstantPowerLoad load = (ConstantPowerLoad) columnNode;

                double V = voltages[load.index];
                double Rcal = V * V / load.getRatedPower();

                if (Rcal > load.getMaximumResistance())
                    Rcal = load.getMaximumResistance();
                if (Rcal < load.getMinimumResistance())
                    Rcal = load.getMinimumResistance();

                if (load.isEnabled())
                    currents[columnNode.index] -= V / Rcal;
            }

            //Switch
            else if (columnNode instanceof SwitchA) {
                SwitchA A = (SwitchA) columnNode;
                SwitchB B = A.getComplement();

                if (A.isOn())
                    currents[columnNode.index] -= (voltages[A.index] - voltages[B.index]) / A.getResistance();
            } else if (columnNode instanceof SwitchB) {
                SwitchB B = (SwitchB) columnNode;
                SwitchA A = B.getComplement();

                if (A.isOn())
                    currents[columnNode.index] -= (voltages[B.index] - voltages[A.index]) / A.getResistance();
            }

            //Transformer
            else if (columnNode instanceof TransformerPrimary) {
                TransformerPrimary pri = (TransformerPrimary) columnNode;
                TransformerSecondary sec = pri.getComplement();
                double ratio = pri.getRatio();
                double res = pri.getInternalResistance();
                currents[columnNode.index] -= voltages[pri.index] * ratio * ratio / res - voltages[sec.index] * ratio / res;
            } else if (columnNode instanceof TransformerSecondary) {
                TransformerSecondary sec = (TransformerSecondary) columnNode;
                TransformerPrimary pri = sec.getComplement();
                double ratio = pri.getRatio();
                double res = pri.getInternalResistance();
                currents[columnNode.index] -= -(voltages[pri.index] * ratio / res) + voltages[sec.index] / res;
            }


            //Diode
            else if (columnNode instanceof DiodeInput) {
                DiodeInput input = (DiodeInput) columnNode;
                DiodeOutput output = input.getComplement();

                double Vd = voltages[input.index] - voltages[output.index];

                currents[columnNode.index] -= input.calcId(Vd) + Vd * this.Gpn;
            } else if (columnNode instanceof DiodeOutput) {
                DiodeOutput output = (DiodeOutput) columnNode;
                DiodeInput input = output.getComplement();

                double Vd = voltages[input.index] - voltages[output.index];


                currents[columnNode.index] += input.calcId(Vd) + Vd * this.Gpn;
            }
        }
    }

    private final void formJacobian(double[] voltages, Iterator<SEComponent> iterator) {
        this.matrix.newMatrix(voltages.length);

        while (iterator.hasNext()) {
            SEComponent columnNode = iterator.next();
            double diagonalElement = 0;

            //Add conductance between nodes
            Iterator<SEComponent> iteratorON = columnNode.optimizedNeighbors.iterator();
            Iterator<Double> iteratorR = columnNode.optimizedResistance.iterator();
            while (iteratorON.hasNext()) {
                SEComponent neighbor = iteratorON.next();
                int rowIndex = neighbor.index;
                double R = iteratorR.next();

                diagonalElement += 1.0D / R;

                this.matrix.setElementValue(columnNode.index, rowIndex, -1.0D / R);
            }


            //Cable - GridNode
            if (columnNode instanceof Cable) {
                Cable cable = (Cable) columnNode;

                if (cable.hasShuntResistance())
                    diagonalElement += 1.0D / cable.getShuntResistance();

                if (cable.connectedGridNode != null && cable.isGridLinkEnabled()) {
                    int iCable = cable.index;
                    int iGridNode = cable.connectedGridNode.index;

                    //Diagonal element
                    diagonalElement += 1.0D / cable.getResistance();

                    //Off-diagonal elements
                    this.matrix.setElementValue(iCable, iGridNode, -1.0D / cable.getResistance());
                    this.matrix.setElementValue(iGridNode, iCable, -1.0D / cable.getResistance());
                }
            } else if (columnNode instanceof GridNode) {
                GridNode gridNode = (GridNode) columnNode;

                if (gridNode.interConnection != null && gridNode.interConnection.isGridLinkEnabled()) {
                    diagonalElement += 1.0D / gridNode.interConnection.getResistance();
                }

                if (gridNode.type == GridNode.ISEGridNode_TransformerPrimary) {
                    GridNode pri = gridNode;
                    GridNode sec = pri.complement;
                    double ratio = pri.ratio;
                    double res = pri.resistance;

                    int iPri = pri.index;
                    int iSec = sec.index;

                    //Primary diagonal element
                    diagonalElement += ratio * ratio / res;

                    //Off-diagonal elements
                    this.matrix.setElementValue(iPri, iSec, -ratio / res);
                    this.matrix.setElementValue(iSec, iPri, -ratio / res);
                }

                if (gridNode.type == GridNode.ISEGridNode_TransformerSecondary) {
                    GridNode sec = gridNode;
                    GridNode pri = sec.complement;
                    double ratio = pri.ratio;
                    double res = pri.resistance;

                    diagonalElement += 1.0D / res;
                }
            }


            //Process voltage sources and resistive loads
            else if (columnNode instanceof VoltageSource) {
                diagonalElement += 1.0D / ((VoltageSource) columnNode).getResistance();
            }

            //Constant power load
            else if (columnNode instanceof ConstantPowerLoad) {
                ConstantPowerLoad load = (ConstantPowerLoad) columnNode;
                double V = voltages[columnNode.index];

                double Rcal = V * V / load.getRatedPower();

                if (Rcal > load.getMaximumResistance())
                    Rcal = load.getMaximumResistance();
                if (Rcal < load.getMinimumResistance())
                    Rcal = load.getMinimumResistance();

                if (load.isEnabled())
                    diagonalElement += 1.0D / Rcal;
            }

            //Two port networks
            //Switch
            else if (columnNode instanceof SwitchA) {
                SwitchA A = (SwitchA) columnNode;

                if (A.isOn()) {
                    int iA = A.index;
                    int iB = A.getComplement().index;

                    //Diagonal element
                    diagonalElement += 1.0D / A.getResistance();

                    //Off-diagonal elements
                    this.matrix.setElementValue(iA, iB, -1.0D / A.getResistance());
                    this.matrix.setElementValue(iB, iA, -1.0D / A.getResistance());
                }
            } else if (columnNode instanceof SwitchB) {
                //Diagonal element
                if (((SwitchB) columnNode).isOn())
                    diagonalElement += 1.0D / ((SwitchB) columnNode).getResistance();
            }


            //Transformer
            else if (columnNode instanceof TransformerPrimary) {
                TransformerPrimary pri = (TransformerPrimary) columnNode;
                int iPri = pri.index;
                int iSec = pri.getComplement().index;

                double ratio = pri.getRatio();
                double res = pri.getInternalResistance();
                //Primary diagonal element
                diagonalElement += ratio * ratio / res;

                //Off-diagonal elements
                this.matrix.setElementValue(iPri, iSec, -ratio / res);
                this.matrix.setElementValue(iSec, iPri, -ratio / res);
            } else if (columnNode instanceof TransformerSecondary) {
                //Secondary diagonal element
                diagonalElement += 1.0D / ((TransformerSecondary) columnNode).getComplement().getInternalResistance();
            }

            //Diode
            else if (columnNode instanceof DiodeInput) {
                DiodeInput input = (DiodeInput) columnNode;
                DiodeOutput output = input.getComplement();

                int iPri = input.index;
                int iSec = output.index;
                double Vd = voltages[iPri] - voltages[iSec];
                double Gd = input.calcG(Vd) + this.Gpn;

                diagonalElement += Gd;
                this.matrix.setElementValue(iPri, iSec, -Gd);
                this.matrix.setElementValue(iSec, iPri, -Gd);
            } else if (columnNode instanceof DiodeOutput) {
            	DiodeOutput output = (DiodeOutput) columnNode;
                DiodeInput input = output.getComplement();

                int iPri = input.index;
                int iSec = output.index;
                double Vd = voltages[iPri] - voltages[iSec];
                double Gd = input.calcG(Vd) + this.Gpn;

                diagonalElement += Gd;
            }

            
            this.matrix.setElementValue(columnNode.index, columnNode.index, diagonalElement);
        }

        this.matrix.finishEditing();
    }

    protected final void runSimulator(boolean optimizeGraph) {
        if (optimizeGraph)
            this.dataProvider.getTEGraph().optimizGraph();

        LinkedList<SEComponent> unknownVoltageNodes = this.dataProvider.getTEGraph().getTerminalNodes();

        int matrixSize = 0;
        Iterator<SEComponent> iterator = unknownVoltageNodes.iterator();
        while (iterator.hasNext()) {
            iterator.next().index = matrixSize;
            matrixSize++;
        }

        double[] voltages = new double[matrixSize];
        double[] currents = new double[matrixSize];

        this.iterations = 0;
        while (true) {
            for (int i = 0; i < matrixSize; i++)
                currents[i] = 0;
            //Calculate the current flow into each node using their voltage
            this.calcCurrents(voltages, currents, unknownVoltageNodes.iterator());    //Current mismatch

            boolean keepGoing = false;

            for (int i = 0; i < matrixSize; i++) {
                if (Math.abs(currents[i]) > this.epsilon)
                    keepGoing = true;
            }


            if (keepGoing) {
                if (this.iterations > ConfigManager.maxIteration) {
                    SELogger.logError(SELogger.simulator, "Convergence problem: Reached maximum iteration limit!");
                    break;
                }
            } else {
                break;
            }

            this.formJacobian(voltages, unknownVoltageNodes.iterator());

            String[] header = new String[unknownVoltageNodes.size()];
            Iterator<SEComponent> it = unknownVoltageNodes.iterator();
            while (it.hasNext()) {
                SEComponent comp = it.next();
                header[comp.index] = comp.toString();
            }
            //matrix.print(header);
            if (!this.matrix.solve(currents)) {
                throw new RuntimeException("Due to incorrect value of components, the EnergyNet has been shutdown!");
            }
            //currents is now deltaV

            //Debug.println("Iteration:", String.valueOf(iterations));
            for (int i = 0; i < matrixSize; i++) {
                if (!Double.isNaN(currents[i]))
                    voltages[i] += currents[i];
                //String[] temp = unknownVoltageNodes.get(i).toString().split("[.]");
                //Debug.println(temp[temp.length-1].split("@")[0], String.valueOf(voltages[i]));
            }

            this.iterations++;
        }


        //Update voltage cache
        this.dataProvider.getTEGraph().clearVoltageCache();
        for (SEComponent node : unknownVoltageNodes) {
            node.voltageCache = voltages[node.index];
        }

        SELogger.logInfo(SELogger.simulator, "Simulation converges in "+ this.iterations + " iterations.");
    }
}
