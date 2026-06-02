package simelectricity.essential.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockTileInfo;

public abstract class SEMultiBlockEnergyBlockEntity extends SEEnergyBlockEntity implements IMultiBlockTile {
	//To minimize network usage, mbInfo will not be send to blocks other than the Render block
    protected MultiBlockTileInfo mbInfo;

    public SEMultiBlockEnergyBlockEntity(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

    //////////////////////////////
    /////BlockEntity
    //////////////////////////////
    @Override
    public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.mbInfo = new MultiBlockTileInfo(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries) {
        this.mbInfo.saveToNBT(nbt);
        super.saveAdditional(nbt, registries);
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
