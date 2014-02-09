package simElectricity.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.WeightedMultigraph;

public class Simulator {
	WeightedMultigraph<Node, Resistor> graph = new WeightedMultigraph<Node, Resistor>(
			Resistor.class);

	public Node newNode() {
		Node result = new Node(graph);
		return result;
	}

	public Node newNode(float voltage) {
		Node result = new Node(graph, voltage);
		return result;
	}

	public boolean connect(Node src, Node dst, float resistance) {
		if (resistance == 0)
			return false;
		Resistor r = graph.addEdge(src, dst);
		graph.setEdgeWeight(r, resistance);
		return r != null;
	}

	public boolean disconnect(Node src, Node dst, float resistance) {
		Set<Resistor> rs = graph.getAllEdges(src, dst);
		for (Resistor resistance2 : rs) {
			if (resistance == graph.getEdgeWeight(resistance2))
				return graph.removeEdge(resistance2);
		}
		return false;
	}

	private static final float EPSILON = (float) 1e-10;

	// Gaussian elimination with partial pivoting
	private static float[] lsolve(float[][] A, float[] b) {
		int N = b.length;

		for (int p = 0; p < N; p++) {

			// find pivot row and swap
			int max = p;
			for (int i = p + 1; i < N; i++) {
				if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
					max = i;
				}
			}
			float[] temp = A[p];
			A[p] = A[max];
			A[max] = temp;
			float t = b[p];
			b[p] = b[max];
			b[max] = t;

			// singular or nearly singular
			if (Math.abs(A[p][p]) <= EPSILON) {
				throw new RuntimeException(
						"Matrix is singular or nearly singular");
			}

			// pivot within A and b
			for (int i = p + 1; i < N; i++) {
				float alpha = A[i][p] / A[p][p];
				b[i] -= alpha * b[p];
				for (int j = p; j < N; j++) {
					A[i][j] -= alpha * A[p][j];
				}
			}
		}

		// back substitution
		float[] x = new float[N];
		for (int i = N - 1; i >= 0; i--) {
			float sum = (float) 0.0;
			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}
			x[i] = (b[i] - sum) / A[i][i];
		}
		return x;
	}

	public List<Node> run() {

		List<Node> unknownVoltageNodes = new ArrayList<Node>();
		for (Node node : graph.vertexSet()) {
			if ((node.definedVoltage != true) && (graph.degreeOf(node) >= 2))
				unknownVoltageNodes.add(node);
		}

		int matrixSize = unknownVoltageNodes.size();
		float[][] A = new float[matrixSize][matrixSize];
		float[] b = new float[matrixSize];

		for (int i = 0; i < matrixSize; i++) {
			List<Node> neighborList = Graphs.neighborListOf(graph,
					unknownVoltageNodes.get(i));

			for (int j = 0; j < matrixSize; j++) {
				float tmp = 0;

				if (i == j) {
					for (Node node : neighborList)
						for (Resistor res : graph.getAllEdges(node,
								unknownVoltageNodes.get(i)))
							tmp += 1 / graph.getEdgeWeight(res);
				} else {
					if (neighborList.contains(unknownVoltageNodes.get(j))) {
						for (Resistor res : graph.getAllEdges(
								unknownVoltageNodes.get(j),
								unknownVoltageNodes.get(i)))
							tmp += -1 / graph.getEdgeWeight(res);
					}
				}

				A[i][j] = tmp;
			}

			b[i] = 0;
			for (Node node : neighborList)
				if (node.definedVoltage == true) {
					float bg = 0;
					for (Resistor res : graph.getAllEdges(node,
							unknownVoltageNodes.get(i)))
						bg += 1 / graph.getEdgeWeight(res);
					b[i] += (bg * node.voltage);
				}
		}

		float[] x = lsolve(A, b);
		for (int i = 0; i < x.length; i++) {
			unknownVoltageNodes.get(i).voltage = x[i];
		}

		return unknownVoltageNodes;
	}

}
