package simElectricity.Blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.*;

public class TileElectricFurnace extends TileStandardSEMachine implements ISyncPacketHandler,ISidedInventory{
	public boolean isWorking=false;
	public int progress=0;
	public float resistance=10;
	public static float energyPerItem=4000F;
	public float energyStored;
	public ItemStack result;
	
	public TileElectricFurnace(){
		inv = new ItemStack[2];
	}

	public void onInventoryChanged(){
    	result=getResult(inv[0]);
    }
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(worldObj.isRemote)
			return;	
		
		if(Util.getPower(this)>0&&result!=null&&(inv[1]==null|(inv[1]!=null&&inv[1].isItemEqual(result)))){
			energyStored+=Util.getPower(this)*0.02;
			progress=((int) (energyStored*100/energyPerItem));
			
			if(resistance>100){
				resistance=50;
				Util.postTileChangeEvent(this);
			}
			
			isWorking=true;
			Util.updateTileEntityField(this, "isWorking");
			
			if(energyStored>energyPerItem){
				ItemStack newResult=getResult(inv,0);
				if(inv[0]!=null&&inv[0].stackSize==0)
					inv[0]=null;
				
				if(inv[1]==null)
					inv[1]=newResult.copy();
				else
					inv[1].stackSize+=newResult.stackSize;
				
				result=getResult(inv[0]);
				progress=0;
				energyStored=0;
			}
		}else{
			progress=0;
			energyStored=0;
			if(resistance<100){
				resistance=1000000000;
				Util.postTileChangeEvent(this);
			}
			isWorking=false;
			Util.updateTileEntityField(this, "isWorking");
		}
	}
	
	public ItemStack getResult(ItemStack i) {
		if (i==null)
			return null;
		return FurnaceRecipes.smelting().getSmeltingResult(i.copy());
	}
	
	public ItemStack getResult(ItemStack inv[],int i) {
		if (inv[i]==null)
			return null;
		ItemStack r= FurnaceRecipes.smelting().getSmeltingResult(inv[i]);
		if(r!=null)
			inv[i].stackSize-=1;
		if(r.stackSize==0)
			r.stackSize=1;
		return r;
	}
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
    	super.readFromNBT(tagCompound);
    	
    	isWorking=tagCompound.getBoolean("isWorking");
    	energyStored=tagCompound.getFloat("energyStored");
    	
        NBTTagList tagList = tagCompound.getTagList("Inventory",Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
                byte slot = tag.getByte("Slot");
                if (slot >= 0 && slot < inv.length) {
                        inv[slot] = ItemStack.loadItemStackFromNBT(tag);
                }
        }
        result=getResult(inv[0]);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
    	super.writeToNBT(tagCompound);
    	
    	tagCompound.setBoolean("isWorking", isWorking);
    	tagCompound.setFloat("energyStored", energyStored);
    	
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
    
	//---------------------------------------------------------------------------------------------------------
	@Override
	public void onServer2ClientUpdate(String field, Object value, short type) {
		if(field.contains("isWorking"))	
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void onClient2ServerUpdate(String field, Object value, short type) {}
	
    @Override
	public float getResistance() {return resistance;}
    
	@Override
	public void onOverloaded() {}

	@Override
	public int getMaxPowerDissipation() {return 0;}

	@Override
	public float getOutputVoltage() {return 0;}

	@Override
	public float getMaxSafeVoltage() {return 265;}

	@Override
	public void onOverVoltage() {
		worldObj.createExplosion(null, xCoord, yCoord, zCoord, 4F+Util.getVoltage(this)/getMaxSafeVoltage(), true);
	}
	
	@Override
	public boolean canSetFacing(ForgeDirection newFacing) {
		if(newFacing!=ForgeDirection.UP&&newFacing!=ForgeDirection.DOWN)
			return true;
		else
			return false;
	}
	
	//Inventory stuff--------------------------------------------------------------------------------
	public ItemStack[] inv;
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
	public String getInventoryName() {return "TileElectricFurnace";}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {return new int[]{0,1};}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		if(slot==0)
			return true;
		else
			return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		if(slot==1)
			return true;
		else
			return false;
	}
}