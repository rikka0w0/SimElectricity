package simelectricity.energynet;

import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.common.ConfigManager;
import simelectricity.common.SELogger;
import simelectricity.energynet.components.*;
import simelectricity.energynet.matrix.IMatrixSolver;
import simelectricity.energynet.matrix.IMatrixSolver.MatrixHelper;

import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;


public class EnergyNetSimulator extends Thread {
	/////////////////////////////////////////////////
	/// Configuration
	/////////////////////////////////////////////////
    /**
     * The absolute tolerance
     */
    private static double epsilon;
    /**
     * The conductance placed between each PN junction(to alleviate convergence problem)
     */
    private static double Gpn;
    /**
     * Matrix solving algorithm used to solve the problem
     */
    private static IMatrixSolver matrix;
    
    public static final void config() {
        epsilon = Math.pow(10, -ConfigManager.precision);
        Gpn = 1.0D / ConfigManager.shuntPN;
        matrix = MatrixHelper.newSolver(ConfigManager.matrixSolver);
    }
    
	/////////////////////////////////////////////////
	/// Runtime
	/////////////////////////////////////////////////
    private final EnergyNetDataProvider dataProvider;
    /**
     * Records the number of iterations during last iterating process
     */
    protected volatile int iterations;
    
    private volatile boolean needOptimize;    	//Set to true to launch the optimizer
    private volatile boolean processing;    	//An indicator of the EnergyNet state
    private volatile long duration;            	//Time taken for the latest simulation, in milliseconds
    private volatile boolean suicide;
    
    protected EnergyNetSimulator(EnergyNetDataProvider dataProvider, String name) {
    	this.dataProvider = dataProvider;
    	this.setName(name);
    	this.processing = false;
    	this.suicide = false;
    }
    
	/////////////////////////////////////////////////
	/// Info
	/////////////////////////////////////////////////
    public int getIterations() {
    	return this.iterations;
    }
    
    public long getTimeConsumption() {
    	return this.duration;
    }

    public float getMatrixSize() {
    	return this.matrix.getMatrixSize();
    }
    
    public float getTotalNonZeros() {
    	return this.matrix.getTotalNonZeros();
    }
    
    public float getDensity() {
        if (this.matrix.getMatrixSize() == 0) {
            return Float.NaN;
        } else {
        	return (float)this.matrix.getTotalNonZeros() * 100F / (float)this.matrix.getMatrixSize() / (float)this.matrix.getMatrixSize();
        }
    }
    
	/////////////////////////////////////////////////
	/// Threading
	/////////////////////////////////////////////////
    public void suicide() {
    	this.suicide = true;
    	this.interrupt();
    }
    
    public boolean isWorking() {
    	return this.processing;
    }
    
    public void start(boolean needOptimize) {
    	this.needOptimize = needOptimize;
    	
    	if (!this.isAlive())
    		this.start();
    	
    	synchronized (this) {  
    		this.notify();
        }
    }
    
    @Override
    public void run() {
        long startAt;
        
        while(true){  
            try {
                SELogger.logInfo(SELogger.simulator, this.getName() + " wake up");

                processing = true;
                SELogger.logInfo(SELogger.simulator, this.getName() + " Started");
                startAt = System.currentTimeMillis();
                this.runSimulator(this.needOptimize);
                SELogger.logInfo(SELogger.simulator, this.getName() + " Done");
                this.duration = System.currentTimeMillis() - startAt;

                //Execute Handlers
                Iterator<TileEntity> iterator = this.dataProvider.getLoadedTileIterator();
                while (iterator.hasNext()) {
                    TileEntity te = iterator.next();
                    if (te instanceof ISEEnergyNetUpdateHandler)
                        ((ISEEnergyNetUpdateHandler) te).onEnergyNetUpdate();
                }
                iterator = this.dataProvider.getLoadedGridTileIterator();
                while (iterator.hasNext()) {
                    TileEntity te = iterator.next();
                    if (te instanceof ISEEnergyNetUpdateHandler)
                        ((ISEEnergyNetUpdateHandler) te).onEnergyNetUpdate();
                }

                processing = false;
                SELogger.logInfo(SELogger.simulator, this.getName() + " sleep");
            	
                synchronized (this) {  
                    wait();  
                }
            } catch (InterruptedException e) {
            	if (this.suicide) {
                	SELogger.logInfo(SELogger.general, this.getName() + " is shutting down");
            		return;
            	}
            	
                e.printStackTrace();  
            }  
        }  
        
        
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

                if (vs.isOn())
                    currents[columnNode.index] -= (voltages[vs.index] - vs.getOutputVoltage()) / vs.getResistance();
            } else if (columnNode instanceof ConstantPowerLoad) {
                ConstantPowerLoad load = (ConstantPowerLoad) columnNode;

                double V = voltages[load.index];
                double Rcal = V * V / load.getRatedPower();

                if (Rcal > load.getMaximumResistance())
                    Rcal = load.getMaximumResistance();
                if (Rcal < load.getMinimumResistance())
                    Rcal = load.getMinimumResistance();

                if (load.isOn())
                    currents[columnNode.index] -= V / Rcal;
            } else if (columnNode instanceof ConstantPowerSource) {
                ConstantPowerSource source = (ConstantPowerSource) columnNode;

                double V = voltages[source.index];

                double Vint = 2*source.getMinimumOutputVoltage();
                double Po = source.getRatedPower();
                double Rmax = Vint*Vint/Po/4;
                double Rmin = (Vint-source.getMaximumOutputVoltage())*source.getMaximumOutputVoltage()/Po;

                double Rcal = (Vint-V)*V/Po;
                if (Rcal > Rmax)
                    Rcal = Rmax;
                if (Rcal < Rmin)
                    Rcal = Rmin;

                if (source.isOn())
                    currents[columnNode.index] -= (V - Vint) / Rcal;
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
                VoltageSource vs = (VoltageSource) columnNode;

                if (vs.isOn())
                    diagonalElement += 1.0D / vs.getResistance();
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

                if (load.isOn())
                    diagonalElement += 1.0D / Rcal;
            }

            //Constant power source
            else if (columnNode instanceof ConstantPowerSource) {
                ConstantPowerSource source = (ConstantPowerSource) columnNode;

                double V = voltages[source.index];

                double Vint = 2*source.getMinimumOutputVoltage();
                double Po = source.getRatedPower();
                double Rmax = Vint*Vint/Po/4;
                double Rmin = (Vint-source.getMaximumOutputVoltage())*source.getMaximumOutputVoltage()/Po;

                double Rcal = (Vint-V)*V/Po;
                if (Rcal > Rmax)
                    Rcal = Rmax;
                if (Rcal < Rmin)
                    Rcal = Rmin;

                if (source.isOn())
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
    	SEGraph circuit = dataProvider.getTEGraph();
        if (optimizeGraph)
        	circuit.optimizGraph();

        LinkedList<SEComponent> unknownVoltageNodes = circuit.getTerminalNodes();

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
        circuit.clearVoltageCache();
        for (SEComponent node : unknownVoltageNodes) {
            node.voltageCache = voltages[node.index];
        }

        SELogger.logInfo(SELogger.simulator, "Simulation converges in "+ this.iterations + " iterations.");
    }
}
