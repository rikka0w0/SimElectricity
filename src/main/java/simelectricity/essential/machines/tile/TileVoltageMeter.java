package simelectricity.essential.machines.tile;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.SESinglePortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;

public class TileVoltageMeter extends SESinglePortMachine implements ISEVoltageSource, IEnergyNetUpdateHandler, ISESocketProvider{
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

	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(EnumFacing side) {
		return side == this.functionalSide ? 0 : -1;
	}
}
