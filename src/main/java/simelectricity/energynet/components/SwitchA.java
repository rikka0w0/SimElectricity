package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISESwitch;
import simelectricity.api.node.ISESubComponent;

public class SwitchA extends SEComponent.Tile<ISESwitch> implements ISESubComponent, ISESwitch{
	public boolean isOn;
	public double resistance;
	
	public SwitchB B;
	
	public SwitchA(ISESwitch dataProvider, TileEntity te) {
		super(dataProvider, te);
		this.B = new SwitchB(this, te);
	}

	@Override
	public ISESubComponent getComplement() {
		return B;
	}

	@Override
	public void updateComponentParameters() {
		this.isOn = dataProvider.isOn();
		this.resistance = dataProvider.getResistance();
	}

	@Override
	public boolean isOn() {
		return isOn;
	}

	@Override
	public double getResistance() {
		return resistance;
	}
}
