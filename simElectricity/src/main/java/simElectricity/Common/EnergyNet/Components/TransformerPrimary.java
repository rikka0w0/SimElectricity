package simElectricity.Common.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.DataProvider.ISETransformerData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class TransformerPrimary extends SEComponent implements ISESubComponent{	
	public TransformerSecondary secondary;
	public ISETransformerData data;
	
	public TransformerPrimary(ISETransformerData _data, TileEntity _te){
		secondary = new TransformerSecondary(this, _te);
		data = _data;
		te = _te;
	}

	@Override
	public ISEComponentDataProvider getDataProvider() {
		return data;
	}	

	@Override
	public ISESubComponent getComplement() {
		return secondary;
	}
}
