package simElectricity.simulatorFloat;

import java.util.HashMap;
import java.util.Map;

public class Node {
	float voltage;
	boolean definedVoltage = false;
	Map<Node, Float> resToOtherNodes = new HashMap<Node, Float>();
	int connectToDefinedVoltageCount = 0;

	public Node() {
		super();
	}

	public Node(float voltage) {
		super();
		this.voltage = voltage;
		this.definedVoltage = true;
		this.connectToDefinedVoltageCount = 1;
	}

	public void connect(Node node, float resistance) {
		if (!resToOtherNodes.containsKey(node)) {
			boolean syncThis = (this.connectToDefinedVoltageCount > 0);
			boolean syncNode = (node.connectToDefinedVoltageCount > 0);
			if (syncThis)
				node.connectToDefinedVoltageCount++;
			if (syncNode)
				this.connectToDefinedVoltageCount++;
		}
		
		this.resToOtherNodes.put(node, resistance);
		node.resToOtherNodes.put(this, resistance);
	}

	public void disconnect(Node node) {
		node.resToOtherNodes.remove(this);
		this.resToOtherNodes.remove(node);

		boolean syncThis = (this.connectToDefinedVoltageCount > 0);
		boolean syncNode = (node.connectToDefinedVoltageCount > 0);
		if (syncThis)
			node.connectToDefinedVoltageCount--;
		if (syncNode)
			this.connectToDefinedVoltageCount--;
	}
}
