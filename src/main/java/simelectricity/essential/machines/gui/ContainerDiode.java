package simelectricity.essential.machines.gui;

import java.util.Iterator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simelectricity.essential.common.ContainerNoInventoryTwoPort;
import simelectricity.essential.machines.tile.TileDiode;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

public class ContainerDiode extends ContainerNoInventoryTwoPort<TileDiode> implements ISEContainerUpdate{
	public double inputVoltage, outputVoltage;
	public EnumFacing inputSide, outputSide;
	
	@SideOnly(Side.CLIENT)
	public boolean forwardBiased;
	
	public ContainerDiode(TileEntity tileEntity) {
		super(tileEntity);
	}
	
	@Override
	public void detectAndSendChanges() {
		double inputVoltage = tileEntity.inputVoltage, outputVoltage = tileEntity.outputVoltage;
		EnumFacing inputSide = tileEntity.inputSide, outputSide = tileEntity.outputSide;
		
		//Look for any changes
		if (this.inputVoltage == inputVoltage &&
			this.outputVoltage == outputVoltage &&
			this.inputSide == inputSide &&
			this.outputSide == outputSide)
			return;

		this.inputVoltage = inputVoltage;
		this.outputVoltage = outputVoltage;
		this.inputSide = inputSide;
		this.outputSide = outputSide;
		
		//Send change to all crafter
    	Iterator<IContainerListener> iterator = this.listeners.iterator();
    	while (iterator.hasNext()) {
    		IContainerListener crafter = iterator.next();
    		
    		if (crafter instanceof EntityPlayerMP){
    			MessageContainerSync.sendToClient((EntityPlayerMP)crafter, inputVoltage, outputVoltage, inputSide, outputSide);
    		}
    	}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onDataArrivedFromServer(Object[] data) {
		this.inputVoltage = (Double) data[0];
		this.outputVoltage = (Double) data[1];
		this.inputSide = (EnumFacing) data[2];
		this.outputSide = (EnumFacing) data[3];
		
		this.forwardBiased = this.inputVoltage > this.outputVoltage;
	}
}
