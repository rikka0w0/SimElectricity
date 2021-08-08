package simelectricity.essential.grid;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.DirHorizontal8;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;

public abstract class TileCableJoint extends TilePoleAccessory implements ISECableTile {
    public TileCableJoint(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

	private final ISESimulatable cableNode = SEAPI.energyNetAgent.newCable(this, true);

    public static class Type10kV extends TileCableJoint {
        public Type10kV(BlockPos pos, BlockState blockState) {
			super(pos, blockState);
		}

		@Override
        @Nonnull
        @OnlyIn(Dist.CLIENT)
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.worldPosition, getHorizontalDirection(), 1, 3);
            renderHelper.addInsulatorGroup(0F, 1.45F, 0.6F,
                    renderHelper.createInsulator(0, 2, -0.95F, 1.17F, -0.3F),
                    renderHelper.createInsulator(0, 2, 0F, 1.45F, 0.6F),
                    renderHelper.createInsulator(0, 2, 0.95F, 1.17F, -0.3F));

            return renderHelper;
        }
    }

    public static class Type415V extends TileCableJoint {
        public Type415V(BlockPos pos, BlockState blockState) {
			super(pos, blockState);
		}

		@Override
        @Nonnull
        @OnlyIn(Dist.CLIENT)
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.worldPosition, getHorizontalDirection(), 1, 4);
            renderHelper.addInsulatorGroup(0F, 1.45F, 0.6F,
            		renderHelper.createInsulator(0, 1.2F, -0.75F, 0.65F, -0.275F),
            		renderHelper.createInsulator(0, 1.2F, -0.275F, 0.9F, 0.35F),
            		renderHelper.createInsulator(0, 1.2F, 0.275F, 0.9F, 0.35F),
            		renderHelper.createInsulator(0, 1.2F, 0.75F, 0.65F, -0.275F));

            return renderHelper;
        }
    }

    protected DirHorizontal8 getHorizontalDirection() {
    	return this.level.getBlockState(this.worldPosition).getValue(DirHorizontal8.prop);
    }

    /////////////////////////////////////////////////////////
    ///ISECableTile
    /////////////////////////////////////////////////////////
    @Override
    public void setColor(int newColor) {

    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public ISESimulatable getNode() {
        return this.cableNode;
    }

    @Override
    public boolean canConnectOnSide(Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    public boolean isGridLinkEnabled() {
        return true;
    }

    @Override
    public boolean hasShuntResistance() {
        return false;
    }

    @Override
    public double getShuntResistance() {
        return 0;
    }
}
