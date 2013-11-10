package simElectricity.simulator;

public class Node {
	private double voltage;
	private boolean unknownVoltage;

	public Node(double voltage, boolean unknownVoltage) {
		super();
		this.voltage = voltage;
		this.unknownVoltage = unknownVoltage;
	}

	public double getVoltage() {
		return voltage;
	}

	public boolean isUnknownVoltage() {
		return unknownVoltage;
	}
}
