package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.api.node.ISESubComponent;

public class VoltageSource extends SEComponent.Tile<ISEVoltageSource> implements ISESubComponent, ISEVoltageSource{
	public double v, r;
	
	public VoltageSource(ISEVoltageSource dataProvider, TileEntity te){
		super(dataProvider, te);
	}

	@Override
	public void updateComponentParameters() {
		this.v = dataProvider.getOutputVoltage();
		this.r = dataProvider.getResistance();
	}

	@Override
	public double getOutputVoltage() {
		return v;
	}

	@Override
	public double getResistance() {
		return r;
	}
	
	@Override
	public String toString() {
		return "V";
	}
}
