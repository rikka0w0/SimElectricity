package simElectricity.simulator;

import java.util.List;

public class SimulatorFloatTest {

	public static void main(String[] args) {
		// List<Node> unknownVoltageNodes = new ArrayList<Node>();
		Simulator table = new Simulator();

		Node v0 = table.newNode(12);
		Node gnd = table.newNode(0);
		Node e1 = table.newNode();
		Node e2 = table.newNode();
		// Node e3 = new Node();

		table.connect(e1, v0, 500);
		table.connect(e2, e1, 50);
		table.connect(e1, gnd, (float) 10e3);
		table.connect(e2, gnd, (float) 10e3);
		// gnd = new Node(0);
		// e2.connect(gnd, (float) 10e3);

		// unknownVoltageNodes.add(e1);
		// unknownVoltageNodes.add(e2);
		// unknownVoltageNodes.add(e3);

		List<Node> b = table.run();

		// 10.9116
		// 10.8573
		for (Node d : b) {
			System.out.println(d.voltage);
		}
	}

}
