package simelectricity.essential.grid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.grid.render.TransmissionTowerRenderHelper;


public class TileTransmissionTower extends SEEnergyTile implements ISEGridTile, ISETransmissionTower{
	private int neighborCoords[] = new int[] { 0, -1, 0, 0, -1, 0 };
	private TransmissionTowerRenderHelper renderHelper;

	//////////////////////////////
	/////ITransmissionTower
	//////////////////////////////
	@Override
	public void updateRenderInfo() {
		getRenderHelper().updateRenderData(neighborCoords[0],neighborCoords[1],neighborCoords[2],neighborCoords[3],neighborCoords[4],neighborCoords[5]);
		if ((getBlockMetadata()&8) == 0)
			this.markForRenderUpdate();
	}
	
	@Override
	public TransmissionTowerRenderHelper getRenderHelper() {
        //Create renderHelper on client side
        if (worldObj.isRemote){
			if (renderHelper == null)
				renderHelper = new TransmissionTowerRenderHelper(this,2,
						(getBlockMetadata()&8) == 0
						?
						new double[]{-1, 18-18.0, -4.5, -0.7, 23-18.0, 0, -1, 18-18.0, 4.5,
										1, 18-18.0, -4.5, 0.7, 23-18.0, 0, 1, 18-18.0, 4.5}
						:
						new double[]{0, 16-18.0, -4.9, 0, 23-18.0, 3.95, 0, 16-18.0, 4.9}
						);
        	return renderHelper;
        }else{
        	return null;
        }
	}

	@Override
	public int getRotation() {
		return getBlockMetadata() & 7;
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
		neighborCoords = new int[] { 0, -1, 0, 0, -1, 0 };
		
		int i=0;
    	loop: for (ISESimulatable neighbor : gridNode.getNeighborList()){
    		if (neighbor instanceof ISEGridNode){
				ISEGridNode neighbor1 = (ISEGridNode)neighbor;
				neighborCoords[i] = neighbor1.getXCoord();
				neighborCoords[i+1] = neighbor1.getYCoord();
				neighborCoords[i+2] = neighbor1.getZCoord();
				
				i+=3;
				if (i>4)
					break loop;
    		}
    	}
		this.markTileEntityForS2CSync();
		
		worldObj.markBlockForUpdate(neighborCoords[0], neighborCoords[1], neighborCoords[2]);
		worldObj.markBlockForUpdate(neighborCoords[3], neighborCoords[4], neighborCoords[5]);
	}

	public boolean canConnect() {
		for (int i=0; i<neighborCoords.length; i+=3){
			if (neighborCoords[i+1] == -1)
				return true;
		}
		return false;
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
		nbt.setIntArray("neighborCoords", neighborCoords);
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
		neighborCoords = nbt.getIntArray("neighborCoords");

		this.updateRenderInfo();
		
		TileEntity neighbor = worldObj.getTileEntity(neighborCoords[0],neighborCoords[1],neighborCoords[2]);
		if (neighbor instanceof ISETransmissionTower)
			((ISETransmissionTower)neighbor).updateRenderInfo();
		
		neighbor = worldObj.getTileEntity(neighborCoords[3],neighborCoords[4],neighborCoords[5]);
		if (neighbor instanceof ISETransmissionTower)
			((ISETransmissionTower)neighbor).updateRenderInfo();
		
		super.onSyncDataFromServerArrived(nbt);
	}
}
