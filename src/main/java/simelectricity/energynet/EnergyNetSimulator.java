package simelectricity.energynet;

import simelectricity.common.ConfigManager;
import simelectricity.common.SELogger;
import simelectricity.energynet.components.*;
import simelectricity.energynet.matrix.IMatrixSolver;

import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.Nullable;

public class EnergyNetSimulator extends Thread {
	/////////////////////////////////////////////////
	/// Configuration
	/////////////////////////////////////////////////
    /**
     * The absolute tolerance
     */
    private double epsilon;
    /**
     * The conductance placed between each PN junction(to alleviate convergence problem)
     */
    private double Gpn;
    /**
     * Matrix solving algorithm used to solve the problem
     */
    private IMatrixSolver matrix;

    @Nullable
    public String getMatrixSolverName() {
        return matrix == null ? null : matrix.getClass().getSimpleName();
    }

    private volatile boolean configChanged;
    public void setConfigChanged() {
    	configChanged = true;
    }

    private void checkAndUpdateConfig() {
        if (configChanged) {
        	configChanged = false;

            epsilon = Math.pow(10, -ConfigManager.precision.get());
            Gpn = 1.0D / ConfigManager.shuntPN.get();
            matrix = IMatrixSolver.newSolver(ConfigManager.matrixSolver.get());
        }
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
    private volatile boolean newResultAvaliable;

    protected EnergyNetSimulator(EnergyNetDataProvider dataProvider, String name) {
    	this.dataProvider = dataProvider;
    	this.setName(name);
    	this.processing = false;
    	this.suicide = false;
    	this.newResultAvaliable = false;
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
    public boolean isNewResultAvaliable() {
        if (this.newResultAvaliable) {
            this.newResultAvaliable = false;
            return true;
        }
        return false;
    }

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

                if (this.matrix == null)
                	this.setConfigChanged();

                checkAndUpdateConfig();

                processing = true;
                SELogger.logInfo(SELogger.simulator, this.getName() + " Started");
                startAt = System.currentTimeMillis();
                this.runSimulator(this.needOptimize);
                SELogger.logInfo(SELogger.simulator, this.getName() + " Done");
                this.duration = System.currentTimeMillis() - startAt;

                processing = false;
                newResultAvaliable = true;
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

    protected final void runSimulator(boolean optimizeGraph) {
    	SEGraph circuit = dataProvider.getTEGraph();
        if (optimizeGraph)
        	circuit.optimizGraph();

        LinkedList<SEComponent> unknownVoltageNodes = circuit.getTerminalNodes();

        int matrixSize = 0;
        for (SEComponent comp : unknownVoltageNodes) {
            comp.index = matrixSize++;
        }

        if (matrixSize == 0) return;

        double[] voltages = new double[matrixSize];
        double[] currents = new double[matrixSize];

        // --- Data-Oriented Extraction & Static Jacobian Caching ---
        double[] staticJacobian = new double[matrixSize * matrixSize];
        double[] staticCurrents = new double[matrixSize];

        int numCPL = 0, numCPS = 0, numDiodes = 0;
        for (SEComponent comp : unknownVoltageNodes) {
            if (comp instanceof ConstantPowerLoad && ((ConstantPowerLoad)comp).isOn()) numCPL++;
            else if (comp instanceof ConstantPowerSource && ((ConstantPowerSource)comp).isOn()) numCPS++;
            else if (comp instanceof DiodeInput) numDiodes++;
        }

        int[] cplNode = new int[numCPL];
        double[] cplP = new double[numCPL];
        double[] cplMaxR = new double[numCPL];
        double[] cplMinR = new double[numCPL];

        int[] cpsNode = new int[numCPS];
        double[] cpsP = new double[numCPS];
        double[] cpsMaxV = new double[numCPS];
        double[] cpsMinV = new double[numCPS];

        int[] diodeIn = new int[numDiodes];
        int[] diodeOut = new int[numDiodes];
        DiodeInput[] diodeComponents = new DiodeInput[numDiodes];

        int idxCPL = 0, idxCPS = 0, idxDiode = 0;

        for (SEComponent node : unknownVoltageNodes) {
            int i = node.index;
            int offsetI = i * matrixSize;

            // 1. Optimized Neighbors (Linear Edges)
            Iterator<SEComponent> itN = node.optimizedNeighbors.iterator();
            Iterator<Double> itR = node.optimizedResistance.iterator();
            while (itN.hasNext()) {
                int j = itN.next().index;
                double G = 1.0 / itR.next();
                staticJacobian[offsetI + i] += G;
                staticJacobian[offsetI + j] -= G;
            }

            // 2. Cables and Grids
            if (node instanceof CableBase) {
                CableBase<?> cable = (CableBase<?>) node;
                if (cable.hasShuntResistance()) {
                    staticJacobian[offsetI + i] += 1.0 / cable.getShuntResistance();
                }
                if (cable instanceof Cable) {
                    Cable c = (Cable) cable;
                    if (c.connectedGridNode != null && c.isGridLinkEnabled()) {
                        int j = c.connectedGridNode.index;
                        int offsetJ = j * matrixSize;
                        double G = 1.0 / c.getResistance();
                        staticJacobian[offsetI + i] += G;
                        staticJacobian[offsetI + j] -= G;
                        staticJacobian[offsetJ + i] -= G;
                    }
                }
            } else if (node instanceof GridNode) {
                GridNode gridNode = (GridNode) node;
                if (gridNode.interConnection != null && gridNode.interConnection.isGridLinkEnabled()) {
                    staticJacobian[offsetI + i] += 1.0 / gridNode.interConnection.getResistance();
                }

                if (gridNode.type == GridNode.ISEGridNode_TransformerPrimary) {
                    GridNode sec = gridNode.complement;
                    int j = sec.index;
                    int offsetJ = j * matrixSize;
                    double ratio = gridNode.ratio;
                    double res = gridNode.resistance;
                    staticJacobian[offsetI + i] += ratio * ratio / res;
                    staticJacobian[offsetI + j] -= ratio / res;
                    staticJacobian[offsetJ + i] -= ratio / res;
                } else if (gridNode.type == GridNode.ISEGridNode_TransformerSecondary) {
                    GridNode pri = gridNode.complement;
                    double res = pri.resistance;
                    staticJacobian[offsetI + i] += 1.0 / res;
                }
            }
            // 3. Voltage Source
            else if (node instanceof VoltageSource) {
                VoltageSource vs = (VoltageSource) node;
                if (vs.isOn()) {
                    double G = 1.0 / vs.getResistance();
                    staticJacobian[offsetI + i] += G;
                    staticCurrents[i] += vs.getOutputVoltage() * G;
                }
            }
            // 4. Constant Power Load
            else if (node instanceof ConstantPowerLoad) {
                ConstantPowerLoad load = (ConstantPowerLoad) node;
                if (load.isOn()) {
                    cplNode[idxCPL] = i;
                    cplP[idxCPL] = load.getRatedPower();
                    cplMaxR[idxCPL] = load.getMaximumResistance();
                    cplMinR[idxCPL] = load.getMinimumResistance();
                    idxCPL++;
                }
            }
            // 5. Constant Power Source
            else if (node instanceof ConstantPowerSource) {
                ConstantPowerSource source = (ConstantPowerSource) node;
                if (source.isOn()) {
                    cpsNode[idxCPS] = i;
                    cpsP[idxCPS] = source.getRatedPower();
                    cpsMaxV[idxCPS] = source.getMaximumOutputVoltage();
                    cpsMinV[idxCPS] = source.getMinimumOutputVoltage();
                    idxCPS++;
                }
            }
            // 6. Switches
            else if (node instanceof SwitchA) {
                SwitchA sw = (SwitchA) node;
                if (sw.isOn()) {
                    int j = sw.getComplement().index;
                    int offsetJ = j * matrixSize;
                    double G = 1.0 / sw.getResistance();
                    staticJacobian[offsetI + i] += G;
                    staticJacobian[offsetI + j] -= G;
                    staticJacobian[offsetJ + i] -= G;
                }
            } else if (node instanceof SwitchB) {
                SwitchB sw = (SwitchB) node;
                if (sw.isOn()) {
                    staticJacobian[offsetI + i] += 1.0 / sw.getResistance();
                }
            }
            // 7. Transformer
            else if (node instanceof TransformerPrimary) {
                TransformerPrimary pri = (TransformerPrimary) node;
                int j = pri.getComplement().index;
                int offsetJ = j * matrixSize;
                double ratio = pri.getRatio();
                double res = pri.getInternalResistance();
                staticJacobian[offsetI + i] += ratio * ratio / res;
                staticJacobian[offsetI + j] -= ratio / res;
                staticJacobian[offsetJ + i] -= ratio / res;
            } else if (node instanceof TransformerSecondary) {
                TransformerSecondary sec = (TransformerSecondary) node;
                staticJacobian[offsetI + i] += 1.0 / sec.getComplement().getInternalResistance();
            }
            // 8. Diode
            else if (node instanceof DiodeInput) {
                diodeIn[idxDiode] = i;
                diodeOut[idxDiode] = ((DiodeInput)node).getComplement().index;
                diodeComponents[idxDiode] = (DiodeInput)node;
                idxDiode++;
            }
        }

        // --- Iterative Solver Loop ---
        this.iterations = 0;
        double[] workingJacobian = new double[matrixSize * matrixSize];

        while (true) {
            // Compute currents mismatch vector
            // I_mismatch = staticCurrents - staticJacobian * voltages
            for (int i = 0; i < matrixSize; i++) {
                double I = staticCurrents[i];
                int offsetI = i * matrixSize;
                for (int j = 0; j < matrixSize; j++) {
                    I -= staticJacobian[offsetI + j] * voltages[j];
                }
                currents[i] = I;
            }

            // O(1) Memory Snapshot (Matrix Reset)
            System.arraycopy(staticJacobian, 0, workingJacobian, 0, matrixSize * matrixSize);

            // Non-linear elements updates
            for (int k = 0; k < numCPL; k++) {
                int i = cplNode[k];
                int offsetI = i * matrixSize;
                double v = voltages[i];
                double Rcal = v * v / cplP[k];
                if (Rcal > cplMaxR[k]) Rcal = cplMaxR[k];
                if (Rcal < cplMinR[k]) Rcal = cplMinR[k];
                double G = 1.0 / Rcal;
                currents[i] -= v * G;
                workingJacobian[offsetI + i] += G;
            }

            for (int k = 0; k < numCPS; k++) {
                int i = cpsNode[k];
                int offsetI = i * matrixSize;
                double v = voltages[i];
                double Isrc, G;
                if (v < cpsMinV[k]) {
                    Isrc = 2 * cpsP[k] / cpsMinV[k];
                    G = cpsP[k] / (cpsMinV[k] * cpsMinV[k]);
                } else if (v > cpsMaxV[k]) {
                    Isrc = 2 * cpsP[k] / cpsMaxV[k];
                    G = cpsP[k] / (cpsMaxV[k] * cpsMaxV[k]);
                } else {
                    Isrc = 2 * cpsP[k] / v;
                    G = cpsP[k] / (v * v);
                }
                currents[i] -= (v * G - Isrc);
                workingJacobian[offsetI + i] += G;
            }

            for (int k = 0; k < numDiodes; k++) {
                int in = diodeIn[k];
                int out = diodeOut[k];
                int offsetIn = in * matrixSize;
                int offsetOut = out * matrixSize;
                double Vd = voltages[in] - voltages[out];
                DiodeInput comp = diodeComponents[k];
                double Id = comp.calcId(Vd);
                double Gd = comp.calcG(Vd) + this.Gpn;
                
                currents[in] -= Id + Vd * this.Gpn;
                currents[out] += Id + Vd * this.Gpn;

                workingJacobian[offsetIn + in] += Gd;
                workingJacobian[offsetOut + out] += Gd;
                workingJacobian[offsetIn + out] -= Gd;
                workingJacobian[offsetOut + in] -= Gd;
            }

            boolean keepGoing = false;
            for (int i = 0; i < matrixSize; i++) {
                if (Math.abs(currents[i]) > this.epsilon) {
                    keepGoing = true;
                    break;
                }
            }

            if (keepGoing) {
                if (this.iterations > ConfigManager.maxIteration) {
                    SELogger.logError(SELogger.simulator, "Convergence problem: Reached maximum iteration limit!");
                    break;
                }
            } else {
                break;
            }

            this.matrix.newMatrix(matrixSize);
            for (int i = 0; i < matrixSize; i++) {
                int offsetI = i * matrixSize;
                for (int j = 0; j < matrixSize; j++) {
                    double val = workingJacobian[offsetI + j];
                    if (val != 0) {
                        this.matrix.setElementValue(i, j, val);
                    }
                }
            }
            this.matrix.finishEditing();

            if (!this.matrix.solve(currents)) {
                throw new RuntimeException("Due to incorrect value of components, the EnergyNet has been shutdown!");
            }

            for (int i = 0; i < matrixSize; i++) {
                if (!Double.isNaN(currents[i]))
                    voltages[i] += currents[i];
            }

            this.iterations++;
        }

        // Update voltage cache
        for (SEComponent node : unknownVoltageNodes) {
            node.newVoltage = voltages[node.index];
        }

        SELogger.logInfo(SELogger.simulator, "Simulation converges in "+ this.iterations + " iterations.");
    }
}
