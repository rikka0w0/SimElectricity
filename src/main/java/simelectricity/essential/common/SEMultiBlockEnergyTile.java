package simelectricity.essential.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockTileInfo;

public abstract class SEMultiBlockEnergyTile extends SEEnergyTile implements IMultiBlockTile {
	//To minimize network usage, mbInfo will not be send to blocks other than the Render block
    protected MultiBlockTileInfo mbInfo;

    public SEMultiBlockEnergyTile(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

    //////////////////////////////
    /////BlockEntity
    //////////////////////////////
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.mbInfo = new MultiBlockTileInfo(nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        this.mbInfo.saveToNBT(nbt);
        return super.save(nbt);
    }

    @Override
    public final void onStructureCreating(MultiBlockTileInfo mbInfo) {
        this.mbInfo = mbInfo;
        setChanged();

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
    public void prepareS2CPacketData(CompoundTag nbt) {
        super.prepareS2CPacketData(nbt);
    	mbInfo.saveToNBT(nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
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
