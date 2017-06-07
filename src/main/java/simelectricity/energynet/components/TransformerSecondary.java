package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISETransformerData;
import simelectricity.api.node.ISESubComponent;

public class TransformerSecondary extends SEComponent implements ISESubComponent, ISETransformerData{
	public TransformerPrimary primary;
	
	public TransformerSecondary(TransformerPrimary primary, TileEntity te){
		this.primary = primary;
		this.te = te;
	}
	
	@Override
	public ISESubComponent getComplement() {
		return primary;
	}

	@Override
	public double getRatio() {
		return primary.ratio;
	}

	@Override
	public double getInternalResistance() {
		return primary.rsec;
	}
}
