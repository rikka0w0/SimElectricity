package simElectricity.Common.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.DataProvider.ISEComponentDataProvider;
import simElectricity.API.EnergyTile.ISESubComponent;

public class DiodeOutput extends SEComponent implements ISESubComponent{
	public DiodeInput input;
	
	public DiodeOutput(DiodeInput _input, TileEntity _te){
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
