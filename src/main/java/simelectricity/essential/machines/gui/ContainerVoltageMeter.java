package simelectricity.essential.machines.gui;

import java.util.Iterator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import simelectricity.essential.common.ContainerNoInventory;
import simelectricity.essential.machines.tile.TileVoltageMeter;
import simelectricity.essential.utils.network.ISEContainerUpdate;
import simelectricity.essential.utils.network.MessageContainerSync;

public class ContainerVoltageMeter extends ContainerNoInventory<TileVoltageMeter> implements ISEContainerUpdate{
	public double voltage;
	
	public ContainerVoltageMeter(TileEntity tileEntity) {
		super(tileEntity);
	}

	@Override
	public void detectAndSendChanges() {
		double voltage = this.tileEntity.voltage;
		
		//Look for any changes
		if (this.voltage == voltage)
			return;
		
		this.voltage = voltage;
		
		//Send change to all crafter
    	Iterator<IContainerListener> iterator = this.listeners.iterator();
    	while (iterator.hasNext()) {
    		IContainerListener crafter = iterator.next();
    		
    		if (crafter instanceof EntityPlayerMP){
    			MessageContainerSync.sendToClient((EntityPlayerMP)crafter, voltage);
    		}
    	}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onDataArrivedFromServer(Object[] data) {
		voltage = (Double) data[0];
	}
}
