package simelectricity.essential.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import rikka.librikka.blockentity.BlockEntityBase;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockTileInfo;

public class BlockEntityMultiBlockPlaceHolder extends BlockEntityBase implements IMultiBlockTile {
	public BlockEntityMultiBlockPlaceHolder(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

	//To minimize network usage, mbInfo will not be send to blocks other than the Render block
    protected MultiBlockTileInfo mbInfo;

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

		this.markForRenderUpdate();
    }

	@Override
    protected void collectModelData(ModelData.Builder builder) {
		builder.with(IMultiBlockTile.prop, this);
    }

    /////////////////////////////////////////////////////////
    /////Functions to be implemented
    /////////////////////////////////////////////////////////
    public void onStructureCreating() {

    }
}
