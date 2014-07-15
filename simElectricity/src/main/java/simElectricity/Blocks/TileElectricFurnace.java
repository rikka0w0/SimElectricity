package simElectricity.Blocks;


import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.Energy;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Util;

public class TileElectricFurnace extends TileStandardSEMachine implements ISyncPacketHandler,IEnergyNetUpdateHandler {
    public static float energyPerItem = 1000F;
    public static float onResistance = 100F;

    public boolean isWorking = false;
    public int progress = 0;
    public float resistance = 10;
    public float energyStored;
    public ItemStack result;


    @Override
    public void onInventoryChanged() {
        if (worldObj.isRemote)
            return;

        result = getResult(inv[0]);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote)
            return;
        //TODO inv[1] == null | (inv[1] != null && inv[1].isItemEqual(result))
        if (Energy.getPower(this) > 0 && result != null && (inv[1] == null || (inv[1] != null && inv[1].isItemEqual(result)))) {
            energyStored += Energy.getPower(this) * 0.02;
            progress = ((int) (energyStored * 100 / energyPerItem));

            if (resistance > onResistance) {
                resistance = onResistance;
                Energy.postTileChangeEvent(this);
            }

            isWorking = true;
            Util.updateTileEntityField(this, "isWorking");

            if (energyStored > energyPerItem) {
                ItemStack newResult = getResult(inv, 0);
                if (inv[0] != null && inv[0].stackSize == 0)
                    inv[0] = null;

                if (inv[1] == null)
                    inv[1] = newResult.copy();
                else
                    inv[1].stackSize += newResult.stackSize;

                result = getResult(inv[0]);
                progress = 0;
                energyStored = 0;
            }
        } else {
            progress = 0;
            energyStored = 0;
            if (resistance <= onResistance) {
                resistance = Float.MAX_VALUE;
                Energy.postTileChangeEvent(this);
            }
            isWorking = false;
            Util.updateTileEntityField(this, "isWorking");
        }
    }

    public ItemStack getResult(ItemStack i) {
        if (i == null)
            return null;
        return FurnaceRecipes.smelting().getSmeltingResult(i.copy());
    }

    public ItemStack getResult(ItemStack inv[], int i) {
        if (inv[i] == null)
            return null;
        ItemStack r = FurnaceRecipes.smelting().getSmeltingResult(inv[i]);
        if (r != null)
            inv[i].stackSize -= 1;
        if (r.stackSize == 0)
            r.stackSize = 1;
        return r;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        isWorking = tagCompound.getBoolean("isWorking");
        energyStored = tagCompound.getFloat("energyStored");
        result = getResult(inv[0]);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setBoolean("isWorking", isWorking);
        tagCompound.setFloat("energyStored", energyStored);
    }

    //---------------------------------------------------------------------------------------------------------
    @Override
    public void onServer2ClientUpdate(String field, Object value, short type) {
        if (field.contains("isWorking"))
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void onClient2ServerUpdate(String field, Object value, short type) {
    }

    @Override
    public float getResistance() {
        return resistance;
    }

    @Override
    public float getOutputVoltage() {
        return 0;
    }

	@Override
	public void onEnergyNetUpdate() {
		if (Energy.getVoltage(this)>265)
			worldObj.createExplosion(null, xCoord, yCoord, zCoord, 4F + Energy.getVoltage(this) / 265, true);
	}

    @Override
    public boolean canSetFacing(ForgeDirection newFacing) {
        return newFacing != ForgeDirection.UP && newFacing != ForgeDirection.DOWN;
    }

    //Inventory
    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { 0, 1 };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side) {
        return slot == 0;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side) {
        return slot == 1;
    }
}