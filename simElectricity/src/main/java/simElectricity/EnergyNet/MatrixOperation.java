package simElectricity.EnergyNet;

public class MatrixOperation {
    public static final double EPSILON = (double) 1e-10;

    // Gaussian elimination with partial pivoting
    public static float[] lsolve(double[][] A, double[] b) {
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
            if (Math.abs(A[p][p]) <= EPSILON) {
                //throw new RuntimeException(	"Matrix is singular or nearly singular");
            }

            // pivot within A and b
            for (int i = p + 1; i < N; i++) {
                if (A[p][p] != 0) {//Ignore any line with all zero
                    double alpha = A[i][p] / A[p][p];
                    b[i] -= alpha * b[p];
                    for (int j = p; j < N; j++) {
                        A[i][j] -= alpha * A[p][j];
                    }
                }
            }
        }

        // back substitution
        float[] x = new float[N];
        for (int i = N - 1; i >= 0; i--) {
            if (A[i][i] != 0) {//Ignore any line with all zero
                double sum = (double) 0.0;
                for (int j = i + 1; j < N; j++) {
                    sum += A[i][j] * x[j];
                }
                x[i] = (float) ((b[i] - sum) / A[i][i]);
            }
        }
        return x;
    }
}
