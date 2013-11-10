package simElectricity.simulatorFloat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulatorFloatTest {

	public static void main(String[] args) {
		List<Node> unknownVoltageNodes = new ArrayList<Node>();
		List<Map<Node, Float>> resToOtherNodes = new ArrayList<Map<Node, Float>>();

		Node v0 = new Node(12, false);
		Node gnd = new Node(0, false);		
		Node e1 = new Node(0, true);
		Map<Node, Float> e1Res = new HashMap<Node, Float>();
		Node e2 = new Node(0, true);
		Map<Node, Float> e2Res = new HashMap<Node, Float>();
		
		e1Res.put(v0, (float) 500.0);
		e1Res.put(gnd, (float) 10e3);
		e1Res.put(e2, (float) 50.0);
		unknownVoltageNodes.add(e1);
		resToOtherNodes.add(e1Res);
				
		e2Res.put(e1, (float) 50.0);
		e2Res.put(gnd, (float) 10e3);
		e2Res.put(gnd, (float) 10e3);
		unknownVoltageNodes.add(e2);
		resToOtherNodes.add(e2Res);
		
		float[] b = Simulator.runSimulator(unknownVoltageNodes, resToOtherNodes);
		
//		10.9116
//		10.8573
		for (float d : b) {
			System.out.println(d);
		}
	}

}
