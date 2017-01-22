package simElectricity.Common.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESubComponent;

public class TransformerSecondary extends SEComponent implements ISESubComponent{
	public TransformerPrimary primary;
	
	public TransformerSecondary(TransformerPrimary _primary, TileEntity _te){
		primary = _primary;
		te = _te;
	}

	@Override
	public ISEComponentDataProvider getDataProvider() {
		return primary.data;
	}
	
	@Override
	public ISESubComponent getComplement() {
		return primary;
	}
}
