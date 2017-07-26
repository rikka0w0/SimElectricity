package simelectricity.essential.extensions.buildcraft;

import buildcraft.api.facades.IFacadeItem;
import buildcraft.api.transport.pluggable.IFacadePluggable;
import buildcraft.api.transport.pluggable.IPipePluggableItem;
import buildcraft.api.transport.pluggable.PipePluggable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISECoverPanelFactory;

public class BCCoverFactory implements ISECoverPanelFactory{
	@Override
	public boolean acceptItemStack(ItemStack itemStack) {
		Item item = itemStack.getItem();
		
		return (item instanceof IFacadeItem) && (item instanceof IPipePluggableItem);
	}
	
	/**
	 * Return null if itemStack is not a valid BuildCraft facade
	 */
	@Override
	public ISECoverPanel fromItemStack(ItemStack itemStack) {
        Item item = itemStack.getItem();
        
        PipePluggable pipePluggable;
        try{
        	pipePluggable = ((IPipePluggableItem) item).createPipePluggable(null, null, itemStack);
        }catch(Exception e){
        	return null;
        }
        
        if (!(pipePluggable instanceof IFacadePluggable))
        	return null;
        
        IFacadePluggable facade = (IFacadePluggable) pipePluggable;
        
        ItemStack newStack = itemStack.copy();
        newStack.stackSize = 1;
        
        return new BCFacadePanel(
        		facade.isHollow(),
        		facade.getCurrentMetadata(),
        		facade.getCurrentBlock(),
        		newStack);
	}

	@Override
	public boolean acceptNBT(NBTTagCompound nbt) {
		String coverPanelType = nbt.getString("coverPanelType");
		return coverPanelType.equals("BCFacade");
	}
	
	@Override
	public ISECoverPanel fromNBT(NBTTagCompound nbt) {
		BCFacadePanel facade = new BCFacadePanel(nbt);
		Block block = facade.getBlock();
		
		if (block == null || block == Blocks.air)
			return null;
		
		return facade;
	}

	@Override
	public boolean acceptCoverPanel(ISECoverPanel coverPanel) {
		return coverPanel instanceof BCFacadePanel;
	}

	@Override
	public ItemStack getItemStack(ISECoverPanel coverPanel) {
		BCFacadePanel facade = (BCFacadePanel) coverPanel;
		
		return facade.getItem();
	}
}
