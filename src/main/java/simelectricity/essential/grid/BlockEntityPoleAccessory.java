package simelectricity.essential.grid;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.blockentity.ISEGridBlockEntity;
import simelectricity.essential.api.ISEPoleAccessory;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEEnergyBlockEntity;

public abstract class BlockEntityPoleAccessory extends SEEnergyBlockEntity implements ISEPoleAccessory, ISEGridBlockEntity, ISEPowerPole {
    protected BlockPos host;

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    protected abstract PowerPoleRenderHelper createRenderHelper();

    public BlockEntityPoleAccessory(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

    /////////////////////////////////////////////////////////
    /////ISEPowerPole
    /////////////////////////////////////////////////////////
    @OnlyIn(Dist.CLIENT)
    protected PowerPoleRenderHelper renderHelper;

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockPos[] getNeighborPosArray() {
        return new BlockPos[] {};
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockPos getAccessoryPos() {
    	return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public PowerPoleRenderHelper getRenderHelper() {
        return this.renderHelper;
    }

    //////////////////////////////
    /////ISEGridBlockEntity
    //////////////////////////////
    protected ISEGridNode gridNode;
    @Override
    public ISEGridNode getGridNode() {
        return this.gridNode;
    }

    @Override
    public void setGridNode(ISEGridNode gridObj) {
        this.gridNode = gridObj;
    }

    @Override
    public void onGridNeighborUpdated() {
    	host = null;

    	ISEGridNode[] neighbors = this.gridNode.getNeighborList();
    	if (neighbors.length > 0)
    		host = neighbors[0].getPos();

        markTileEntityForS2CSync();
    }

    @Override
    public boolean canConnect(BlockPos toPos) {
        return this.host == null;
    }

    //////////////////////////////
    /////BlockEntity
    //////////////////////////////


    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
        Utils.saveToNbt(nbt, "host", this.host);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
    	host = Utils.posFromNbt(nbt, "host");

        if (this.renderHelper == null)
            this.renderHelper = this.createRenderHelper();

        PowerPoleRenderHelper.notifyChanged(this);

        if (this.host != null) {
            BlockEntity neighborTile = this.level.getBlockEntity(host);
            if (neighborTile instanceof ISEPowerPole)
                PowerPoleRenderHelper.notifyChanged((ISEPowerPole) neighborTile);
        }

        super.onSyncDataFromServerArrived(nbt);
    }
}
