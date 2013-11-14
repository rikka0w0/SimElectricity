package simElectricity.simulatorFloat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class SimulatorFloatTest {

	public static void main(String[] args) {
		List<Node> unknownVoltageNodes = new ArrayList<Node>();
		

		Node v0 = new Node(12);
		Node gnd = new Node(0);		
		Node e1 = new Node();
		Node e2 = new Node();
//		Node e3 = new Node();

		e1.connect(v0, 500);
		e1.connect(e2, 50);
		e1.connect(gnd, (float) 10e3);
		
		e2.connect(gnd, (float) 10e3);
//		gnd = new Node(0);	
//		e2.connect(gnd, (float) 10e3);
		
		unknownVoltageNodes.add(e1);
		unknownVoltageNodes.add(e2);
//		unknownVoltageNodes.add(e3);
		
		float[] b = Simulator.runSimulator(unknownVoltageNodes);
		
//		10.9116
//		10.8573
		for (float d : b) {
			System.out.println(d);
		}
	}

}
