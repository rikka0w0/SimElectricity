package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.common.SETileEntity;
import simelectricity.essential.common.multiblock.ISEMultiBlockTile;
import simelectricity.essential.common.multiblock.MultiBlockTileInfo;

public class TilePowerTransformerPlaceHolder extends SETileEntity implements ISEMultiBlockTile{
	private MultiBlockTileInfo mbInfo;
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		mbInfo = new MultiBlockTileInfo(nbt);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		mbInfo.saveToNBT(nbt);
		return super.writeToNBT(nbt);
	}

	@Override
	public MultiBlockTileInfo getMultiBlockTileInfo() {
		return this.mbInfo;
	}
	
	@Override
	public void onStructureCreating(MultiBlockTileInfo mbInfo) {
		this.mbInfo = mbInfo;
		this.markDirty();
	}

	@Override
	public void onStructureCreated() {
		
	}

	@Override
	public void onStructureRemoved() {
		
	}
}
