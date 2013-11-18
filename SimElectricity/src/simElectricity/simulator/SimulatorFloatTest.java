package simElectricity.simulator;

import java.util.List;

import org.jgrapht.graph.WeightedMultigraph;

public class SimulatorFloatTest {

	public static void main(String[] args) {
		// List<Node> unknownVoltageNodes = new ArrayList<Node>();
		WeightedMultigraph<Node, Resistor> graph = new WeightedMultigraph<Node, Resistor>(
				Resistor.class);

		Node v0 = new Node(graph, 12);
		Node gnd = new Node(graph, 0);
		Node e1 = new Node(graph);
		Node e2 = new Node(graph);
		// Node e3 = new Node();

		e1.connect(v0, 500);
		e1.connect(e2, 50);
		e1.connect(gnd, (float) 10e3);

		e2.connect(gnd, (float) 10e3);
		// gnd = new Node(0);
		// e2.connect(gnd, (float) 10e3);

		// unknownVoltageNodes.add(e1);
		// unknownVoltageNodes.add(e2);
		// unknownVoltageNodes.add(e3);

		List<Node> b = Simulator.runSimulator(graph);

		// 10.9116
		// 10.8573
		for (Node d : b) {
			System.out.println(d.voltage);
		}
	}

}
