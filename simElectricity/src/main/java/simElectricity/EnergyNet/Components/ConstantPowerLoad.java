package simElectricity.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;

import simElectricity.API.DataProvider.ISEConstantPowerLoadData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class ConstantPowerLoad extends SEComponent.Tile<ISEConstantPowerLoadData> implements ISESubComponent{
	public double pRated, rMin, rMax;
	public boolean enabled;
	
	public ConstantPowerLoad(ISEConstantPowerLoadData dataProvider, TileEntity te){
		super(dataProvider, te);
	}

	@Override
	public void updateComponentParameters() {
		this.pRated = dataProvider.getRatedPower();
		this.rMin = dataProvider.getMinimumResistance();
		this.rMax = dataProvider.getMaximumResistance();
		this.enabled = dataProvider.isEnabled();
	}
}
