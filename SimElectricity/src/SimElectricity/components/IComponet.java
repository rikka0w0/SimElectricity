package SimElectricity.components;

public interface IComponet {
	Node getNode(int pin);
	Node setNode(int index, Node pin);
	boolean addNode(Node node);
}
