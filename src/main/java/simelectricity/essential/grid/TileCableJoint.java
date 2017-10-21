package simelectricity.essential.grid;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.properties.Properties;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;

public abstract class TileCableJoint extends TilePoleAccessory implements ISECableTile {
    private final ISESimulatable cableNode = SEAPI.energyNetAgent.newCable(this, true);
    
    public static class Type10kV extends TileCableJoint {
        @Override
        @Nonnull
        @SideOnly(Side.CLIENT)
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            int rotation = this.world.getBlockState(this.pos).getValue(Properties.facing3bit);
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.world, this.pos, rotation, 1, 3);
            renderHelper.addInsulatorGroup(0.6F, 1.45F, 0F,
                    renderHelper.createInsulator(0, 2, -0.3F, 1.17F, -0.95F),
                    renderHelper.createInsulator(0, 2, 0.6F, 1.45F, 0F),
                    renderHelper.createInsulator(0, 2, -0.3F, 1.17F, 0.95F));

            return renderHelper;
        }
    }
    
    public static class Type415V extends TileCableJoint {
        @Override
        @Nonnull
        @SideOnly(Side.CLIENT)
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            int rotation = this.world.getBlockState(this.pos).getValue(Properties.facing3bit);
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.world, this.pos, rotation, 1, 4);
            renderHelper.addInsulatorGroup(0.6F, 1.45F, 0F,
            		renderHelper.createInsulator(0, 1.2F, -0.275F, 0.65F, -0.75F),
            		renderHelper.createInsulator(0, 1.2F, 0.35F, 0.9F, -0.275F),
            		renderHelper.createInsulator(0, 1.2F, 0.35F, 0.9F, 0.275F),
            		renderHelper.createInsulator(0, 1.2F, -0.275F, 0.65F, 0.75F));

            return renderHelper;
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
	public void updateRenderInfo() {
    	
    }
    
    @Override
    public boolean canConnect(BlockPos toPos) {
        return this.host == null;
    }
    
    /////////////////////////////////////////////////////////
    ///ISECableTile
    /////////////////////////////////////////////////////////
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
    public boolean canConnectOnSide(EnumFacing direction) {
        return direction == EnumFacing.DOWN;
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
