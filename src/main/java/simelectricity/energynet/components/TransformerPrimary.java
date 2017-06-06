package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISETransformerData;
import simelectricity.api.node.ISESubComponent;

public class TransformerPrimary extends SEComponent.Tile<ISETransformerData> implements ISESubComponent{	
	public double rsec, ratio;
	public TransformerSecondary secondary;
	
	public TransformerPrimary(ISETransformerData dataProvider, TileEntity te){
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
}
