package simElectricity.Templates.TileEntity;

import java.util.List;

import simElectricity.API.INetworkEventHandler;
import simElectricity.API.SEAPI;
import simElectricity.API.EnergyTile.ISEGridNode;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.Tile.ISEGridTile;
import simElectricity.Templates.Client.Render.ITransmissionTower;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileTransmissionTower extends TileEntity implements ISEGridTile, INetworkEventHandler, ITransmissionTower{
	public int facing;
	public int neighborList[] = new int[] { 0, -1, 0, 0, -1, 0 };
	
	//ISEGridTile
    private boolean registered = false;
    private ISEGridNode gridNode = null;
	@Override
	public void setGridNode(ISEGridNode gridObj) {this.gridNode = gridObj;}

	@Override
	public ISEGridNode getGridNode() {return gridNode;}

	@Override
	public void onGridNeighborUpdated() {
		neighborList = new int[] { 0, -1, 0, 0, -1, 0 };
		
		int i=0;
    	loop: for (ISESimulatable neighbor : gridNode.getNeighborList()){
    		if (neighbor instanceof ISEGridNode){
				ISEGridNode neighbor1 = (ISEGridNode)neighbor;
				neighborList[i] = neighbor1.getXCoord();
				neighborList[i+1] = neighbor1.getYCoord();
				neighborList[i+2] = neighbor1.getZCoord();
				
				i+=3;
				if (i>4)
					break loop;
    		}
    	}
		SEAPI.networkManager.updateNetworkFields(this);
	}

	@Override
	public boolean canConnect() {
		for (int i=0; i<neighborList.length; i+=3){
			if (neighborList[i+1] == -1)
				return true;
		}
		return false;
	}
	
    //INetworkEventHandler --------------------------------------------------------------------------------
	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {

	}

	@Override
	public void addNetworkFields(List fields) {
		fields.add("neighborList");
		fields.add("facing");
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	//ITransmissionTower
	@Override
	public int[] getNeighborCoordArray() {return neighborList;}

	@Override
	public double getInsulatorLength() {return 2;}
	
	@Override
	public double getWireTension() {return 3;}
	
	@Override
	public double[] getInsulatorPositionArray() {
		switch (getBlockMetadata()){
		case 0: return new double[]{-1, 18, -4.5, -0.7, 23, 0, -1, 18, 4.5,
				   					1, 18, -4.5, 0.7, 23, 0, 1, 18, 4.5};
		case 1:	return new double[]{0, 16, -4.5, 0, 23, 4, 0, 16, 4.5};
		default: return null;
		}

	}

	@Override
	public int getRotation() {return facing;}
	
	
	//TileEntity
	@Override
    public void updateEntity() {
        super.updateEntity();
        
        //No client side operation
        if (worldObj.isRemote)
        	return;
        
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
