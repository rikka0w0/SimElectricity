package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISERegulatorData;
import simelectricity.api.node.ISESubComponent;

public class RegulatorOutput extends SEComponent implements ISESubComponent, ISERegulatorData{
	public RegulatorInput input;
	
	public RegulatorOutput(RegulatorInput input, TileEntity te){
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
