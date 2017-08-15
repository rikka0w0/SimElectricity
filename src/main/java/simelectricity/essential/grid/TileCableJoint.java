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
import simelectricity.essential.client.grid.ISETransmissionTower;
import simelectricity.essential.client.grid.TransmissionTowerRenderHelper;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.utils.Utils;

public class TileCableJoint extends SEEnergyTile implements ISECableTile, ISEGridTile, ISETransmissionTower{
	private ISESimulatable cableNode = SEAPI.energyNetAgent.newCable(this, true);
    
    private TransmissionTowerRenderHelper renderHelper;
    private BlockPos neighbor;
    
	//////////////////////////////
	/////TileEntity
	//////////////////////////////
	@SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared()
    {
        return 10000;
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
		
		this.updateRenderInfo();
		
		if (neighbor != null) {
			TileEntity neighborTile = world.getTileEntity(this.neighbor);
			if (neighborTile instanceof ISETransmissionTower)
				((ISETransmissionTower)neighborTile).updateRenderInfo();
		}

		super.onSyncDataFromServerArrived(nbt);
	}
	
	/////////////////////////////////////////////////////////
	///ITransmissionTower
	/////////////////////////////////////////////////////////
	@Override
	public void updateRenderInfo() {
		getRenderHelper().updateRenderData(neighbor, null);
	}
	
	@Override
	public TransmissionTowerRenderHelper getRenderHelper() {
        //Create renderHelper on client side
        if (world.isRemote){
			if (renderHelper == null)
				renderHelper = new TransmissionTowerRenderHelper(this, 2, new double[]{-0.3, 1.17, -0.95, 0.6, 1.45, 0, -0.3, 1.17, 0.95});
        	return renderHelper;
        }else{
        	return null;
        }
	}
	
	@Override
	public int getRotation() {
		return world.getBlockState(pos).getValue(Properties.propertyFacing);
	}
}
