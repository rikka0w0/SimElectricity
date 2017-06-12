package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISERegulator;
import simelectricity.api.node.ISESubComponent;

public class RegulatorController extends SEComponent implements ISESubComponent, ISERegulator{
	public RegulatorInput input;
	
	public RegulatorController(RegulatorInput input, TileEntity te){
		this.input = input;
		this.te = te;
	}

	@Override
	public ISESubComponent getComplement() {
		return input;
	}
	
	@Override
	public double getRegulatedVoltage() {
		return input.Vref;
	}

	@Override
	public double getOutputResistance() {
		return input.Ro;
	}

	@Override
	public double getDMax() {
		return input.Dmax;
	}

	@Override
	public double getRc() {
		return input.Rc;
	}

	@Override
	public double getGain() {
		return input.A;
	}

	@Override
	public double getRs() {
		return input.Rs;
	}

	@Override
	public double getRDummyLoad() {
		return input.Rdummy;
	}
}
