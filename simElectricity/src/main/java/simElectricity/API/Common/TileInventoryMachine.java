package simElectricity.API.Common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public abstract class TileInventoryMachine extends TileEntity implements ISidedInventory {
	protected ItemStack[] inv;	
	public void onInventoryChanged() {}     //Called when the content of the inventory changes
	public abstract int  getInventorySize();//Used to get the size of the inventory, should return a constant!
	
	//ISidedInventory - Override the following three function when necessary!
	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {return null;}
	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {return false;}
	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {return false;}
	
	//Initiallize
	public TileInventoryMachine(){
		inv = new ItemStack[getInventorySize()];
	}
	
	//NBT
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
    	super.readFromNBT(tagCompound);
    	    	
        NBTTagList tagList = tagCompound.getTagList("Inventory",Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                byte slot = tag.getByte("Slot");
                if (slot >= 0 && slot < inv.length) {
                        inv[slot] = ItemStack.loadItemStackFromNBT(tag);
                }
        }
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
    	super.writeToNBT(tagCompound);
    	
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < inv.length; i++) {
                ItemStack stack = inv[i];
                if (stack != null) {
                        NBTTagCompound tag = new NBTTagCompound();
                        tag.setByte("Slot", (byte) i);
                        stack.writeToNBT(tag);
                        itemList.appendTag(tag);
                }
        }
        tagCompound.setTag("Inventory", itemList);
    }
	//Inventory stuff--------------------------------------------------------------------------------

	@Override
	public int getSizeInventory() {return inv.length;}

	@Override
	public ItemStack getStackInSlot(int slot) {return inv[slot];}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
        	if (stack.stackSize <= amount) {
        		setInventorySlotContents(slot, null);
            } else {
            	stack = stack.splitStack(amount);
            	if (stack.stackSize == 0) {
            		setInventorySlotContents(slot, null);
            	}
            }
        }
        return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
    	ItemStack stack = getStackInSlot(slot);
    	if (stack != null) 
    		setInventorySlotContents(slot, null);
    	return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
        	stack.stackSize = getInventoryStackLimit();
        
        onInventoryChanged();
	}

	@Override
	public boolean hasCustomInventoryName() {return false;}

	@Override
	public int getInventoryStackLimit() {return 64;}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {return true;}
	
	@Override
	public String getInventoryName() {return this.toString();}
}
