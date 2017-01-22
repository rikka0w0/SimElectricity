package simElectricity.Common.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESubComponent;

public class RegulatorController extends SEComponent implements ISESubComponent {
	public RegulatorInput input;
	
	public RegulatorController(RegulatorInput _input, TileEntity _te){
		input = _input;
		te = _te;
	}

	@Override
	public ISEComponentDataProvider getDataProvider() {
		return input.data;
	}
	

	@Override
	public ISESubComponent getComplement() {
		return input;
	}
}
