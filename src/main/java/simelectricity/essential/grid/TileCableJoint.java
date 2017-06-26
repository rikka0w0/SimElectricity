package simelectricity.essential.grid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.grid.render.TransmissionTowerRenderHelper;

public class TileCableJoint extends SEEnergyTile implements ISECableTile, ISEGridTile, ISETransmissionTower{
	private ISESimulatable cableNode = SEAPI.energyNetAgent.newCable(this, true);
    
    private TransmissionTowerRenderHelper renderHelper;
    private int neighborX, neighborY = -1, neighborZ;
    
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
	public boolean canConnectOnSide(ForgeDirection direction) {
		return direction == ForgeDirection.DOWN;
	}

	@Override
	public boolean isGridLinkEnabled() {
		return true;
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
		this.neighborY = -1;
		f:for (ISESimulatable neighbor : gridNode.getNeighborList()){
			if (neighbor instanceof ISEGridNode){
				ISEGridNode gridNode = (ISEGridNode) neighbor;
				this.neighborX = gridNode.getXCoord();
				this.neighborY = gridNode.getYCoord();
				this.neighborZ = gridNode.getZCoord();
				break f;
			}
		}
		
		this.markTileEntityForS2CSync();
	}
	
	public boolean canConnect() {
		return neighborY == -1;
	}
	
	/////////////////////////////////////////////////////////
	///Sync
	/////////////////////////////////////////////////////////
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt) {
		nbt.setInteger("neighborX", neighborX);
		nbt.setInteger("neighborY", neighborY);
		nbt.setInteger("neighborZ", neighborZ);
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
		neighborX = nbt.getInteger("neighborX");
		neighborY = nbt.getInteger("neighborY");
		neighborZ = nbt.getInteger("neighborZ");
		
		this.updateRenderInfo();
		
		TileEntity neighbor = worldObj.getTileEntity(neighborX, neighborY, neighborZ);
		if (neighbor instanceof ISETransmissionTower)
			((ISETransmissionTower)neighbor).updateRenderInfo();
		
		super.onSyncDataFromServerArrived(nbt);
	}
	
	/////////////////////////////////////////////////////////
	///ITransmissionTower
	/////////////////////////////////////////////////////////
	@Override
	public void updateRenderInfo() {
		getRenderHelper().updateRenderData(neighborX, neighborY, neighborZ, 0 ,-1, 0);
	}
	
	@Override
	public TransmissionTowerRenderHelper getRenderHelper() {
        //Create renderHelper on client side
        if (worldObj.isRemote){
			if (renderHelper == null)
				renderHelper = new TransmissionTowerRenderHelper(this, 2, new double[]{-0.3, 1.17, -0.95, 0.6, 1.45, 0, -0.3, 1.17, 0.95});
        	return renderHelper;
        }else{
        	return null;
        }
	}
	
	@Override
	public int getRotation() {
		return getBlockMetadata() & 7;
	}
}
