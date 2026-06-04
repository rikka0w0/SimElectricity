package simelectricity.energynet.matrix;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;
import jdk.incubator.vector.VectorOperators;

public class Gaussian implements IMatrixSolver {
    public static final double EPSILON = 1e-10;
    
    // Select the optimal SIMD width for the current hardware architecture (e.g., 256-bit or 512-bit)
    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;

    private double[] matrix;
    private int nZ;
    private int size;

    // Gaussian elimination with partial pivoting and SIMD acceleration via Java Vector API
    public static double[] lsolve(double[] A, double[] b) {
        int N = b.length;

        for (int p = 0; p < N; p++) {
            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i * N + p]) > Math.abs(A[max * N + p])) {
                    max = i;
                }
            }
            
            int pOffset = p * N;
            int maxOffset = max * N;
            for (int k = 0; k < N; k++) {
                double temp = A[pOffset + k];
                A[pOffset + k] = A[maxOffset + k];
                A[maxOffset + k] = temp;
            }
            double t = b[p];
            b[p] = b[max];
            b[max] = t;

            // singular or nearly singular
            if (Math.abs(A[pOffset + p]) <= Gaussian.EPSILON) {
                return null;
            }

            // pivot within A and b
            for (int i = p + 1; i < N; i++) {
                int iOffset = i * N;
                if (A[pOffset + p] != 0) { //Ignore any line with all zero
                    double alpha = A[iOffset + p] / A[pOffset + p];
                    b[i] -= alpha * b[p];
                    
                    int j = p;
                    // Hardware accelerated FMA (Fused Multiply-Add) via Vector API
                    int upperBound = SPECIES.loopBound(N - p) + p;
                    for (; j < upperBound; j += SPECIES.length()) {
                        DoubleVector vRowP = DoubleVector.fromArray(SPECIES, A, pOffset + j);
                        DoubleVector vRowI = DoubleVector.fromArray(SPECIES, A, iOffset + j);
                        vRowI.sub(vRowP.mul(alpha)).intoArray(A, iOffset + j);
                    }
                    // Scalar post-loop for the remainder
                    for (; j < N; j++) {
                        A[iOffset + j] -= alpha * A[pOffset + j];
                    }
                }
            }
        }

        // back substitution
        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            int iOffset = i * N;
            if (A[iOffset + i] != 0) { //Ignore any line with all zero
                double sum = 0.0;
                
                int j = i + 1;
                // Hardware accelerated reduction via Vector API
                int upperBound = SPECIES.loopBound(N - (i + 1)) + (i + 1);
                
                DoubleVector vSum = DoubleVector.zero(SPECIES);
                for (; j < upperBound; j += SPECIES.length()) {
                    DoubleVector vA = DoubleVector.fromArray(SPECIES, A, iOffset + j);
                    DoubleVector vX = DoubleVector.fromArray(SPECIES, x, j);
                    vSum = vSum.add(vA.mul(vX));
                }
                sum += vSum.reduceLanes(VectorOperators.ADD);
                
                // Scalar remainder
                for (; j < N; j++) {
                    sum += A[iOffset + j] * x[j];
                }
                
                x[i] = (b[i] - sum) / A[iOffset + i];
            }
        }
        return x;
    }

    @Override
    public void newMatrix(int size) {
        this.size = size;
        this.matrix = new double[size * size];
        this.nZ = 0;
    }

    @Override
    public void setElementValue(int column, int row, double value) {
        this.matrix[column * this.size + row] = value;
    }

    @Override
    public void finishEditing() {
    }

    @Override
    public void print(String[] header) {
        for (int r = 0; r < this.size; r++) {
            for (int c = 0; c < this.size; c++) {
                System.out.print(this.matrix[c * this.size + r]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    @Override
    public boolean solve(double[] b) {
        int N = b.length;
        double[] A = new double[N * N];
        System.arraycopy(this.matrix, 0, A, 0, N * N);

        double[] x = Gaussian.lsolve(A, b);
        if (x == null)
            return false;
        System.arraycopy(x, 0, b, 0, N);
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
