package simElectricity.simulator;

import java.util.Set;

import org.jgrapht.graph.WeightedMultigraph;

public class Node {

	WeightedMultigraph<Node, Resistor> graph;
	float voltage;
	boolean definedVoltage = false;

	public Node(WeightedMultigraph<Node, Resistor> graph) {
		super();
		this.graph = graph;
		this.graph.addVertex(this);
	}

	public Node(WeightedMultigraph<Node, Resistor> graph, float voltage) {
		super();
		this.graph = graph;
		this.graph.addVertex(this);

		this.voltage = voltage;
		this.definedVoltage = true;
	}

	public boolean connect(Node node, float resistance) {
		Resistor r = graph.addEdge(this, node);
		graph.setEdgeWeight(r, resistance);
		return r != null;
	}

	public boolean disconnect(Node node, float resistance) {
		Set<Resistor> rs = graph.getAllEdges(node, this);
		for (Resistor resistance2 : rs) {
			if (resistance == graph.getEdgeWeight(resistance2))
				return graph.removeEdge(resistance2);
		}
		return false;
	}
}
