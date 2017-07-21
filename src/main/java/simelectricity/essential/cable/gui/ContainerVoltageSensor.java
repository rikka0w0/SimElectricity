package simelectricity.essential.cable.gui;

import simelectricity.essential.cable.VoltageSensorPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerVoltageSensor extends Container {
	public ContainerVoltageSensor(VoltageSensorPanel panel, TileEntity te){
		
	}
	
	@Override
	public ItemStack slotClick(int p_75144_1_, int p_75144_2_, int p_75144_3_, EntityPlayer p_75144_4_){
		return null;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}
