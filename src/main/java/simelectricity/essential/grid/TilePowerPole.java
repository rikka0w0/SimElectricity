package simelectricity.essential.grid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.utils.Utils;
import simelectricity.essential.utils.math.SEMathHelper;
import simelectricity.essential.utils.math.Vec3f;


public class TilePowerPole extends SEEnergyTile implements ISEGridTile, ISEPowerPole{
	private BlockPos neighbor1, neighbor2;
	@SideOnly(Side.CLIENT)
    private PowerPoleRenderHelper renderHelper;
	
	@SideOnly(Side.CLIENT)
	protected boolean scheduleBlockRenderUpdateWhenChange(){
		return isSpecial();
	}
	
	protected boolean isSpecial() {
		return (getBlockMetadata() >> 3) == 0;
	}
	
	@SideOnly(Side.CLIENT)
	protected PowerPoleRenderHelper createRenderHelper(){
		PowerPoleRenderHelper helper;
		int rotation = getBlockMetadata() & 7;
		
		if (isSpecial()) {
			helper = new PowerPoleRenderHelper(world, pos, rotation, 2, 3) {
				@Override
				public void updateRenderData(BlockPos... neighborPosList) {
					super.updateRenderData(neighborPosList);
					
					if (connectionInfo.size() < 2)
						return;
					
		    		ConnectionInfo[] connection1 = this.connectionInfo.getFirst();
		    		ConnectionInfo[] connection2 = this.connectionInfo.getLast();
		    		
		    		Vec3f pos = new Vec3f(
		    		3.95F * MathHelper.sin(this.rotation/180F*SEMathHelper.PI) + 0.5F + this.pos.getX(),
		    		this.pos.getY() + 23 -18,
		    		3.95F * MathHelper.cos(this.rotation/180F*SEMathHelper.PI) + 0.5F + this.pos.getZ()
		    		);
		    		
		    		addExtraWire(connection1[1].fixedFrom, pos, 2.5F);
		    		addExtraWire(pos, connection2[1].fixedFrom, 2.5F);
		    		if (PowerPoleRenderHelper.hasIntersection(
		    				connection1[0].fixedFrom, connection2[0].fixedFrom,
		    				connection1[2].fixedFrom, connection2[2].fixedFrom)) {
		    			addExtraWire(connection1[0].fixedFrom, connection2[2].fixedFrom, 2.5F);
		    			addExtraWire(connection1[2].fixedFrom, connection2[0].fixedFrom, 2.5F);
		    		}else {
		    			addExtraWire(connection1[0].fixedFrom, connection2[0].fixedFrom, 2.5F);
		    			addExtraWire(connection1[2].fixedFrom, connection2[2].fixedFrom, 2.5F);
		    		}
				}
			};
			helper.addInsulatorGroup(-0.7F, 5, 0,
					helper.createInsulator(2, 3, -1, 0, -4.5F),
					helper.createInsulator(2, 3, -0.7F, 5, 0),
					helper.createInsulator(2, 3, -1, 0, 4.5F)
					);
			helper.addInsulatorGroup(0.7F, 5, 0,
					helper.createInsulator(2, 3, 1, 0, -4.5F),
					helper.createInsulator(2, 3, 0.7F, 5, 0),
					helper.createInsulator(2, 3, 1, 0, 4.5F)
					);
		}else {
			helper = new PowerPoleRenderHelper(world, pos, rotation, 1, 3);
			helper.addInsulatorGroup(0, 5, 3.95F,
					helper.createInsulator(0, 3, 0, -2, -4.9F),
					helper.createInsulator(0, 3, 0, 5, 3.95F),
					helper.createInsulator(0, 3, 0, -2, 4.9F)
					);
		}

		return helper;
	}
	
	//////////////////////////////
	/////ITransmissionTower
	//////////////////////////////
	@SideOnly(Side.CLIENT)
	@Override
	public void updateRenderInfo() {		
		getRenderHelper().updateRenderData(neighbor1, neighbor2);
		if (scheduleBlockRenderUpdateWhenChange())
			this.markForRenderUpdate();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public PowerPoleRenderHelper getRenderHelper() {
        return renderHelper;
	}
	
	//////////////////////////////
	/////ISEGridTile
	//////////////////////////////
    private ISEGridNode gridNode = null;
	@Override
	public void setGridNode(ISEGridNode gridObj) {this.gridNode = gridObj;}

	@Override
	public ISEGridNode getGridNode() {return gridNode;}

	@Override
	public void onGridNeighborUpdated() {
		neighbor1 = null;
		neighbor2 = null;
		
		ISEGridNode[] neighbors = gridNode.getNeighborList();
		if (neighbors.length == 1) {
			neighbor1 = neighbors[0].getPos();
		}else if (neighbors.length > 1) {
			neighbor1 = neighbors[0].getPos();
			neighbor2 = neighbors[1].getPos();
		}

		this.markTileEntityForS2CSync();
		
		//notifyNeighbor(neighbor1);
		//notifyNeighbor(neighbor2);
	}

	public boolean canConnect() {
		return neighbor1==null || neighbor2==null;
	}
	
	private void notifyNeighbor(BlockPos neighbor) {
		if (neighbor == null)
			return;
		IBlockState state = world.getBlockState(neighbor);
		world.notifyBlockUpdate(neighbor1, state, state, 2);
	}	
	//////////////////////////////
	/////TileEntity
	//////////////////////////////
	@SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared()
    {
        return 100000;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public AxisAlignedBB getRenderBoundingBox(){
    	return INFINITE_EXTENT_AABB;
    }

	/////////////////////////////////////////////////////////
	///Sync
	/////////////////////////////////////////////////////////
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt) {
		Utils.saveToNbt(nbt, "neighbor1", neighbor1);
		Utils.saveToNbt(nbt, "neighbor2", neighbor2);
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
		neighbor1 = Utils.posFromNbt(nbt, "neighbor1");
		neighbor2 = Utils.posFromNbt(nbt, "neighbor2");

		if (renderHelper == null)
			renderHelper = createRenderHelper();
		
		PowerPoleRenderHelper.notifyChanged(this);
		//this.updateRenderInfo();
		
		updateRenderInfo(neighbor1);
		updateRenderInfo(neighbor2);
		
		super.onSyncDataFromServerArrived(nbt);
	}
	
	@SideOnly(value = Side.CLIENT)
	private void updateRenderInfo(BlockPos neighborPos) {
		if (neighborPos == null)
			return;
		
		TileEntity neighbor = world.getTileEntity(neighborPos);
		if (neighbor instanceof ISEPowerPole)
			PowerPoleRenderHelper.notifyChanged((ISEPowerPole)neighbor);
			//((ISEPowerPole)neighbor).updateRenderInfo();
	}
	
	
}
