package simElectricity.Templates.TileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.SEAPI;
import simElectricity.API.Client.ITransmissionTower;
import simElectricity.API.Client.ITransmissionTowerRenderHelper;
import simElectricity.API.EnergyTile.ISEGridNode;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.Tile.ISECableTile;
import simElectricity.API.Tile.ISEGridTile;
import simElectricity.Templates.Common.TileEntitySE;

public class TileCableClamp extends TileEntitySE implements ISECableTile, ISEGridTile, ITransmissionTower{
	private ISESimulatable cableNode = SEAPI.energyNetAgent.newCable(this, true);
	private ISEGridNode gridNode = null;
    
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
		return direction == facing;
	}

	@Override
	public boolean isGridLinkEnabled() {
		return true;
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
