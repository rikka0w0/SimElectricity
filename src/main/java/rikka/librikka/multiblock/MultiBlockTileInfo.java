package rikka.librikka.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import rikka.librikka.Utils;

public class MultiBlockTileInfo {
    public final EnumFacing facing;
    public final boolean mirrored;
    public final int xOffset, yOffset, zOffset;
    public final BlockPos origin;
    public boolean formed;

    /**
     * Structure creation
     *
     * @param facing
     * @param mirrored
     * @param xOffset
     * @param yOffset
     * @param zOffset
     * @param xOrigin
     * @param yOrigin
     * @param zOrigin
     */
    public MultiBlockTileInfo(EnumFacing facing, boolean mirrored, int xOffset, int yOffset, int zOffset, int xOrigin, int yOrigin, int zOrigin) {
        this.facing = facing;
        this.mirrored = mirrored;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        origin = new BlockPos(xOrigin, yOrigin, zOrigin);
        formed = true;
    }

    public MultiBlockTileInfo(NBTTagCompound nbt) {
        facing = Utils.facingFromNbt(nbt, "facing");
        mirrored = nbt.getBoolean("mirrored");
        xOffset = nbt.getInteger("xOffset");
        yOffset = nbt.getInteger("yOffset");
        zOffset = nbt.getInteger("zOffset");
        origin = Utils.posFromNbt(nbt, "origin");
        formed = nbt.getBoolean("formed");
    }

    public void saveToNBT(NBTTagCompound nbt) {
        Utils.saveToNbt(nbt, "facing", this.facing);
        nbt.setBoolean("mirrored", this.mirrored);
        nbt.setInteger("xOffset", this.xOffset);
        nbt.setInteger("yOffset", this.yOffset);
        nbt.setInteger("zOffset", this.zOffset);
        Utils.saveToNbt(nbt, "origin", this.origin);
        nbt.setBoolean("formed", this.formed);
    }

    public BlockPos getPartPos(Vec3i offsetPos) {
        int[] offset = MultiBlockStructure.offsetFromOrigin(getFacing(), this.mirrored,
                offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
        return this.origin.add(offset[0], offset[1], offset[2]);
    }
    
    public int getFacing() {
    	return this.facing.ordinal() - 2;
    }
}
