package simelectricity.essential.grid;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public abstract class TileMultiBlockPole extends SEMultiBlockEnergyTile implements ISEGridTile, ISEPowerPole {
    protected BlockPos neighbor1, neighbor2;

    @OnlyIn(Dist.CLIENT)
    protected abstract PowerPoleRenderHelper createRenderHelper();

    public TileMultiBlockPole(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

    //////////////////////////////
    /////ISEPowerPole
    //////////////////////////////
    @OnlyIn(Dist.CLIENT)
    private PowerPoleRenderHelper renderHelper;

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockPos[] getNeighborPosArray() {
        return new BlockPos[] {this.neighbor1, this.neighbor2};
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public PowerPoleRenderHelper getRenderHelper() {
        return this.renderHelper;
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
    public void setGridNode(ISEGridNode gridNode) {
        this.gridNode = gridNode;
    }

    @Override
    public void onGridNeighborUpdated() {
        this.neighbor1 = null;
        this.neighbor2 = null;

        ISEGridNode[] neighbors = this.gridNode.getNeighborList();
        if (neighbors.length == 1) {
            this.neighbor1 = neighbors[0].getPos();
        } else if (neighbors.length > 1) {
            this.neighbor1 = neighbors[0].getPos();
            this.neighbor2 = neighbors[1].getPos();
        }

        markTileEntityForS2CSync();
    }

    @Override
    public boolean canConnect(@Nullable BlockPos to) {
        return this.neighbor1 == null || this.neighbor2 == null;
    }

    //////////////////////////////
    /////BlockEntity
    //////////////////////////////
    @OnlyIn(Dist.CLIENT)
    @Override
    public double getViewDistance() {
        return 100000;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AABB getRenderBoundingBox() {
        return BlockEntity.INFINITE_EXTENT_AABB;
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
    	super.prepareS2CPacketData(nbt);
        Utils.saveToNbt(nbt, "neighbor1", this.neighbor1);
        Utils.saveToNbt(nbt, "neighbor2", this.neighbor2);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
        super.onSyncDataFromServerArrived(nbt);
        this.neighbor1 = Utils.posFromNbt(nbt, "neighbor1");
        this.neighbor2 = Utils.posFromNbt(nbt, "neighbor2");
        if (this.renderHelper == null)
            this.renderHelper = this.createRenderHelper();


        PowerPoleRenderHelper.notifyChanged(this);

        this.updateRenderInfo(this.neighbor1);
        this.updateRenderInfo(this.neighbor2);

    }

    @OnlyIn(Dist.CLIENT)
    protected void updateRenderInfo(BlockPos neighborPos) {
        if (neighborPos == null)
            return;

        BlockEntity neighbor = this.level.getBlockEntity(neighborPos);
        if (neighbor instanceof ISEPowerPole)
            PowerPoleRenderHelper.notifyChanged((ISEPowerPole) neighbor);
    }
}
