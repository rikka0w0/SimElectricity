package simE.components;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseComponet implements IComponet {

	List<Node> pins = new ArrayList<Node>();
	
	@Override
	public Node getNode(int pin) {
		return pins.get(pin);
	}

	@Override
	public Node setNode(int index, Node pin) {
		return pins.set(index, pin);
	}

	@Override
	public boolean addNode(Node node) {		
		return pins.add(node);
	}

}
