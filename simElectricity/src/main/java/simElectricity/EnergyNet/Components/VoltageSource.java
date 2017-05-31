package simElectricity.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import simElectricity.API.DataProvider.ISEVoltageSourceData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class VoltageSource extends SEComponent.Tile<ISEVoltageSourceData> implements ISESubComponent{
	public double v, r;
	
	public VoltageSource(ISEVoltageSourceData dataProvider, TileEntity te){
		super(dataProvider, te);
	}

	@Override
	public void updateComponentParameters() {
		this.v = dataProvider.getOutputVoltage();
		this.r = dataProvider.getResistance();
	}
}
