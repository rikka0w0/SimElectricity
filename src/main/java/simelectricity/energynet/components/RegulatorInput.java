package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISERegulator;
import simelectricity.api.node.ISESubComponent;

public class RegulatorInput extends SEComponent.Tile<ISERegulator> implements ISESubComponent, ISERegulator{
	public double Vref, Ro, Dmax, Rc, A, Rs, Rdummy;
	public RegulatorOutput output;
	public RegulatorController controller;
	
	public RegulatorInput(ISERegulator dataProvider, TileEntity te){
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

	@Override
	public double getRegulatedVoltage() {
		return Vref;
	}

	@Override
	public double getOutputResistance() {
		return Ro;
	}

	@Override
	public double getDMax() {
		return Dmax;
	}

	@Override
	public double getRc() {
		return Rc;
	}

	@Override
	public double getGain() {
		return A;
	}

	@Override
	public double getRs() {
		return Rs;
	}

	@Override
	public double getRDummyLoad() {
		return Rdummy;
	}
}
