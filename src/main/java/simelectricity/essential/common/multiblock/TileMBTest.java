package simelectricity.essential.common.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.common.SETileEntity;

public class TileMBTest extends SETileEntity implements ISEMultiBlockTile{
	private MultiBlockTileInfo mbInfo;
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		mbInfo = new MultiBlockTileInfo(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		mbInfo.saveToNBT(nbt);
	}

	@Override
	public void onStructureCreated(MultiBlockTileInfo mbInfo) {
		this.mbInfo = mbInfo;
		this.markDirty();
	}

	@Override
	public MultiBlockTileInfo getMultiBlockTileInfo() {
		return this.mbInfo;
	}

	@Override
	public void onStructureRemoved() {
		System.out.println("Structure Removed");
	}
}
