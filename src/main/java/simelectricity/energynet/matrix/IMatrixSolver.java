package simelectricity.energynet.matrix;

import simelectricity.common.SELogger;

/**
 * A class implementing this interface is designed to solve Ax=b, <p>
 * where x and b are vectors, A is a square matrix.
 */
public interface IMatrixSolver {
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
     *
     * @param b is acting as both input and output, as an input parameter, b is the right hand side vector, after this function is executed, b will have the same content as x
     * @return Return true if no error occurs during solving
     */
    boolean solve(double[] b);

    /**
     * Print the matrix, must be called after finishEditing()
     */
    void print(String[] header);

    /**
     * Return the number of non-zero elements with in the matrix
     */
    int getTotalNonZeros();

    /**
     * Return the number of rows/columns of the matrix
     */
    int getMatrixSize();

    public static IMatrixSolver newSolver(String name) {
        try {
            return (IMatrixSolver) Class.forName("simelectricity.energynet.matrix." + name).getConstructor().newInstance();
        } catch (Exception e) {
            SELogger.logFatal(SELogger.simulator, "Invalid Matrix Solver! Please check your config settings!");
            e.printStackTrace();
            return null;
        }
    }
}
