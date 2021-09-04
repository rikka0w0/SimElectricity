package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEMultiBlockEnergyTile;

public abstract class SEMultiBlockGridTile extends SEMultiBlockEnergyTile implements ISEGridTile, ISEPowerPole {
    @OnlyIn(Dist.CLIENT)
    protected PowerPoleRenderHelper renderHelper;
    protected BlockPos neighbor;

    public SEMultiBlockGridTile(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

    //////////////////////////////
    /////ISEGridTile
    //////////////////////////////
    protected ISEGridNode gridNode;

    @Override
    public ISEGridNode getGridNode() {
        return this.gridNode;
    }

    @Override
    public void setGridNode(ISEGridNode gridObj) {
        gridNode = gridObj;
    }

    @Override
    public void onGridNeighborUpdated() {
        ISEGridNode[] neighbors = this.gridNode.getNeighborList();
        neighbor = (neighbors!=null && neighbors.length>0) ? neighbors[0].getPos() : null;

        markTileEntityForS2CSync();
    }

	@Override
	public boolean canConnect(BlockPos toPos) {
        return this.neighbor == null;
    }

    //////////////////////////////
    /////BlockEntity
    //////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public double getViewDistance() {
        return 100000;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return BlockEntity.INFINITE_EXTENT_AABB;
    }

    /////////////////////////////////////////////////////////
    /////Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
        Utils.saveToNbt(nbt, "neighbor", this.neighbor);
        super.prepareS2CPacketData(nbt);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
        neighbor = Utils.posFromNbt(nbt, "neighbor");
        super.onSyncDataFromServerArrived(nbt);

        if (this.renderHelper == null)
            this.renderHelper = this.createRenderHelper();

        PowerPoleRenderHelper.notifyChanged(this);

        if (this.neighbor != null) {
            BlockEntity neighborTile = this.level.getBlockEntity(neighbor);
            if (neighborTile instanceof ISEPowerPole)
                PowerPoleRenderHelper.notifyChanged((ISEPowerPole) neighborTile);
        }
    }

    /////////////////////////////////////////////////////////
    /////ISEPowerPole
    /////////////////////////////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public PowerPoleRenderHelper getRenderHelper() {
        return this.renderHelper;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockPos[] getNeighborPosArray() {
        return new BlockPos[] {this.neighbor};
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract PowerPoleRenderHelper createRenderHelper();
}
