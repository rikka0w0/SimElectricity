package simElectricity.Common.EnergyNet;

import simElectricity.Common.SEUtils;

//Ax = b
public interface IMatrixResolver {
    /**
     * Initialize to a size*size square matrix
     *
     * @param size Matrix size
     */
    void newMatrix(int size);

    /**
     * Push a coefficient into the matrix(current location)
     */
    void pushCoefficient(double value);

    /**
     * Shift to the next column
     */
    void pushColumn();

    /**
     * Finish generate the left hand side
     */
    void finalizeLHS();

    /**
     * Solve the matrix, results will be located in array b
     *
     * @param b the array of right hand side values
     */
    boolean solve(double[] b);

    /**
     * Select a column that is going to be updated, this method MUST NOT be invoked before finalizeLHS()
     */
    void selectColumn(int column);

    /**
     * Update the value of an EXISTING cell, this method MUST NOT be invoked before selectColumn()
     */
    void setCell(double value);

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
