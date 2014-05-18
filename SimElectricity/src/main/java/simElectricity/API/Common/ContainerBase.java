package simElectricity.API.Common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class ContainerBase extends Container{
	protected TileEntity tileEntity;
	
	public void init(){};
    public abstract int getPlayerInventoryStartIndex();
    public abstract int getPlayerInventoryEndIndex();
    public abstract int getTileInventoryStartIndex();
    public abstract int getTileInventoryEndIndex();
    
	public ContainerBase (InventoryPlayer inventoryPlayer, TileEntity te){
        tileEntity = te;
        
        init();
        
        bindPlayerInventory(inventoryPlayer);
	}
    
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

    public void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                        addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                                        8 + j * 18, 84 + i * 18));
                }
        }

        for (int i = 0; i < 9; i++) {
                addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
}
	
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
    	ItemStack stack = null;
    	Slot slotObject = (Slot) inventorySlots.get(slot);

    	//null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
        	ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            //merges the item into player inventory since its in the tileEntity
            if (slot < getPlayerInventoryStartIndex()) {
            	if (!this.mergeItemStack(stackInSlot, getPlayerInventoryStartIndex(), getPlayerInventoryEndIndex(), true))
            		return null;
            }
            //places it into the tileEntity is possible since its in the player inventory
            else if (!this.mergeItemStack(stackInSlot, getTileInventoryStartIndex(),getTileInventoryEndIndex(), false))
            	return null;
            

            if (stackInSlot.stackSize == 0)
            	slotObject.putStack(null);
            else
            	slotObject.onSlotChanged();

           	if (stackInSlot.stackSize == stack.stackSize)
                	return null;
                    
            slotObject.onPickupFromSlot(player, stackInSlot);
        }
        return stack;
    }
}
