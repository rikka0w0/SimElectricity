package simElectricity.Common.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.DataProvider.ISERegulatorData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class RegulatorInput extends SEComponent implements ISESubComponent{
	public ISERegulatorData data;
	public RegulatorOutput output;
	public RegulatorController controller;
	
	public RegulatorInput(ISERegulatorData _data, TileEntity _te){
		output = new RegulatorOutput(this, _te);
		controller = new RegulatorController(this, _te);
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
