package simelectricity.essential.grid;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
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
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.pos, rotation, 1, 3);
            renderHelper.addInsulatorGroup(0F, 1.45F, 0.6F,
                    renderHelper.createInsulator(0, 2, -0.95F, 1.17F, -0.3F),
                    renderHelper.createInsulator(0, 2, 0F, 1.45F, 0.6F),
                    renderHelper.createInsulator(0, 2, 0.95F, 1.17F, -0.3F));

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
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.pos, rotation, 1, 4);
            renderHelper.addInsulatorGroup(0F, 1.45F, 0.6F,
            		renderHelper.createInsulator(0, 1.2F, -0.75F, 0.65F, -0.275F),
            		renderHelper.createInsulator(0, 1.2F, -0.275F, 0.9F, 0.35F),
            		renderHelper.createInsulator(0, 1.2F, 0.275F, 0.9F, 0.35F),
            		renderHelper.createInsulator(0, 1.2F, 0.75F, 0.65F, -0.275F));

            return renderHelper;
        }
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
