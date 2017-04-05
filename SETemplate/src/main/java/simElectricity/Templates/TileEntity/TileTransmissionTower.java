package simElectricity.Templates.TileEntity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import simElectricity.API.INetworkEventHandler;
import simElectricity.API.SEAPI;
import simElectricity.API.Client.ITransmissionTower;
import simElectricity.API.Client.ITransmissionTowerRenderHelper;
import simElectricity.API.EnergyTile.ISEGridNode;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.Tile.ISEGridTile;



public class TileTransmissionTower extends TileEntity implements ISEGridTile, INetworkEventHandler, ITransmissionTower{
	public int facing;
	public int neighborCoords[] = new int[] { 0, -1, 0, 0, -1, 0 };
	private ITransmissionTowerRenderHelper renderHelper;

	@Override
	public ITransmissionTowerRenderHelper getRenderHelper() {return renderHelper;}
	
	@Override
	public double getInsulatorLength() {return 2;}
	
	@Override
	public double[] getInsulatorPositionArray() {
		switch (getBlockMetadata()){
		case 0: return new double[]{-1, 18, -4.5, -0.7, 23, 0, -1, 18, 4.5,
				   					1, 18, -4.5, 0.7, 23, 0, 1, 18, 4.5};
		case 1:	return new double[]{0, 16, -4.9, 0, 23, 3.95, 0, 16, 4.9};
		default: return null;
		}

	}

	public int getRotation() {return facing;}
	
	//ISEGridTile
    private boolean registered = false;
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
		SEAPI.networkManager.updateNetworkFields(this);
		
		TileEntity neighbor1 = worldObj.getTileEntity(neighborCoords[0], neighborCoords[1], neighborCoords[2]);
		TileEntity neighbor2 = worldObj.getTileEntity(neighborCoords[3], neighborCoords[4], neighborCoords[5]);
		if (neighbor1!=null)
			SEAPI.networkManager.updateNetworkFields(neighbor1);
		if (neighbor2!=null)
			SEAPI.networkManager.updateNetworkFields(neighbor2);
	}

	@Override
	public boolean canConnect() {
		for (int i=0; i<neighborCoords.length; i+=3){
			if (neighborCoords[i+1] == -1)
				return true;
		}
		return false;
	}
	
    //INetworkEventHandler --------------------------------------------------------------------------------
	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {
		if (worldObj.isRemote){
			if (renderHelper == null)
				renderHelper = SEAPI.clientRender.newTransmissionTowerRenderHelper(this);
			renderHelper.updateRenderData(neighborCoords);

		}
	}

	@Override
	public void addNetworkFields(List fields) {
		fields.add("neighborCoords");
		fields.add("facing");
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	
	
	//TileEntity
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //No client side operation
        if (worldObj.isRemote){
			if (renderHelper == null)
				renderHelper = SEAPI.clientRender.newTransmissionTowerRenderHelper(this);
			renderHelper.updateRenderData(neighborCoords);
        	return;
        }
        	
        
        if (!registered){
        	SEAPI.energyNetAgent.attachTile(this);
            registered = true;
        }
        	
	}
	
    @Override
    public void invalidate() {
    	super.invalidate();
    	
    	//No client side operation
        if (worldObj.isRemote)
        	return;
        
        if (registered){
        	SEAPI.energyNetAgent.detachTile(this);
            registered = false;        	
        }
    }
	
    @Override
    public void onChunkUnload(){
    	invalidate();
    }   
	
	@Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        facing = tagCompound.getInteger("facing");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setInteger("facing", facing);
    }
    
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
}
