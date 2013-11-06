package simE.components;

import simE.components.Node.NodeType;

public class Resistance extends BaseComponet implements IComponet {
	double ohm;
	double siemens;
	ResistanceType type;
	
	public ResistanceType getType() {
		return type;
	}

	public void setType(ResistanceType type) {
		this.type = type;
	}

	public class NodeWithOhm{
		public Node node;
		public double ohm;
	}
	
	public enum ResistanceType {
		CABLE, LOAD
	}

	public double getConductance() {
		return siemens;
	}

	public double getResistance() {
		return ohm;
	}

	public Resistance(double ohm, ResistanceType type) {
		super();
		this.ohm = ohm;
		this.siemens = 1 / this.ohm;
		this.type = type;
	}
	
	public NodeWithOhm getOtherRealNode(Node thisNode){
		NodeWithOhm result = new NodeWithOhm();
		result.ohm = this.ohm;
		
		for (Node nextNode : pins) {
			if(nextNode != thisNode)
			{
				result.node = nextNode;
				break;
			}
		}
		
		if(result.node.getType() == NodeType.FAKE){
			Resistance nextResistance = (Resistance)result.node.getFirstOtherComponet(this);
			NodeWithOhm temp = nextResistance.getOtherRealNode(result.node);
			result.node = temp.node;
			result.ohm += temp.ohm;
		}
		
		return result;		
	}
}
