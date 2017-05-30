package simElectricity.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import simElectricity.API.EnergyTile.ISESubComponent;

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
