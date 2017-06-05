package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.node.ISESubComponent;

public class TransformerSecondary extends SEComponent implements ISESubComponent{
	public TransformerPrimary primary;
	
	public TransformerSecondary(TransformerPrimary primary, TileEntity te){
		this.primary = primary;
		this.te = te;
	}
	
	@Override
	public ISESubComponent getComplement() {
		return primary;
	}
}
