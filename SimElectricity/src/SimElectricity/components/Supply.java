package SimElectricity.components;

public class Supply extends BaseComponet implements IComponet {
	double voltage;

	public Supply(double voltage) {
		super();
		this.voltage = voltage;
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

}
