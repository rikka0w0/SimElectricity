package simelectricity.essential.machines.tile;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEDiode;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SETwoPortMachine;

public class TileDiode extends SETwoPortMachine implements ISEDiode, IEnergyNetUpdateHandler, ISESocketProvider{
	public double inputVoltage, outputVoltage;
	
	/////////////////////////////////////////////////////////
	///ISEDiodeData
	/////////////////////////////////////////////////////////
	@Override
	public double getForwardResistance() {return 0.1;}

	@Override
	public double getSaturationCurrent() {return 1e-6;}

	@Override
	public double getThermalVoltage() {return 26e-6;}

    ///////////////////////////////////
    /// IEnergyNetUpdateHandler
    ///////////////////////////////////
	@Override
	public void onEnergyNetUpdate() {
		inputVoltage = SEAPI.energyNetAgent.getVoltage(this.input);
		outputVoltage = SEAPI.energyNetAgent.getVoltage(this.input.getComplement());
	}
	
    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(EnumFacing side) {
		if (side == inputSide)
			return 2;
		else if (side == outputSide)
			return 4;
		else 
			return -1;
	}
}
