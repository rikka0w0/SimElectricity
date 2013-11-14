package simElectricity.simulatorFloat;

import java.util.HashMap;
import java.util.Map;

public class Node {
	float voltage;
	boolean definedVoltage;
	Map<Node, Float> resToOtherNodes = new HashMap<Node, Float>();

	public Node() {
		super();
		this.definedVoltage = false;
	}

	public Node(float voltage) {
		super();
		this.voltage = voltage;
		this.definedVoltage = true;
	}

	public void connect(Node node, float resistance) {
		this.resToOtherNodes.put(node, resistance);
		node.resToOtherNodes.put(this, resistance);
	}

	public void disconnect(Node node) {
		node.resToOtherNodes.remove(this);
		this.resToOtherNodes.remove(node);
	}
}
