package simElectricity.Common.EnergyNet.Components;

import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;

import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.DataProvider.ISEConstantPowerLoadData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class ConstantPowerLoad extends SEComponent implements ISESubComponent{
	public ISEConstantPowerLoadData data;
	
	public ConstantPowerLoad(ISEConstantPowerLoadData _data, TileEntity _te){
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
