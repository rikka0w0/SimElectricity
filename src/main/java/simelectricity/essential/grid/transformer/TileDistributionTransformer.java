package simelectricity.essential.grid.transformer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEMultiBlockEnergyTile;

public abstract class TileDistributionTransformer extends SEMultiBlockGridTile{
	protected abstract BlockPos getComplementPos();
	
    @Override
    public void onGridNeighborUpdated() {
    	neighbor = null;
    	BlockPos complementPos = getComplementPos();
    	for (ISEGridNode neighbor: this.gridNode.getNeighborList()) {
    		BlockPos pos = neighbor.getPos();
    		if (pos.equals(complementPos))
    			continue;
    		
    		this.neighbor = neighbor.getPos();
    	}

        markTileEntityForS2CSync();
    }
	
	public static class Pole415V extends TileDistributionTransformer {
		public final static Vec3i rightPos = new Vec3i(5, 4, 0);
		public final static Vec3i leftPos = new Vec3i(0, 4, 0);

		@Override
		protected void onStructureCreating() {
	        gridNode = SEAPI.energyNetAgent.newGridNode(this.pos, 4);
	        SEAPI.energyNetAgent.attachGridNode(this.world, this.gridNode);
		}
		
		@Override
		public void onStructureCreated() {
			if (this.mbInfo.isPart(Pole415V.rightPos)) {
				BlockPos compPos = this.mbInfo.getPartPos(Pole415V.leftPos);
				Pole415V comp = (Pole415V) this.world.getTileEntity(compPos);
				SEAPI.energyNetAgent.connectGridNode(this.world, gridNode, comp.gridNode, 0.1F);
			}
		}

		@Override
		protected BlockPos getComplementPos() {
			if (mbInfo.isPart(rightPos))
				return mbInfo.getPartPos(leftPos);
			else
				return mbInfo.getPartPos(rightPos);
		}
		
        @Override
        @SideOnly(Side.CLIENT)
        protected PowerPoleRenderHelper createRenderHelper() {
            PowerPoleRenderHelper helper = new PowerPoleRenderHelper(this.world, this.pos, PowerPoleRenderHelper.facing2rotation(mbInfo.facing) - 2, mbInfo.mirrored, 1, 4);
            helper.addInsulatorGroup(0, 0.55F, 0,
                    helper.createInsulator(0, 1.2F, 0, 0.3F, -0.9F),
                    helper.createInsulator(0, 1.2F, 0, 0.3F, -0.45F),
                    helper.createInsulator(0, 1.2F, 0, 0.3F, 0.45F),
                    helper.createInsulator(0, 1.2F, 0, 0.3F, 0.9F)
            );
            return helper;
        }
	}
	
	public static class Pole10kV extends TileDistributionTransformer {
		public final static Vec3i rightPos = new Vec3i(5, 6, 0);
		public final static Vec3i leftPos = new Vec3i(0, 6, 0);
		
		@Override
		protected void onStructureCreating() {
	        gridNode = SEAPI.energyNetAgent.newGridNode(this.pos, 3);
	        SEAPI.energyNetAgent.attachGridNode(this.world, this.gridNode);
		}
		
		@Override
		public void onStructureCreated() {
			if (this.mbInfo.isPart(Pole10kV.rightPos)) {
				BlockPos secPos = this.mbInfo.getPartPos(Pole415V.rightPos);
				Pole415V secondaryTile = (Pole415V) this.world.getTileEntity(secPos);
				SEAPI.energyNetAgent.makeTransformer(this.world, gridNode, secondaryTile.gridNode, 1, 415F / 10000F);
				
				BlockPos compPos = this.mbInfo.getPartPos(Pole10kV.leftPos);
				Pole10kV comp = (Pole10kV) this.world.getTileEntity(compPos);
				SEAPI.energyNetAgent.connectGridNode(this.world, gridNode, comp.gridNode, 0.1F);
			}
		}
		
		@Override
		protected BlockPos getComplementPos() {
			if (mbInfo.isPart(rightPos))
				return mbInfo.getPartPos(leftPos);
			else
				return mbInfo.getPartPos(rightPos);
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		protected PowerPoleRenderHelper createRenderHelper() {
            PowerPoleRenderHelper helper = new PowerPoleRenderHelper(world, pos, PowerPoleRenderHelper.facing2rotation(mbInfo.facing) - 2, mbInfo.mirrored, 1, 3);
            helper.addInsulatorGroup(0, 0.5F, 0,
                    helper.createInsulator(0, 1.2F, 0, 0.55F, -0.74F),
                    helper.createInsulator(0, 1.2F, 0, 1.5F, 0),
                    helper.createInsulator(0, 1.2F, 0, 0.55F, 0.74F)
            );
            return helper;
		}
	}

    @Override
    public void onStructureRemoved() {
        SEAPI.energyNetAgent.detachGridNode(this.world, this.gridNode);
    }
	
	public static class PlaceHolder extends SEMultiBlockEnergyTile{
	    @Override
	    public void onLoad() {}
		
		@Override
		public void onStructureCreated() {}

		@Override
		public void onStructureRemoved() {}

		@Override
		protected void onStructureCreating() {}
	}
}
