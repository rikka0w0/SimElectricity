package simelectricity.essential.grid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import simelectricity.api.node.ISEGridNode;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.ISEFacing8;

public abstract class TilePoleBranch extends TilePoleAccessory implements ISEFacing8{
	public TilePoleBranch(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
		super(beType, pos, blockState);
	}

	protected BlockPos neighbor;

    /////////////////////////////////////////////////////////
    /////ISEPowerPole
    /////////////////////////////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockPos[] getNeighborPosArray() {
        return new BlockPos[] {this.neighbor};
    }

    //////////////////////////////
    /////ISEGridTile
    //////////////////////////////
    @Override
    public void onGridNeighborUpdated() {
    	this.host = null;
    	this.neighbor = null;

    	for (ISEGridNode node: this.gridNode.getNeighborList()) {
    		BlockPos nodePos = node.getPos();
    		if (nodePos.below().equals(this.worldPosition))
    			this.host = nodePos;
    		else
    			this.neighbor = nodePos;
    	}

        markTileEntityForS2CSync();
    }

    @Override
    public boolean canConnect(BlockPos toPos) {
    	if (toPos == null) {
    		return this.host == null || this.neighbor == null;
    	}

        return (this.host == null && toPos.below().equals(this.worldPosition)) || this.neighbor == null;
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
    	super.prepareS2CPacketData(nbt);
    	Utils.saveToNbt(nbt, "neighbor", this.neighbor);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
    	this.neighbor = Utils.posFromNbt(nbt, "neighbor");
    	super.onSyncDataFromServerArrived(nbt);

        if (this.neighbor != null) {
            BlockEntity neighborTile = this.level.getBlockEntity(this.neighbor);
            if (neighborTile instanceof ISEPowerPole)
                PowerPoleRenderHelper.notifyChanged((ISEPowerPole) neighborTile);
        }
    }

    //////////////////////////////
    /////BlockEntity
    //////////////////////////////
	public static class Type10kV extends TilePoleBranch {
		public Type10kV(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
			super(beType, pos, blockState);
		}

		@Override
		protected PowerPoleRenderHelper createRenderHelper() {
			PowerPoleRenderHelper helper = new PowerPoleRenderHelper(worldPosition, getRotation(), 1, 3);

			helper.addInsulatorGroup(0, 0.9F, -0.6F,
					helper.createInsulator(0.5F, 1.2F, -0.74F, 0.1F, 0.2F),
					helper.createInsulator(0.5F, 1.2F, 0, 0.1F, 0.2F),
					helper.createInsulator(0.5F, 1.2F, 0.74F, 0.1F, 0.2F)
					);

			return helper;
		}
	}

	public static class Type415V extends TilePoleBranch {
		public Type415V(BlockEntityType<?> beType, BlockPos pos, BlockState blockState) {
			super(beType, pos, blockState);
		}

		@Override
		protected PowerPoleRenderHelper createRenderHelper() {
			PowerPoleRenderHelper helper = new PowerPoleRenderHelper(worldPosition, getRotation(), 1, 4);

            helper.addInsulatorGroup(0, 0.55F, 0,
                    helper.createInsulator(0.25F, 1.2F, -0.9F, 0.5F, 0.25F),
                    helper.createInsulator(0.25F, 1.2F, -0.45F, 0.5F, 0.25F),
                    helper.createInsulator(0.25F, 1.2F, 0.45F, 0.5F, 0.25F),
                    helper.createInsulator(0.25F, 1.2F, 0.9F, 0.5F, 0.25F)
            );

			return helper;
		}
	}
}
