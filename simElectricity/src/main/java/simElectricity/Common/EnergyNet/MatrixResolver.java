package simElectricity.Common.EnergyNet;

public interface MatrixResolver {
	/**Initialize to a size*size square matrix
	 * @param size */
	void newMatrix(int size);
	/**Push a coefficient into the matrix(current location)
	 * @param value*/
	void pushCoefficient(double value);
	/** Shift to the next column*/
	void pushColumn();
	/**Solve the matrix and clean the mess
	 * @param b the array of right hand side values*/
	void solve(double[] b);		
}
