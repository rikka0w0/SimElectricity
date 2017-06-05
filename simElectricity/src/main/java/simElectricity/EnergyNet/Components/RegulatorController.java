package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.node.ISESubComponent;

public class RegulatorController extends SEComponent implements ISESubComponent {
	public RegulatorInput input;
	
	public RegulatorController(RegulatorInput input, TileEntity te){
		this.input = input;
		this.te = te;
	}

	@Override
	public ISESubComponent getComplement() {
		return input;
	}
}
