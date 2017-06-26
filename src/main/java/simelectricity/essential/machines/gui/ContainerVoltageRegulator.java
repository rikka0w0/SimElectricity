package simelectricity.essential.machines.gui;

import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileVoltageRegulator;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.ISEDirectionSelectorEventHandler;
import simelectricity.essential.utils.network.MessageContainerSync;

public class ContainerVoltageRegulator extends ContainerNoInventoryTwoPort<TileVoltageRegulator> implements ISEContainerUpdate, ISEDirectionSelectorEventHandler{
	public double inputVoltage, outputVoltage, outputCurrent, dutyCycle;
	public ForgeDirection inputSide, outputSide;
	
	public ContainerVoltageRegulator(TileEntity tileEntity) {
		super(tileEntity);
	}

	@Override
	public void detectAndSendChanges() {
		double inputVoltage = tileEntity.inputVoltage, outputVoltage = tileEntity.outputVoltage;
		ForgeDirection inputSide = tileEntity.inputSide, outputSide = tileEntity.outputSide;
		double outputCurrent = this.tileEntity.outputCurrent, dutyCycle = tileEntity.dutyCycle;
		
		//Look for any changes
		if (this.inputVoltage == inputVoltage &&
				this.outputVoltage == outputVoltage &&
				this.inputSide == inputSide &&
				this.outputSide == outputSide &&
				this.outputCurrent == outputCurrent &&
				this.dutyCycle == dutyCycle)
			return;
		
		this.inputVoltage = inputVoltage;
		this.outputVoltage = outputVoltage;
		this.inputSide = inputSide;
		this.outputSide = outputSide;
		this.outputCurrent = outputCurrent;
		this.dutyCycle = dutyCycle;
		
		//Send change to all crafter
    	Iterator<ICrafting> iterator = this.crafters.iterator();
    	while (iterator.hasNext()) {
    		ICrafting crafter = iterator.next();
    		
    		if (crafter instanceof EntityPlayerMP){
    			MessageContainerSync.sendToClient((EntityPlayerMP)crafter, inputVoltage, outputVoltage, inputSide, outputSide, outputCurrent, dutyCycle);
    		}
    	}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void onDataArrivedFromServer(Object[] data) {
		this.inputVoltage = (Double) data[0];
		this.outputVoltage = (Double) data[1];
		this.inputSide = (ForgeDirection) data[2];
		this.outputSide = (ForgeDirection) data[3];
		this.outputCurrent = (Double) data[4];
		this.dutyCycle = (Double) data[5];
	}
}
