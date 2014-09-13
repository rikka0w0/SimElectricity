package simElectricity.Common.EnergyNet;

//Ax = b
public interface MatrixResolver {
	/**Initialize to a size*size square matrix
	 * @param size */
	void newMatrix(int size);
	/**Push a coefficient into the matrix(current location)
	 * @param value*/
	void pushCoefficient(double value);
	/** Shift to the next column*/
	void pushColumn();
	/** Finish generate the left hand side*/
	void finalizeLHS();
	/**Solve the matrix, results will be located in array b
	 * @param b the array of right hand side values*/
	void solve(double[] b);	
	/**Select a column that is going to be updated, this method MUST NOT be invoked before finalizeLHS()*/
	void selectColumn(int column);
	/**Update the value of an EXISTING cell, this method MUST NOT be invoked before selectColumn()*/
	void setCell(double value);
}
