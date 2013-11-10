package simElectricity.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulatorTest {

	public static void main(String[] args) {
		List<Node> unknownVoltageNodes = new ArrayList<Node>();
		List<Map<Node, Double>> resToOtherNodes = new ArrayList<Map<Node, Double>>();

		Node v0 = new Node(12, false);
		Node gnd = new Node(0, false);		
		Node e1 = new Node(0, true);
		Map<Node, Double> e1Res = new HashMap<Node, Double>();
		Node e2 = new Node(0, true);
		Map<Node, Double> e2Res = new HashMap<Node, Double>();
		
		e1Res.put(v0, 500.0);
		e1Res.put(gnd, 10e3);
		e1Res.put(e2, 50.0);
		unknownVoltageNodes.add(e1);
		resToOtherNodes.add(e1Res);
				
		e2Res.put(e1, 50.0);
		e2Res.put(gnd, 10e3);
		e2Res.put(gnd, 10e3);
		unknownVoltageNodes.add(e2);
		resToOtherNodes.add(e2Res);
		
		double[] b = Simulator.runSimulator(unknownVoltageNodes, resToOtherNodes);
		
//		10.9116
//		10.8573
		for (double d : b) {
			System.out.println(d);
		}
	}

}
