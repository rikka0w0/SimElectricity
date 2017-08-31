package rikka.librikka.tileentity;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class InventoryHelper {
	public static void stacksFromNBT(NBTTagCompound nbt, ItemStack[] itemStacks)	{
		final byte NBT_TYPE_COMPOUND = 10;       // See NBTBase.createNewByType() for a listing
		NBTTagList dataForAllSlots = nbt.getTagList("items", NBT_TYPE_COMPOUND);

		Arrays.fill(itemStacks, ItemStack.EMPTY);           // set all slots to empty EMPTY_ITEM
		for (int i = 0; i < dataForAllSlots.tagCount(); ++i) {
			NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
			int slotIndex = dataForOneSlot.getByte("slot") & 255;

			if (slotIndex >= 0 && slotIndex < itemStacks.length) {
				itemStacks[slotIndex] = new ItemStack(dataForOneSlot);
			}
		}
	}
	
	public static  void stacksToNBT(NBTTagCompound nbt, ItemStack[] itemStacks)	{
		NBTTagList dataForAllSlots = new NBTTagList();
		for (int i = 0; i < itemStacks.length; ++i) {
			if (!itemStacks[i].isEmpty())	{ //isEmpty()
				NBTTagCompound dataForThisSlot = new NBTTagCompound();
				dataForThisSlot.setByte("slot", (byte) i);
				itemStacks[i].writeToNBT(dataForThisSlot);
				dataForAllSlots.appendTag(dataForThisSlot);
			}
		}
		nbt.setTag("items", dataForAllSlots);
	}
	
	public static boolean isEmpty(ItemStack[] itemStacks) {
		for (ItemStack itemstack : itemStacks) {
			if (!itemstack.isEmpty())
				return false;
		}
		return true;
	}
	
	public static void setInventorySlotContents(IInventory inventory, ItemStack[] itemStacks, int slotIndex, ItemStack itemstack) {
		itemStacks[slotIndex] = itemstack;
		if (!itemstack.isEmpty() && itemstack.getCount() > inventory.getInventoryStackLimit()) { 
			itemstack.setCount(inventory.getInventoryStackLimit());
		}
		inventory.markDirty();
	}
	
	public static ItemStack decrStackSize(IInventory inventory, int slotIndex, int count) {
		ItemStack itemStackInSlot = inventory.getStackInSlot(slotIndex);
		if (itemStackInSlot.isEmpty()) return ItemStack.EMPTY;  //isEmpty(), EMPTY_ITEM
		
		ItemStack itemStackRemoved;
		if (itemStackInSlot.getCount() <= count) { //getStackSize
			itemStackRemoved = itemStackInSlot;
			inventory.setInventorySlotContents(slotIndex, ItemStack.EMPTY); // EMPTY_ITEM
		} else {
			itemStackRemoved = itemStackInSlot.splitStack(count);
			if (itemStackInSlot.getCount() == 0) { //getStackSize
				inventory.setInventorySlotContents(slotIndex, ItemStack.EMPTY); //EMPTY_ITEM
			}
		}
		inventory.markDirty();
		return itemStackRemoved;
	}
	
	public static ItemStack removeStackFromSlot(IInventory inventory, int slotIndex) {
		ItemStack itemStack = inventory.getStackInSlot(slotIndex);
		if (!itemStack.isEmpty())
			inventory.setInventorySlotContents(slotIndex, ItemStack.EMPTY);
		return itemStack;
	}
	
	public static boolean withInRange(TileEntity te, EntityPlayer player) {
		return withInRange(te, player, 8);
	}
	
	public static boolean withInRange(TileEntity te, EntityPlayer player, double maxDistance) {
		BlockPos pos = te.getPos();
		if (te.getWorld().getTileEntity(pos) != te)
			return false;
		
		return player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < maxDistance*maxDistance;
	}
}
