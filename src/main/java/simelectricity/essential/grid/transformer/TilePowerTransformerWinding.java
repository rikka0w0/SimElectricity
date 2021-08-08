package simelectricity.essential.grid.transformer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.api.SEAPI;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;

public abstract class TilePowerTransformerWinding extends SEMultiBlockGridTile{
    public TilePowerTransformerWinding(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

	@Override
    @OnlyIn(Dist.CLIENT)
    public BlockPos getAccessoryPos() {
    	return null;
    }

    //////////////////////////////
    /////IMultiBlockTile
    //////////////////////////////
    @Override
    public void onStructureCreating() {
        gridNode = SEAPI.energyNetAgent.newGridNode(this.worldPosition, 3);
        SEAPI.energyNetAgent.attachGridNode(this.level, this.gridNode);
    }

    @Override
    public void onStructureCreated() {
    }

    @Override
    public void onStructureRemoved() {
        SEAPI.energyNetAgent.detachGridNode(this.level, this.gridNode);
    }

    public static class Primary extends TilePowerTransformerWinding {
        public Primary(BlockPos pos, BlockState blockState) {
    		super(pos, blockState);
    	}

        @Override
        public void onStructureCreated() {
            BlockPos pos = this.mbInfo.getPartPos(EnumPowerTransformerBlockType.Secondary.offset);
            TilePowerTransformerWinding.Secondary secondaryTile = (TilePowerTransformerWinding.Secondary) this.level.getBlockEntity(pos);
            SEAPI.energyNetAgent.makeTransformer(this.level, getGridNode(), secondaryTile.getGridNode(), 1, 1 / 3.5);
        }

        @Override
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.worldPosition, this.getFacing(), this.isMirrored(), 1, 3);

            renderHelper.addInsulatorGroup(0F, 2.8F, 0F,
                    renderHelper.createInsulator(0, 2, 1.5F, 2.8F, 0),
                    renderHelper.createInsulator(0, 2, 0, 2.8F, 0),
                    renderHelper.createInsulator(0, 2, -1.5F, 2.8F, 0));
            return renderHelper;
        }
    }

    public static class Secondary extends TilePowerTransformerWinding {
        public Secondary(BlockPos pos, BlockState blockState) {
    		super(pos, blockState);
    	}

        @Override
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.worldPosition, this.getFacing(), this.isMirrored(), 1, 3);
            renderHelper.addInsulatorGroup(0, 1.8F, 0,
                    renderHelper.createInsulator(0, 0.5F, 0.8F, 2.1F, 0),
                    renderHelper.createInsulator(0, 0.5F, 0, 2.1F, 0),
                    renderHelper.createInsulator(0, 0.5F, -0.8F, 2.1F, 0));
            return renderHelper;
        }
    }
}
