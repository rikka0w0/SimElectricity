package simelectricity.essential.blocks;

import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.SESinglePortMachine;

public class TileVoltageMeter extends SESinglePortMachine<ISEVoltageSource> implements ISEVoltageSource, IEnergyNetUpdateHandler{
    public double voltage = 0;
	
	@Override
    public double getResistance() {
        return 1e6F;
    }

    @Override
    public double getOutputVoltage() {
        return 0;
    }

	@Override
	public void onEnergyNetUpdate() {
		voltage = SEAPI.energyNetAgent.getVoltage(this.circuit);
	}
}
