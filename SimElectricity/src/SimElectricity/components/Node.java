package SimElectricity.components;

import java.util.ArrayList;
import java.util.List;

import SimElectricity.components.Resistance.ResistanceType;

public class Node extends ArrayList<IComponet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5284346123639058210L;

	public enum NodeType {
		GROUND, NORMAL, SUPPLY, FAKE
	}

	NodeType type = NodeType.FAKE;
	double voltage;

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public List<IComponet> getOtherComponets(IComponet componet) {
		@SuppressWarnings("unchecked")
		List<IComponet> result = (List<IComponet>) this.clone();
		result.remove(componet);
		return result;
	}

	public IComponet getFirstOtherComponet(IComponet componet) {
		return getOtherComponets(componet).get(0);
	}

	/*
	 * check node is fake and add this node to componet's node list
	 */
	@Override
	public boolean add(IComponet e) {
		if (this.type == NodeType.GROUND) {
			;
		} else if (e.getClass() == Supply.class) {
			this.type = NodeType.SUPPLY;
			this.voltage = ((Supply)e).getVoltage();
		} else if (this.type == NodeType.FAKE) {
			if (e.getClass() == Resistance.class) {
				if (((Resistance) e).getType() != ResistanceType.CABLE) {
					type = NodeType.NORMAL;
				} else if (this.size() > 1) {
					type = NodeType.NORMAL;
				}
			} else
				type = NodeType.NORMAL;
		}
		
		e.addNode(this);
		return super.add(e);
	}
}
