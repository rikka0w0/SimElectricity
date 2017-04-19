package simElectricity.Templates.TileEntity;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.SEAPI;
import simElectricity.API.Client.ITransmissionTower;
import simElectricity.API.Client.ITransmissionTowerRenderHelper;
import simElectricity.API.DataProvider.ISEJunctionData;
import simElectricity.API.EnergyTile.ISEGridNode;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.Tile.ISECableTile;
import simElectricity.API.Tile.ISEGridTile;
import simElectricity.API.Tile.ISETile;
import simElectricity.Templates.Common.TileEntitySE;
import simElectricity.Templates.Utils.Utils;

public class TileCableClamp extends TileEntitySE implements ISETile,ISEJunctionData,ISEGridTile, ITransmissionTower{
    private ISEGridNode gridNode = null;
    private ISESubComponent junction = (ISESubComponent) SEAPI.energyNetAgent.newComponent(this);
    
    private ITransmissionTowerRenderHelper renderHelper;
    private int neighborX, neighborY = -1, neighborZ;
    
    public ForgeDirection facing = ForgeDirection.NORTH;
    
	/////////////////////////////////////////////////////////
	///TileEntitySE
	/////////////////////////////////////////////////////////
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt) {
		super.prepareS2CPacketData(nbt);
		
		nbt.setByte("facing", (byte) facing.ordinal());
		nbt.setInteger("neighborX", this.neighborX);
		nbt.setInteger("neighborY", this.neighborY);
		nbt.setInteger("neighborZ", this.neighborZ);
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
		this.facing = ForgeDirection.getOrientation(nbt.getByte("facing"));
		this.neighborX = nbt.getInteger("neighborX");
		this.neighborY = nbt.getInteger("neighborY");
		this.neighborZ = nbt.getInteger("neighborZ");
		
		if (renderHelper == null)
			renderHelper = SEAPI.clientRender.newTransmissionTowerRenderHelper(this);
		renderHelper.updateRenderData(neighborX, neighborY, neighborZ, 0 ,-1, 0);
		
		super.onSyncDataFromServerArrived(nbt);
	}
	
	@Override
	public boolean attachToEnergyNet() {
		return true;
	}
    
	/////////////////////////////////////////////////////////
	///TileEntity
	/////////////////////////////////////////////////////////
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //Create renderHelper on client side
        if (worldObj.isRemote){
			if (renderHelper == null)
				renderHelper = SEAPI.clientRender.newTransmissionTowerRenderHelper(this);
			renderHelper.updateRenderData(neighborX, neighborY, neighborZ, 0 ,-1, 0);
        	return;
        }        	
	}
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        facing = ForgeDirection.getOrientation(tagCompound.getByte("facing"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("facing", (byte) facing.ordinal());
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

	/////////////////////////////////////////////////////////
	///ISETile
	/////////////////////////////////////////////////////////	
	@Override
	public int getNumberOfComponents() {
		return 1;
	}

	@Override
	public ForgeDirection[] getValidDirections() {
		return new ForgeDirection[]{facing};
	}

	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		return side == facing ? this.junction : null;
	}
	
	/////////////////////////////////////////////////////////
	///ISEJunction
	/////////////////////////////////////////////////////////
	@Override
	public void getNeighbors(List<ISESimulatable> list) {
		TileEntity neighbor = Utils.getTileEntityonDirection(this, facing);
		
        if (neighbor instanceof ISECableTile)
            list.add(((ISECableTile) neighbor).getNode());
        
        if (gridNode != null)
        	list.add(gridNode);
	}

	@Override
	public double getResistance(ISESimulatable neighbor) {
		return 0.1;
	}
	
	/////////////////////////////////////////////////////////
	///ISEGridTile
	/////////////////////////////////////////////////////////
	@Override
	public void setGridNode(ISEGridNode gridNode) {
		this.gridNode = gridNode;
	}

	@Override
	public ISEGridNode getGridNode() {
		return this.gridNode;
	}
	
	@Override
	public boolean canConnect() {
		return neighborY == -1;
	}
	
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
	
	/////////////////////////////////////////////////////////
	///ITransmissionTower
	/////////////////////////////////////////////////////////
	@Override
	public ITransmissionTowerRenderHelper getRenderHelper() {return renderHelper;}
	
	@Override
	public double getInsulatorLength() {return 2;}

	@Override
	public double[] getInsulatorPositionArray() {return new double[]{0.4, 1.45, -0.9, 0.6, 1.7, 0, 0.4, 1.45, 0.9};	}

	@Override
	public int getRotation() {
		switch(this.facing){
		case NORTH: return 4;
		case SOUTH: return 0;
		case WEST: return 6;
		case EAST: return 2;
		default: return 0;	
		}		
	}
}
