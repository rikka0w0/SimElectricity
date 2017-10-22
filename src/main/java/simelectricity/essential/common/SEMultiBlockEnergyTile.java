package simelectricity.essential.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockTileInfo;

public abstract class SEMultiBlockEnergyTile extends SEEnergyTile implements IMultiBlockTile {
	//To minimize network usage, mbInfo will not be send to blocks other than the Render block
    protected MultiBlockTileInfo mbInfo;
    
    //////////////////////////////
    /////TileEntity
    //////////////////////////////
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
    public final void onStructureCreating(MultiBlockTileInfo mbInfo) {
        this.mbInfo = mbInfo;
        markDirty();
        
        onStructureCreating();
    }
    
    @Override
    public MultiBlockTileInfo getMultiBlockTileInfo() {
        return mbInfo;
    }
    
    protected abstract void onStructureCreating();
    
    /////////////////////////////////////////////////////////
    /////Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        super.prepareS2CPacketData(nbt);
    	mbInfo.saveToNBT(nbt);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
    	mbInfo = new MultiBlockTileInfo(nbt);
        
        super.onSyncDataFromServerArrived(nbt);
    }
    
    /////////////////////////////////////////////////////////
    /////Utils
    /////////////////////////////////////////////////////////
    public boolean isMirrored() {
    	return this.mbInfo==null? false : this.mbInfo.mirrored;
    }
    
    public EnumFacing getFacing() {
    	return this.mbInfo==null? null : this.mbInfo.facing;
    }
    
    public int getFacingInt() {
    	return this.mbInfo==null? 0 : this.mbInfo.facing.ordinal()-2;
    }
}
