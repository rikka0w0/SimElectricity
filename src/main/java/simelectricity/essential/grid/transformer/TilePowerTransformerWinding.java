package simelectricity.essential.grid.transformer;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;

public abstract class TilePowerTransformerWinding extends SEMultiBlockGridTile{
    @Override
    @SideOnly(Side.CLIENT)
    public BlockPos getAccessoryPos() {
    	return null;
    }
	
    //////////////////////////////
    /////IMultiBlockTile
    //////////////////////////////
    @Override
    public void onStructureCreating() {
        gridNode = SEAPI.energyNetAgent.newGridNode(this.pos, 3);
        SEAPI.energyNetAgent.attachGridNode(this.world, this.gridNode);
    }

    @Override
    public void onStructureCreated() {
    }

    @Override
    public void onStructureRemoved() {
        SEAPI.energyNetAgent.detachGridNode(this.world, this.gridNode);
    }

    public static class Primary extends TilePowerTransformerWinding {
        @Override
        public void onStructureCreated() {
            BlockPos pos = this.mbInfo.getPartPos(EnumPowerTransformerBlockType.Secondary.offset);
            TilePowerTransformerWinding.Secondary secondaryTile = (TilePowerTransformerWinding.Secondary) this.world.getTileEntity(pos);
            SEAPI.energyNetAgent.makeTransformer(this.world, getGridNode(), secondaryTile.getGridNode(), 1, 1 / 3.5);
        }

        @Override
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.pos, this.getFacing(), this.isMirrored(), 1, 3);
            
            renderHelper.addInsulatorGroup(0F, 2.8F, 0F,
                    renderHelper.createInsulator(0, 2, 1.5F, 2.8F, 0),
                    renderHelper.createInsulator(0, 2, 0, 2.8F, 0),
                    renderHelper.createInsulator(0, 2, -1.5F, 2.8F, 0));
            return renderHelper;
        }
    }

    public static class Secondary extends TilePowerTransformerWinding {
        @Override
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.pos, this.getFacing(), this.isMirrored(), 1, 3);
            renderHelper.addInsulatorGroup(0, 1.8F, 0,
                    renderHelper.createInsulator(0, 0.5F, 0.8F, 2.1F, 0),
                    renderHelper.createInsulator(0, 0.5F, 0, 2.1F, 0),
                    renderHelper.createInsulator(0, 0.5F, -0.8F, 2.1F, 0));
            return renderHelper;
        }
    }
}
