package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;

import simelectricity.api.components.ISEDiodeData;
import simelectricity.api.node.ISESubComponent;

public class DiodeOutput extends SEComponent implements ISESubComponent, ISEDiodeData{
	public DiodeInput input;
	
	public DiodeOutput(DiodeInput input, TileEntity te){
		this.input = input;
		this.te = te;
	}

	@Override
	public ISESubComponent getComplement() {
		return input;
	}
	
	@Override
	public double getForwardResistance() {
		return input.Rs;
	}

	@Override
	public double getSaturationCurrent() {
		return input.Is;
	}

	@Override
	public double getThermalVoltage() {
		return input.Vt;
	}
}
