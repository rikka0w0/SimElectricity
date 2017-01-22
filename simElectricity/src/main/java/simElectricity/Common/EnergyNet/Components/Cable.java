package simElectricity.Common.EnergyNet.Components;

import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;

import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISECableTile;

public class Cable extends SEComponent{
	public ISECableTile data;
	
	public Cable(ISECableTile _data, TileEntity _te){
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
