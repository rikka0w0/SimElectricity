package simElectricity.Common.EnergyNet;

public class NativeSparseMatrix {
	static {System.loadLibrary("UMFPack");}
	public static NativeSparseMatrix solver = new NativeSparseMatrix();
	
	public native void newMatrix(int sz);
	public native void pushCoefficient(int column,double value);
	public native void solveMatrix(double[] b, double[] x);
	public native void printMatrix();
}
