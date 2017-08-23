package simelectricity.essential.grid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.utils.Utils;

public class TileCableJoint extends SEEnergyTile implements ISECableTile, ISEGridTile, ISEPowerPole{
	private ISESimulatable cableNode = SEAPI.energyNetAgent.newCable(this, true);
    
	@SideOnly(Side.CLIENT)
    private PowerPoleRenderHelper renderHelper;
    private BlockPos neighbor;
    
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
		return cableNode;
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
		this.neighbor = null;
		f:for (ISESimulatable neighbor : gridNode.getNeighborList()){
			if (neighbor instanceof ISEGridNode){
				ISEGridNode gridNode = (ISEGridNode) neighbor;
				this.neighbor = gridNode.getPos().toImmutable();
				break f;
			}
		}
		
		this.markTileEntityForS2CSync();
	}

	public boolean canConnect() {
		return neighbor == null;
	}
	
	/////////////////////////////////////////////////////////
	///Sync
	/////////////////////////////////////////////////////////
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt) {
		Utils.saveToNbt(nbt, "neighbor", neighbor);
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
		this.neighbor = Utils.posFromNbt(nbt, "neighbor");
		
		if (renderHelper == null)
			renderHelper = createRenderHelper();
		
		PowerPoleRenderHelper.notifyChanged(this);
		//this.updateRenderInfo();
		
		if (neighbor != null) {
			TileEntity neighborTile = world.getTileEntity(this.neighbor);
			if (neighborTile instanceof ISEPowerPole)
				PowerPoleRenderHelper.notifyChanged((ISEPowerPole)neighborTile);
				//((ISEPowerPole)neighborTile).updateRenderInfo();
		}
		
		super.onSyncDataFromServerArrived(nbt);
	}
	
	/////////////////////////////////////////////////////////
	///ITransmissionTower
	/////////////////////////////////////////////////////////
	@Override
	@SideOnly(Side.CLIENT)
	public void updateRenderInfo() {
		renderHelper.updateRenderData(neighbor);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public PowerPoleRenderHelper getRenderHelper() {
		return renderHelper;
	}
	
	@SideOnly(Side.CLIENT)
	protected PowerPoleRenderHelper createRenderHelper() {
		//Create renderHelper on client side
		int rotation = world.getBlockState(pos).getValue(Properties.propertyFacing);
		PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(world, pos, rotation, 1, 3);
		renderHelper.addInsulatorGroup(0.6F, 1.45F, 0F, 
				renderHelper.createInsulator(0, 2, -0.3F, 1.17F, -0.95F),
				renderHelper.createInsulator(0, 2, 0.6F, 1.45F, 0F),
				renderHelper.createInsulator(0, 2, -0.3F, 1.17F, 0.95F));
		
		return renderHelper;
	}
}
