package simElectricity.Blocks;

import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerElectricFurnace extends Container{
	protected TileElectricFurnace tileEntity;
	protected int progress;
	
	public ContainerElectricFurnace (InventoryPlayer inventoryPlayer, TileElectricFurnace te){
        tileEntity = te;
        
        addSlotToContainer(new Slot(tileEntity, 0, 43, 33));
        addSlotToContainer(new Slot(tileEntity, 1, 103, 34){public boolean isItemValid(ItemStack par1ItemStack){return false;}});
        
        bindPlayerInventory(inventoryPlayer);
	}
    
    @Override
    public void addCraftingToCrafters(ICrafting par1iCrafting){
        super.addCraftingToCrafters(par1iCrafting);
      	par1iCrafting.sendProgressBarUpdate(this, 0, tileEntity.progress);     
    }
    
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2){
    	if (par1 == 0)	tileEntity.progress = par2;
   	}
    
    @Override
    public void detectAndSendChanges(){
    	super.detectAndSendChanges();
    	Iterator var1 = this.crafters.iterator();
    	while (var1.hasNext())
    	{
    		ICrafting var2 = (ICrafting)var1.next();
            	var2.sendProgressBarUpdate(this, 0, progress);          	
    	}
    	
    	progress=tileEntity.progress;
    }
	
	//Common Stuff
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
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
            if (slot < 2) {
            	if (!this.mergeItemStack(stackInSlot, 2, 38, true))
            		return null;
            }
            //places it into the tileEntity is possible since its in the player inventory
            else if (!this.mergeItemStack(stackInSlot, 0,1, false))
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