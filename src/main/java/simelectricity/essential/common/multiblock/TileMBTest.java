package simelectricity.essential.common.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.common.SETileEntity;

public class TileMBTest extends SETileEntity implements ISEMultiBlockTile {
    public MultiBlockTileInfo mbInfo;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.mbInfo = new MultiBlockTileInfo(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        this.mbInfo.saveToNBT(nbt);
        return super.writeToNBT(nbt);
    }

    @Override
    public void onStructureCreating(MultiBlockTileInfo mbInfo) {
        this.mbInfo = mbInfo;
        markDirty();
    }

    @Override
    public MultiBlockTileInfo getMultiBlockTileInfo() {
        return mbInfo;
    }

    @Override
    public void onStructureRemoved() {
        System.out.println("Structure Removed");
    }

    @Override
    public void onStructureCreated() {
        // TODO Auto-generated method stub

    }
}
