package simElectricity.simulator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import simElectricity.API.BaseComponent;

/*************************************************************************
 * Compilation: javac GaussianElimination.java Execution: java
 * GaussianElimination
 * 
 * Gaussian elimination with partial pivoting.
 * 
 * % java GaussianElimination -1.0 2.0 2.0
 * 
 *************************************************************************/

public class Simulator {
	private static final double EPSILON = 1e-10;

	// Gaussian elimination with partial pivoting
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
			if (Math.abs(A[p][p]) <= EPSILON) {
				throw new RuntimeException(
						"Matrix is singular or nearly singular");
			}

			// pivot within A and b
			for (int i = p + 1; i < N; i++) {
				double alpha = A[i][p] / A[p][p];
				b[i] -= alpha * b[p];
				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}

		// back substitution
		double[] x = new double[N];
		for (int i = N - 1; i >= 0; i--) {
			double sum = 0.0;
			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}
			x[i] = (b[i] - sum) / A[i][i];
		}
		return x;
	}

	public static double[] runSimulator(List<Node> unknownVoltageNodes,
			List<Map<Node, Double>> resToOtherNodes) {

		int matrixSize = unknownVoltageNodes.size();
		double[][] A = new double[matrixSize][matrixSize];
		double[] b = new double[matrixSize];

		for (int i = 0; i < matrixSize; i++) {
			Map<Node, Double> tmpRes = resToOtherNodes.get(i);
			
			for (int j = 0; j < matrixSize; j++) {
				double tmp = 0;

				if (i == j) {
					for (Entry<Node, Double> entry : tmpRes.entrySet())
						tmp += 1.0 / entry.getValue();
				} else if(tmpRes.containsKey(unknownVoltageNodes.get(j))) {
					tmp = -1.0 / tmpRes.get(unknownVoltageNodes.get(j));
				}

				A[i][j] = tmp;
			}
			
			b[i] = 0;
			for (Entry<Node, Double> entry : tmpRes.entrySet())
				if(entry.getKey().isUnknownVoltage() != true)
					if(entry.getKey().getVoltage() != 0)
						b[i] = (1.0 / entry.getValue()) * entry.getKey().getVoltage();
		}

		return lsolve(A, b);
	}
}