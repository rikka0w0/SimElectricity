package simelectricity.essential.common.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class MultiBlockTileInfo {
	public final ForgeDirection facing;
	public final boolean mirrored;
	public final int xOffset, yOffset, zOffset;
	public final int xOrigin, yOrigin, zOrigin;
	
	public MultiBlockTileInfo(ForgeDirection facing, boolean mirrored, int xOffset, int yOffset, int zOffset, int xOrigin, int yOrigin, int zOrigin){
		this.facing = facing;
		this.mirrored = mirrored;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.xOrigin = xOrigin;
		this.yOrigin = yOrigin;
		this.zOrigin = zOrigin;
	}
	
	public MultiBlockTileInfo(NBTTagCompound nbt){
		this.facing = ForgeDirection.getOrientation(nbt.getByte("facing"));
		this.mirrored = nbt.getBoolean("mirrored");
		this.xOffset = nbt.getInteger("xOffset");
		this.yOffset = nbt.getInteger("yOffset");
		this.zOffset = nbt.getInteger("zOffset");
		this.xOrigin = nbt.getInteger("xOrigin");
		this.yOrigin = nbt.getInteger("yOrigin");
		this.zOrigin = nbt.getInteger("zOrigin");
	}
	
	public void saveToNBT(NBTTagCompound nbt){
		nbt.setByte("facing", (byte) facing.ordinal());
		nbt.setBoolean("mirrored", mirrored);
		nbt.setInteger("xOffset", xOffset);
		nbt.setInteger("yOffset", yOffset);
		nbt.setInteger("zOffset", zOffset);
		nbt.setInteger("xOrigin", xOrigin);
		nbt.setInteger("yOrigin", yOrigin);
		nbt.setInteger("zOrigin", zOrigin);
	}
}
