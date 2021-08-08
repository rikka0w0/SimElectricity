package simelectricity.essential.common;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class ItemStackHandlerInventory implements Container {
	protected IItemHandlerModifiable itemHandler;
	
	public ItemStackHandlerInventory(IItemHandlerModifiable itemHandler) {
		this.itemHandler = itemHandler;
	}
	
	@Override
	public int getContainerSize() {
		return this.itemHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < getContainerSize(); i++)
			if (!this.getItem(i).isEmpty())
				return false;

		return true;
	}

	@Override
	public ItemStack getItem(int slotIndex) {
	    return this.itemHandler.getStackInSlot(slotIndex);
	}
	
	@Override
	public ItemStack removeItem(int slotIndex, int count) {
	    ItemStack itemStackInSlot = getItem(slotIndex);
	    if (itemStackInSlot.isEmpty()) return ItemStack.EMPTY;  //isEmpty(), EMPTY_ITEM
	
	    ItemStack itemStackRemoved;
	    if (itemStackInSlot.getCount() <= count) { //getStackSize
	        itemStackRemoved = itemStackInSlot;
	        setItem(slotIndex, ItemStack.EMPTY); // EMPTY_ITEM
	    } else {
	        itemStackRemoved = itemStackInSlot.split(count);
	        if (itemStackInSlot.getCount() == 0) //getStackSize
	            setItem(slotIndex, ItemStack.EMPTY); //EMPTY_ITEM
	    }
	    setChanged();
	    return itemStackRemoved;
	}

	/**
	 * This method removes the entire contents of the given slot and returns it.
	 * Used by containers such as crafting tables which return any items in their slots when you close the GUI
	 * @param slotIndex
	 * @return
	 */
	@Override
	public ItemStack removeItemNoUpdate(int slotIndex) {
	    ItemStack itemStack = getItem(slotIndex);
	    if (!itemStack.isEmpty())
	        setItem(slotIndex, ItemStack.EMPTY);  //isEmpty();  EMPTY_ITEM
	    return itemStack;
	}

	@Override
	public void setItem(int slotIndex, ItemStack itemstack) {
	    if (!itemstack.isEmpty() && itemstack.getCount() > getMaxStackSize())  // isEmpty();  getStackSize()
	        itemstack.setCount(getMaxStackSize());  //setStackSize()
	    this.itemHandler.setStackInSlot(slotIndex, itemstack);

//	    if (getStackInSlot(0).isEmpty() && itemstack.isEmpty())
//	    	onContentsChanged(slotIndex);
	
	    setChanged();
	}

	@Override
	public int getMaxStackSize() {
	    return this.itemHandler.getSlotLimit(0);	// This is slot sensitive in IItemHandler
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return this.itemHandler.isItemValid(slot, stack);
	}

	@Override
	public void clearContent() {
		for (int i=0; i<getContainerSize(); i++) {
			this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
}
