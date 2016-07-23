/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simElectricity.Common.Blocks.TileEntity;


import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileStandardSEMachine;
import simElectricity.API.Energy;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.Util;

import java.util.List;

public class TileElectricFurnace extends TileStandardSEMachine implements IEnergyNetUpdateHandler, INetworkEventHandler {
    public static float energyPerItem = 1000F;
    public static float onResistance = 100F;

    public boolean isWorking = false;
    public int progress = 0;
    public float resistance = Float.MAX_VALUE;
    public float energyStored;
    public ItemStack result;


    @Override
    public void onInventoryChanged() {
        if (worldObj.isRemote)
            return;

        result = getResult(inv[0]);

        if (result == null) {
            stop();
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote)
            return;
        //TODO inv[1] == null | (inv[1] != null && inv[1].isItemEqual(result))
        double voltage = Energy.getVoltage(this);
        double power = voltage * voltage / this.getResistance();
        
        if (power > 0 && result != null && (inv[1] == null || (inv[1] != null && inv[1].stackSize < 64 && inv[1].isItemEqual(result)))) {
            energyStored += power * 0.02;
            progress = ((int) (energyStored * 100 / energyPerItem));

            if (resistance > onResistance) {
                resistance = onResistance;
                Energy.postTileChangeEvent(this);
            }

            isWorking = true;
            Util.networkManager.updateNetworkFields(this);

            if (energyStored > energyPerItem) {
                ItemStack newResult = result.copy();

                inv[0].stackSize -= 1;
                if (newResult.stackSize == 0)
                    newResult.stackSize = 1;

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
        }

        if (result == null && isWorking) {
            stop();
        }
    }

    void stop() {
        progress = 0;
        energyStored = 0;
        if (resistance <= onResistance) {
            resistance = Float.MAX_VALUE;
            Energy.postTileChangeEvent(this);
        }
        isWorking = false;
        Util.networkManager.updateNetworkFields(this);
    }

    public ItemStack getResult(ItemStack i) {
        if (i == null)
            return null;
        return FurnaceRecipes.smelting().getSmeltingResult(i.copy());
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

    @Override
    public void addNetworkFields(List fields) {
        fields.add("isWorking");
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void onFieldUpdate(String[] fields, Object[] values) {

    }

    @Override
    public double getResistance() {
        return resistance;
    }

    @Override
    public double getOutputVoltage() {
        return 0;
    }

    @Override
    public void onEnergyNetUpdate() {
        if (Energy.getVoltage(this) > 265)
            worldObj.createExplosion(null, xCoord, yCoord, zCoord, (float) (4F + Energy.getVoltage(this) / 265), true);

        if (Energy.getVoltage(this) == 0) {
            isWorking = false;
            Util.networkManager.updateNetworkFields(this);
        }
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
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return slot == 0 && FurnaceRecipes.smelting().getSmeltingResult(itemStack) != null;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        return slot == 1;
    }
}