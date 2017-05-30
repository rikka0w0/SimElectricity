package simElectricity.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.DataProvider.ISETransformerData;
import simElectricity.API.EnergyTile.ISESubComponent;

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
