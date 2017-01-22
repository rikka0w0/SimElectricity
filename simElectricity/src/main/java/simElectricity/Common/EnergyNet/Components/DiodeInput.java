package simElectricity.Common.EnergyNet.Components;

import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.DataProvider.ISEDiodeData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class DiodeInput extends SEComponent implements ISESubComponent{
	public DiodeOutput output;
	public ISEDiodeData data;
	
	public DiodeInput(ISEDiodeData _data, TileEntity _te){
		output = new DiodeOutput(this, _te);
		data = _data;
		te = _te;
	}

	@Override
	public ISEComponentDataProvider getDataProvider() {
		return data;
	}

	@Override
	public ISESubComponent getComplement() {
		return output;
	}
}
