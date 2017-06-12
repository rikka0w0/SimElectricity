package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISETransformer;
import simelectricity.api.node.ISESubComponent;

public class TransformerPrimary extends SEComponent.Tile<ISETransformer> implements ISESubComponent, ISETransformer{	
	public double rsec, ratio;
	public TransformerSecondary secondary;
	
	public TransformerPrimary(ISETransformer dataProvider, TileEntity te){
		super(dataProvider, te);
		secondary = new TransformerSecondary(this, te);
	}
	
	@Override
	public ISESubComponent getComplement() {
		return secondary;
	}

	@Override
	public void updateComponentParameters() {
		this.rsec = dataProvider.getInternalResistance();
		this.ratio = dataProvider.getRatio();
	}
	
	@Override
	public double getRatio() {
		return ratio;
	}

	@Override
	public double getInternalResistance() {
		return rsec;
	}
}
