package simelectricity.essential.machines.tile;

import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISERegulator;
import simelectricity.essential.common.SETwoPortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;

public class TileVoltageRegulator extends SETwoPortMachine implements ISERegulator, IEnergyNetUpdateHandler, ISESocketProvider{
	public double inputVoltage, outputVoltage, outputCurrent, dutyCycle;
	
	/////////////////////////////////////////////////////////
	///ISERegulatorData
	/////////////////////////////////////////////////////////
	@Override
	public double getRegulatedVoltage() {return 12;}

	@Override
	public double getOutputResistance() {return 0.001;}
	
	@Override
	public double getDMax() {return 0.95;}

	@Override
	public double getRc() {return 0.01;}

	@Override
	public double getGain() {return 1e5;}

	@Override
	public double getRs() {return 1e6;}

	@Override
	public double getRDummyLoad() {return 1e6;}
	
    ///////////////////////////////////
    /// IEnergyNetUpdateHandler
    ///////////////////////////////////
	@Override
	public void onEnergyNetUpdate() {
		inputVoltage = SEAPI.energyNetAgent.getVoltage(this.input);
		outputVoltage = SEAPI.energyNetAgent.getVoltage(this.input.getComplement());
		dutyCycle = SEAPI.energyNetAgent.getVoltage(this.input.getComplement2());
		
		ISERegulator currentState = (ISERegulator) this.input;
		dutyCycle += currentState.getDMax();
		outputCurrent = (inputVoltage*dutyCycle - outputVoltage) / currentState.getOutputResistance();
	}
	
    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(ForgeDirection side) {
		if (side == inputSide)
			return 2;
		else if (side == outputSide)
			return 3;
		else 
			return -1;
	}
}
