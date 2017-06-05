package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.node.ISESubComponent;

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
