package simelectricity.essential.common;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockTileInfo;

public abstract class SEMultiBlockEnergyTile extends SEEnergyTile implements IMultiBlockTile {
	//To minimize network usage, mbInfo will not be send to blocks other than the Render block
    protected MultiBlockTileInfo mbInfo;
    
    //////////////////////////////
    /////TileEntity
    //////////////////////////////
    @Override
    public void read(BlockState blockState, CompoundNBT nbt) {
        super.read(blockState, nbt);
        this.mbInfo = new MultiBlockTileInfo(nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        this.mbInfo.saveToNBT(nbt);
        return super.write(nbt);
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
    public void prepareS2CPacketData(CompoundNBT nbt) {
        super.prepareS2CPacketData(nbt);
    	mbInfo.saveToNBT(nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundNBT nbt) {
    	mbInfo = new MultiBlockTileInfo(nbt);
        
        super.onSyncDataFromServerArrived(nbt);
    }
    
    /////////////////////////////////////////////////////////
    /////Utils
    /////////////////////////////////////////////////////////
    public boolean isMirrored() {
    	return this.mbInfo==null? false : this.mbInfo.mirrored;
    }
    
    public Direction getFacing() {
    	return this.mbInfo==null? null : this.mbInfo.facing;
    }
    
    public int getFacingInt() {
    	return this.mbInfo==null? 0 : this.mbInfo.facing.ordinal()-2;
    }
}
