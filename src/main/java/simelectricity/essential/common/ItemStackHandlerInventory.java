package simelectricity.essential.common;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class ItemStackHandlerInventory implements IInventory {
	protected IItemHandlerModifiable itemHandler;
	
	public ItemStackHandlerInventory(IItemHandlerModifiable itemHandler) {
		this.itemHandler = itemHandler;
	}
	
	@Override
	public int getSizeInventory() {
		return this.itemHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < getSizeInventory(); i++)
			if (!this.getStackInSlot(i).isEmpty())
				return false;

		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
	    return this.itemHandler.getStackInSlot(slotIndex);
	}
	
	@Override
	public ItemStack decrStackSize(int slotIndex, int count) {
	    ItemStack itemStackInSlot = getStackInSlot(slotIndex);
	    if (itemStackInSlot.isEmpty()) return ItemStack.EMPTY;  //isEmpty(), EMPTY_ITEM
	
	    ItemStack itemStackRemoved;
	    if (itemStackInSlot.getCount() <= count) { //getStackSize
	        itemStackRemoved = itemStackInSlot;
	        setInventorySlotContents(slotIndex, ItemStack.EMPTY); // EMPTY_ITEM
	    } else {
	        itemStackRemoved = itemStackInSlot.split(count);
	        if (itemStackInSlot.getCount() == 0) //getStackSize
	            setInventorySlotContents(slotIndex, ItemStack.EMPTY); //EMPTY_ITEM
	    }
	    markDirty();
	    return itemStackRemoved;
	}

	/**
	 * This method removes the entire contents of the given slot and returns it.
	 * Used by containers such as crafting tables which return any items in their slots when you close the GUI
	 * @param slotIndex
	 * @return
	 */
	@Override
	public ItemStack removeStackFromSlot(int slotIndex) {
	    ItemStack itemStack = getStackInSlot(slotIndex);
	    if (!itemStack.isEmpty())
	        setInventorySlotContents(slotIndex, ItemStack.EMPTY);  //isEmpty();  EMPTY_ITEM
	    return itemStack;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
	    if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit())  // isEmpty();  getStackSize()
	        itemstack.setCount(getInventoryStackLimit());  //setStackSize()
	    this.itemHandler.setStackInSlot(slotIndex, itemstack);

//	    if (getStackInSlot(0).isEmpty() && itemstack.isEmpty())
//	    	onContentsChanged(slotIndex);
	
	    markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
	    return this.itemHandler.getSlotLimit(0);	// This is slot sensitive in IItemHandler
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return this.itemHandler.isItemValid(slot, stack);
	}

	@Override
	public void clear() {
		for (int i=0; i<getSizeInventory(); i++) {
			this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
}
