package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;

import simelectricity.api.node.ISESubComponent;

public class DiodeOutput extends SEComponent implements ISESubComponent{
	public DiodeInput input;
	
	public DiodeOutput(DiodeInput input, TileEntity te){
		this.input = input;
		this.te = te;
	}

	@Override
	public ISESubComponent getComplement() {
		return input;
	}
}
