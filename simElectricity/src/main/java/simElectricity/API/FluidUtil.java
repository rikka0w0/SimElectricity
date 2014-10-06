package simElectricity.API;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidUtil {
	/**Get the empty container for the filled container*/
	public static ItemStack getEmptyContainer(ItemStack filled){
		for (FluidContainerRegistry.FluidContainerData dat:FluidContainerRegistry.getRegisteredFluidContainerData())
			if (dat.filledContainer.isItemEqual(filled))
				return dat.emptyContainer;
    	return null;
    }
	
	/**Return the fluid drain from a filled container if it's allowed*/
	public static FluidStack drainContainer(int maxVolume,FluidStack currentLiquid,ItemStack[] slots,int filledSlotID,int emptySlotID){
		FluidStack fluidInItem = FluidContainerRegistry.getFluidForFilledItem(slots[filledSlotID]);
		
		if(fluidInItem == null)
			return null;//Not a valid fluid containing item
		
    	
    	ItemStack emptyContainer = getEmptyContainer(slots[filledSlotID]);
    	if (emptyContainer == null)
    		return null;  //TODO : No empty Container, should we eat it? May be here's a bug
    	
    	if (slots[emptySlotID] != null)
    		//Different stack at the output 
    		if (!slots[emptySlotID].isItemEqual(emptyContainer)||
    		//Too many stack at the output
    		slots[emptySlotID].stackSize > slots[emptySlotID].getMaxStackSize()-emptyContainer.stackSize)
    			return null;
    	
    	
    	if (currentLiquid!=null){
    		if (!fluidInItem.isFluidEqual(currentLiquid))
    			return null; //Fluid type mismatch
    		if (fluidInItem.amount>maxVolume-currentLiquid.amount)
    			return null; //No enough space
    	}
    	
   
    	
    	//Make a output
    	if (slots[emptySlotID] == null){
    		emptyContainer.stackSize = 1;
    		slots[emptySlotID] = emptyContainer;
    	}
    	else
    		slots[emptySlotID].stackSize += 1;
    	
    	
    	slots[filledSlotID].stackSize -= 1; //Consume the filled stack
    	if (slots[filledSlotID].stackSize == 0)
    		slots[filledSlotID]=null;     //Remove empty stacks
    		

    	return fluidInItem;
    }
}
