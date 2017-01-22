package simElectricity.Common.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.DataProvider.ISEVoltageSourceData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class VoltageSource extends SEComponent implements ISESubComponent{
	public ISEVoltageSourceData data;
	
	public VoltageSource(ISEVoltageSourceData _data, TileEntity _te){
		data = _data;
		te = _te;
	}

	@Override
	public ISEComponentDataProvider getDataProvider() {
		return data;
	}
	
	@Override
	public ISESubComponent getComplement() {
		return null;
	}
}
