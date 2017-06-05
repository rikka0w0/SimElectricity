package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.node.ISESubComponent;

public class SwitchB extends SEComponent implements ISESubComponent{
	public SwitchA A;
	
	public SwitchB (SwitchA A, TileEntity te){
		this.A = A;
		this.te = te;
	}
	
	@Override
	public ISESubComponent getComplement() {
		return A;
	}
}
