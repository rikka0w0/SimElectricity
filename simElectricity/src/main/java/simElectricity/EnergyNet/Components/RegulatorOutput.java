package simElectricity.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import simElectricity.API.EnergyTile.ISESubComponent;

public class RegulatorOutput extends SEComponent implements ISESubComponent{
	public RegulatorInput input;
	
	public RegulatorOutput(RegulatorInput input, TileEntity te){
		this.input = input;
		this.te = te;
	}

	@Override
	public ISESubComponent getComplement() {
		return input;
	}
}
