package simElectricity.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;

import simElectricity.API.DataProvider.ISEDiodeData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class DiodeInput extends SEComponent.Tile<ISEDiodeData> implements ISESubComponent{
	public double Rs, Is, Vt;
	public DiodeOutput output;
	
	public DiodeInput(ISEDiodeData dataProvider, TileEntity te){
		super(dataProvider, te);
		output = new DiodeOutput(this, te);
	}

	@Override
	public ISESubComponent getComplement() {
		return output;
	}

	@Override
	public void updateComponentParameters() {
		this.Rs = dataProvider.getForwardResistance();
		this.Is = dataProvider.getSaturationCurrent();
		this.Vt = dataProvider.getThermalVoltage();
	}
}
