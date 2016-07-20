package simElectricity.Common.EnergyNet;

import simElectricity.Common.SEUtils;

/**
 * A class that is designed to solve problem Ax=b
 * */
public interface IMatrixResolver {
    /**
     * Initialize and create a size*size square matrix
     *
     * @param size Matrix size
     */
    void newMatrix(int size);

    /**
     * Set the value of an element within the matrix, must be called after newMatrix(size)
     */
    void setElementValue(int column, int row, double value);

    /**
     * Must be called before solving or printing
     */
    void finishEditing();

    /**
     * Solve the matrix, results will be stored in array b
     * <p/>
     * This function solves: Ax = b, A is the matrix that has been generated and stored within this class, b is the right hand side of the matrix, a vector, x is a vector containing the result
     * @param b is acting as both input and output, as an input parameter, b is the right hand side vector, after this function is executed, b will have the same content as x
     * @return Return true if no error occurs during solving 
     */
    boolean solve(double[] b);

	/**
	 * Print the matrix, must be called after finishEditing()
	 * */
    public void print();
    
    /**
     * Return the number of non-zero elements with in the matrix
     */
    int getTotalNonZeros();

    public static class MatrixHelper {
        public static IMatrixResolver newResolver(String name) {
            try {
                return (IMatrixResolver) Class.forName("simElectricity.Common.EnergyNet." + name).newInstance();
            } catch (Exception e) {
                SEUtils.logFatal("Invalid Matrix Solver! Please check your config settings!");
                e.printStackTrace();
                return null;
            }
        }
    }
}
