package simElectricity.simulatorFloat;

public class Node {
	private float voltage;
	private boolean unknownVoltage;

	public Node(float voltage, boolean unknownVoltage) {
		super();
		this.voltage = voltage;
		this.unknownVoltage = unknownVoltage;
	}

	public float getVoltage() {
		return voltage;
	}

	public boolean isUnknownVoltage() {
		return unknownVoltage;
	}
}
