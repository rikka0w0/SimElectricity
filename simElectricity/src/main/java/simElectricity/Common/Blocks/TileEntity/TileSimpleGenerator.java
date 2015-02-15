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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import simElectricity.API.Common.TileSidedGenerator;
import simElectricity.API.Energy;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.Network;

import java.util.List;

public class TileSimpleGenerator extends TileSidedGenerator implements INetworkEventHandler {
    public static int normalOutputV = 230;
    public static int normalOutputR = 1;

    public boolean isWorking;
    public int progress;
    protected int burnTime;
    protected float burned;

    //Statics
    public static int getBurnTime(ItemStack in) {
        return TileEntityFurnace.getItemBurnTime(in);
    }

    @Override
    public void update() {
        super.update();

        if (worldObj.isRemote)
            return;


        if (burnTime > 0) { //Burning something
            if (outputVoltage != normalOutputV) {
                isWorking = true;
                outputVoltage = normalOutputV;
                outputResistance = normalOutputR;
                Energy.postTileChangeEvent(this);
            }

            double workDone = Energy.getWorkDonePerTick(this);
            if (workDone < 0)
                return;

            burned -= 0.03 + 0.02 * workDone;
            progress = (int) (burned * 100 / burnTime); //Update progress

            if (burned <= 0) {
                burned = 0;

                int burnTime_Inv0 = getBurnTime(inv[0]);      //Get next thing to burn
                if (burnTime_Inv0 != 0) {                     //Can burn
                    if (inv[0] != null && inv[0].stackSize > 1) { //Consume Item
                        inv[0].stackSize--;
                    } else {
                        inv[0] = null;
                    }
                    updateWorkingStatus(burnTime_Inv0);    //Update Working Status
                } else {
                    updateWorkingStatus(0);                //Update Working Status (No more fuel)
                }
            }
        } else {
            progress = 0;
        }
    }

    public void updateWorkingStatus(int bt) {
        if (bt == 0) {
            isWorking = false;

            if (outputVoltage != 0) {
                outputVoltage = 0;
                outputResistance = Float.MAX_VALUE;
                Energy.postTileChangeEvent(this);
            }

            burnTime = 0;
            burned = 0;
        } else {
            isWorking = true;

            if (outputVoltage != normalOutputV) {
                outputVoltage = normalOutputV;
                outputResistance = normalOutputR;
                Energy.postTileChangeEvent(this);
            }

            burnTime = bt;
            if (burned <= 0)
                burned = burnTime;
        }
        Network.updateNetworkFields(this);
    }

    @Override
    public void onInventoryChanged() {
        if (worldObj.isRemote)
            return;

        int burnTime_Inv0 = getBurnTime(inv[0]);
        if (burned == 0 && burnTime_Inv0 != 0) {
            if (inv[0] != null && inv[0].stackSize > 1) {
                inv[0].stackSize--;
            } else {
                inv[0] = null;
            }
            updateWorkingStatus(burnTime_Inv0);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        isWorking = tagCompound.getBoolean("isWorking");
        burnTime = tagCompound.getInteger("burnTime");
        burned = tagCompound.getFloat("burned");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setBoolean("isWorking", isWorking);
        tagCompound.setInteger("burnTime", burnTime);
        tagCompound.setFloat("burned", burned);
    }

    //Functions
    @Override
    public int getInventorySize() {
        return 1;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{0};
    }


    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, EnumFacing side) {
        return slot == 0 && TileEntityFurnace.isItemFuel(itemStack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, EnumFacing side) {
        return slot == 0;
    }

    @Override
    public void addNetworkFields(List fields) {
        fields.add("isWorking");
        worldObj.markBlockForUpdate(pos);
    }

    @Override
    public void onFieldUpdate(String[] fields, Object[] values) {
        //Handling on client side
        if (worldObj.isRemote) {
            for (String s : fields) {
                if (s.contains("isWorking")) {
                    worldObj.markBlockForUpdate(pos);
                }
            }
        }
    }

    @Override
    public boolean canSetFunctionalSide(EnumFacing newFunctionalSide) {
        return true;
    }
}
