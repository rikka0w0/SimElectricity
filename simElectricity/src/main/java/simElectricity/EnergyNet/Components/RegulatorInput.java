package simElectricity.EnergyNet.Components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.API.DataProvider.ISERegulatorData;
import simElectricity.API.EnergyTile.ISESubComponent;

public class RegulatorInput extends SEComponent.Tile<ISERegulatorData> implements ISESubComponent{
	public double Vref, Ro, Dmax, Rc, A, Rs, Rdummy;
	public RegulatorOutput output;
	public RegulatorController controller;
	
	public RegulatorInput(ISERegulatorData dataProvider, TileEntity te){
		super(dataProvider, te);
		output = new RegulatorOutput(this, te);
		controller = new RegulatorController(this, te);
	}

	@Override
	public ISESubComponent getComplement() {
		return output;
	}

	@Override
	public void updateComponentParameters() {
		this.Vref = dataProvider.getRegulatedVoltage();
		this.Ro = dataProvider.getOutputResistance();
		this.Dmax = dataProvider.getDMax();
		this.Rc = dataProvider.getRc();
		this.A = dataProvider.getGain();
		this.Rs = dataProvider.getRs();
		this.Rdummy = dataProvider.getRDummyLoad();
		
	}
}
