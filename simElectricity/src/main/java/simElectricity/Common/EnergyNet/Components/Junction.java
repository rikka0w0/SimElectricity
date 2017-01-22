package simElectricity.Common.EnergyNet.Components;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.DataProvider.ISEJunctionData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class Junction extends SEComponent implements ISESubComponent{
	public ISEJunctionData data;
	
	public Junction(ISEJunctionData _data, TileEntity _te){
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
