package simelectricity.energynet.matrix;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorOperators;

public class Gaussian implements IMatrixSolver {
    public static final double EPSILON = 1e-10;
    
    // Select the optimal SIMD width for the current hardware architecture (e.g., 256-bit or 512-bit)
    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;

    private double[][] matrix;
    private int nZ;
    private int size;

    // Gaussian elimination with partial pivoting and SIMD acceleration via Java Vector API
    public static double[] lsolve(double[][] A, double[] b) {
        int N = b.length;

        for (int p = 0; p < N; p++) {

            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            double[] temp = A[p];
            A[p] = A[max];
            A[max] = temp;
            double t = b[p];
            b[p] = b[max];
            b[max] = t;

            // singular or nearly singular
            if (Math.abs(A[p][p]) <= Gaussian.EPSILON) {
                return null;
            }

            // pivot within A and b
            for (int i = p + 1; i < N; i++) {
                if (A[p][p] != 0) {//Ignore any line with all zero
                    double alpha = A[i][p] / A[p][p];
                    b[i] -= alpha * b[p];
                    
                    int j = p;
                    // Hardware accelerated FMA (Fused Multiply-Add) via Vector API
                    int upperBound = SPECIES.loopBound(N - p) + p;
                    for (; j < upperBound; j += SPECIES.length()) {
                        DoubleVector vRowP = DoubleVector.fromArray(SPECIES, A[p], j);
                        DoubleVector vRowI = DoubleVector.fromArray(SPECIES, A[i], j);
                        vRowI.sub(vRowP.mul(alpha)).intoArray(A[i], j);
                    }
                    // Scalar post-loop for the remainder
                    for (; j < N; j++) {
                        A[i][j] -= alpha * A[p][j];
                    }
                }
            }
        }

        // back substitution
        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            if (A[i][i] != 0) {//Ignore any line with all zero
                double sum = 0.0;
                
                int j = i + 1;
                // Hardware accelerated reduction via Vector API
                int upperBound = SPECIES.loopBound(N - (i + 1)) + (i + 1);
                
                DoubleVector vSum = DoubleVector.zero(SPECIES);
                for (; j < upperBound; j += SPECIES.length()) {
                    DoubleVector vA = DoubleVector.fromArray(SPECIES, A[i], j);
                    DoubleVector vX = DoubleVector.fromArray(SPECIES, x, j);
                    vSum = vSum.add(vA.mul(vX));
                }
                sum += vSum.reduceLanes(VectorOperators.ADD);
                
                // Scalar remainder
                for (; j < N; j++) {
                    sum += A[i][j] * x[j];
                }
                
                x[i] = (b[i] - sum) / A[i][i];
            }
        }
        return x;
    }

    @Override
    public void newMatrix(int size) {
        this.size = size;
        this.matrix = new double[size][size];
        this.nZ = 0;
    }

    @Override
    public void setElementValue(int column, int row, double value) {
        this.matrix[column][row] = value;
    }

    @Override
    public void finishEditing() {
    }

    @Override
    public void print(String[] header) {
        for (int r = 0; r < this.size; r++) {
            for (int c = 0; c < this.size; c++) {
                System.out.print(this.matrix[c][r]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    @Override
    public boolean solve(double[] b) {
        double[][] A = new double[b.length][b.length];
        for (int i = 0; i < b.length; i++)
            for (int j = 0; j < b.length; j++)
                A[i][j] = this.matrix[i][j];

        double[] x = Gaussian.lsolve(A, b);
        if (x == null)
            return false;
        for (int i = 0; i < b.length; i++)
            b[i] = x[i];
        return true;
    }

    @Override
    public int getTotalNonZeros() {
        return this.nZ;
    }

    @Override
    public int getMatrixSize() {
        return this.size;
    }
}
