package SimElectricity.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import SimElectricity.components.IComponet;
import SimElectricity.components.Node;
import SimElectricity.components.Node.NodeType;
import SimElectricity.components.Resistance;
import SimElectricity.components.Resistance.NodeWithOhm;
import SimElectricity.components.Resistance.ResistanceType;
import SimElectricity.components.Supply;

public class Grid extends ArrayList<Node> {

	Node ground;

	public Grid() {
		super();
		// add GND
		ground = new Node();
		ground.setType(NodeType.GROUND);
		this.add(ground);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 372314436128304411L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(Node arg0) {
		for (IComponet iComponet : arg0) {
			if (iComponet.getClass() == Resistance.class)
				if (((Resistance) iComponet).getType() == ResistanceType.CABLE)
					continue;
			ground.add(iComponet);
		}
		return super.add(arg0);
	}

	public double[][] getMatrix() {
		List<Node> fromNodes = new ArrayList<Node>();
		List<Map<Node, Double>> toNodesAndOhms = new ArrayList<Map<Node, Double>>();

		for (Node node : this) {
			// only NORMAL node need to calculate the voltage
			if (node.getType() != NodeType.NORMAL)
				continue;
			fromNodes.add(node);
			Map<Node, Double> no = new HashMap<Node, Double>();
			toNodesAndOhms.add(no);

			for (IComponet iComponet : node) {
//				if (iComponet.getClass() != Resistance.class)
//					continue;
//				if (((Resistance) iComponet).getType() != ResistanceType.CABLE)
//					continue;

				NodeWithOhm tempNo = ((Resistance) iComponet)
						.getOtherRealNode(node);
				no.put(tempNo.node, tempNo.ohm);
			}
		}

		double[][] result = new double[fromNodes.size()][fromNodes.size() + 1];

		for (int i = 0; i < fromNodes.size(); i++) {
//			Node fromNode = fromNodes.get(i);
			Map<Node, Double> toNodesMap = toNodesAndOhms.get(i);
			for (int j = 0; j < fromNodes.size(); j++) {
				if (i == j) {
					Iterator<Entry<Node, Double>> iter = toNodesMap.entrySet()
							.iterator();
					result[i][j] = 0;
					while (iter.hasNext()) {
						Entry<Node, Double> entry = iter.next();
						if(entry.getKey().getType() == NodeType.SUPPLY){
							result[i][fromNodes.size()] = entry.getKey().getVoltage() * (1.0 / entry.getValue());
						}
						result[i][j] = result[i][j] + (1.0 / entry.getValue());
					}
				} else {
					if(toNodesMap.get(fromNodes.get(j)) == null)
						result[i][j] = 0;
					else
						result[i][j] = -1.0 * (1.0 / toNodesMap.get(fromNodes.get(j)));
				}
			}
		}

		return result;
	}
}
