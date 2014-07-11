package simElectricity.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import simElectricity.API.Common.TileSidedGenerator;
import simElectricity.API.Energy;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Util;

public class TileSimpleGenerator extends TileSidedGenerator implements ISyncPacketHandler {
    public static int normalOutputV = 230;
    public static int normalOutputR = 1;

    public boolean isWorking;
    protected int burnTime;
    protected float burned;
    public int progress;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote)
            return;


        if (burnTime > 0) { //Burning something
            if (outputVoltage != normalOutputV) {
                isWorking = true;
                outputVoltage = normalOutputV;
                outputResistance = normalOutputR;
                Energy.postTileChangeEvent(this);
            }

            float workDone = Energy.getWorkDonePerTick(this);
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
                    updateWoringStatus(burnTime_Inv0);    //Update Working Status
                } else {
                    updateWoringStatus(0);                //Update Working Status (No more fuel)
                }
            }
        } else {
            progress = 0;
        }
    }

    public void updateWoringStatus(int bt) {
        if (bt == 0) {
            isWorking = false;

            if (outputVoltage != 0) {
                outputVoltage = 0;
                outputResistance = 10000000;
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
        Util.updateTileEntityField(this, "isWorking");
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
            updateWoringStatus(burnTime_Inv0);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        burnTime = tagCompound.getInteger("burnTime");
        burned = tagCompound.getFloat("burned");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("burnTime", burnTime);
        tagCompound.setFloat("burned", burned);
    }

    //Statics-------------------------------------------------------
    public static int getBurnTime(ItemStack in) {
        return TileEntityFurnace.getItemBurnTime(in);
    }

    //Functions-----------------------------------------------------
    @Override
    public int getInventorySize() {
        return 1;
    }

    @Override
    public void onServer2ClientUpdate(String field, Object value, short type) {
        if (field.contains("isWorking"))
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void onClient2ServerUpdate(String field, Object value, short type) {
    }
}
