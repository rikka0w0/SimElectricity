package simElectricity.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.WeightedMultigraph;

public class SimulatorTable {
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

		float[] x = Simulator.lsolve(A, b);
		for (int i = 0; i < x.length; i++) {
			unknownVoltageNodes.get(i).voltage = x[i];
		}

		return unknownVoltageNodes;
	}

}
