package simelectricity.essential.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelDataMap;
import rikka.librikka.blockentity.BlockEntityBase;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockTileInfo;

public class TileMultiBlockPlaceHolder extends BlockEntityBase implements IMultiBlockTile {
	public TileMultiBlockPlaceHolder(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

	//To minimize network usage, mbInfo will not be send to blocks other than the Render block
    protected MultiBlockTileInfo mbInfo;

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
    protected void collectModelData(ModelDataMap.Builder builder) {
		builder.withInitial(IMultiBlockTile.prop, this);
    }

    /////////////////////////////////////////////////////////
    /////Functions to be implemented
    /////////////////////////////////////////////////////////
    public void onStructureCreating() {

    }
}
