package simelectricity.essential.common.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import simelectricity.essential.utils.Utils;

public class MultiBlockTileInfo {
	public final EnumFacing facing;
	public final boolean mirrored;
	public final int xOffset, yOffset, zOffset;
	public final BlockPos origin;
	
	/**
	 * Structure creation
	 * @param facing
	 * @param mirrored
	 * @param xOffset
	 * @param yOffset
	 * @param zOffset
	 * @param xOrigin
	 * @param yOrigin
	 * @param zOrigin
	 */
	public MultiBlockTileInfo(EnumFacing facing, boolean mirrored, int xOffset, int yOffset, int zOffset, int xOrigin, int yOrigin, int zOrigin){
		this.facing = facing;
		this.mirrored = mirrored;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.origin = new BlockPos(xOrigin, yOrigin, zOrigin);
		this.formed = true;
	}
	
	public MultiBlockTileInfo(NBTTagCompound nbt){
		this.facing = Utils.facingFromNbt(nbt, "facing");
		this.mirrored = nbt.getBoolean("mirrored");
		this.xOffset = nbt.getInteger("xOffset");
		this.yOffset = nbt.getInteger("yOffset");
		this.zOffset = nbt.getInteger("zOffset");
		this.origin = Utils.posFromNbt(nbt, "origin");
		this.formed = nbt.getBoolean("formed");
	}
	
	public void saveToNBT(NBTTagCompound nbt){
		Utils.saveToNbt(nbt, "facing", facing);
		nbt.setBoolean("mirrored", mirrored);
		nbt.setInteger("xOffset", xOffset);
		nbt.setInteger("yOffset", yOffset);
		nbt.setInteger("zOffset", zOffset);
		Utils.saveToNbt(nbt, "origin", origin);
		nbt.setBoolean("formed", formed);
	}
	
	public boolean formed;
}
