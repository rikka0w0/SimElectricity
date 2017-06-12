package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;

import simelectricity.api.components.ISEDiode;
import simelectricity.api.node.ISESubComponent;

public class DiodeInput extends SEComponent.Tile<ISEDiode> implements ISESubComponent, ISEDiode{
	public double Rs, Is, Vt;
	public DiodeOutput output;
	
	public DiodeInput(ISEDiode dataProvider, TileEntity te){
		super(dataProvider, te);
		output = new DiodeOutput(this, te);
	}

	@Override
	public ISESubComponent getComplement() {
		return output;
	}

	@Override
	public void updateComponentParameters() {
		this.Rs = dataProvider.getForwardResistance();
		this.Is = dataProvider.getSaturationCurrent();
		this.Vt = dataProvider.getThermalVoltage();
	}

	@Override
	public double getForwardResistance() {
		return Rs;
	}

	@Override
	public double getSaturationCurrent() {
		return Is;
	}

	@Override
	public double getThermalVoltage() {
		return Vt;
	}
}
