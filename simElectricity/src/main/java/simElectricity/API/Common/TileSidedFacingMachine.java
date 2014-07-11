package simElectricity.API.Common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;

public abstract class TileSidedFacingMachine extends TileInventoryMachine implements ISidedFacing {
    protected ForgeDirection facing = ForgeDirection.NORTH;

    //ISidedFacing
    @Override
    public void setFacing(ForgeDirection newFacing) {
        facing = newFacing;
    }

    @Override
    public ForgeDirection getFacing() {
        return facing;
    }

    @Override
    public boolean canSetFacing(ForgeDirection newFacing) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        facing = Util.byte2Direction(tagCompound.getByte("facing"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("facing", Util.direction2Byte(facing));
    }
}
