package simElectricity.simulator;

import org.jgrapht.graph.WeightedMultigraph;

public class Node {

	float voltage = 0;
	boolean definedVoltage = false;

	public Node(WeightedMultigraph<Node, Resistor> graph) {
		graph.addVertex(this);
	}

	public Node(WeightedMultigraph<Node, Resistor> graph, float voltage) {
		graph.addVertex(this);
		this.voltage = voltage;
		this.definedVoltage = true;
	}

	public float getVoltage() {
		return voltage;
	}
}
