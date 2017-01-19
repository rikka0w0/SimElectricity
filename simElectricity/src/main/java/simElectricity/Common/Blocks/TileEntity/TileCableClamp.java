package simElectricity.Common.Blocks.TileEntity;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import simElectricity.API.Energy;
import simElectricity.API.IHVTower;
import simElectricity.API.INetworkEventHandler;
import simElectricity.API.Util;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.EnergyTile.ISEConductor;
import simElectricity.API.EnergyTile.ISEGridNode;
import simElectricity.API.EnergyTile.ISEGridTile;
import simElectricity.API.EnergyTile.ISEJunction;
import simElectricity.API.EnergyTile.ISESimulatable;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.EnergyTile.ISETile;

public class TileCableClamp extends TileEntitySE implements ISETile,ISEJunction,ISEGridTile,INetworkEventHandler, IHVTower{
    private boolean registered = false;
    private ISEGridNode gridNode = null;
	
    public int[] neighbor =  new int[]{0,-1,0};
    
    @Override
    public boolean attachToEnergyNet(){
    	return true;
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

	
	//IHVTower ---------------------------------------------------------------------
	public int facing = 0;
	
	@Override
	public float[] offsetArray() {
		return new float[]{
				-0.8F, 1.2F, 0.4F,
				0, 1.5F, 0.6F,
				0.8F, 1.2F, 0.4F};
	}

	@Override
	public int[] getNeighborInfo() {
		return neighbor;
	}

	@Override
	public int getFacing() {
		return facing;
	}

	@Override
	public float getWireTension() {
		return 0.06F;
	}
	
	

	//ISETile ----------------------------------------------------------------------
	@Override
	public int getNumberOfComponents() {
		return 1;
	}

	@Override
	public ForgeDirection[] getValidDirections() {
		return new ForgeDirection[]{ForgeDirection.getOrientation(facing)};
	}

	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		if (side == ForgeDirection.getOrientation(facing))
			return this;
		return null;
	}

	//ISEJunction ------------------------------------------------------------------
	@Override
	public void getNeighbors(List<ISESimulatable> list) {
		TileEntity neighbor = Util.getTileEntityonDirection(this, ForgeDirection.getOrientation(facing));
		
        if (neighbor instanceof ISEConductor)
            list.add((ISEConductor) neighbor);
        
        if (gridNode != null)
        	list.add(gridNode);
	}

	@Override
	public double getResistance(ISESimulatable neighbor) {
		return 0.1;
	}

	
	//ISEGridTile-----------------------------------------------------------------------------------------
	@Override
	public void setGridNode(ISEGridNode gridNode) {
		this.gridNode = gridNode;
	}

	@Override
	public ISEGridNode getGridNode() {
		return this.gridNode;
	}
	
	@Override
	public void onGridNeighborUpdated() {
		this.neighbor =  new int[]{0,-1,0};
		f:for (ISESimulatable neighbor : gridNode.getNeighborList()){
			if (neighbor instanceof ISEGridNode){
    			int neighborMeta = worldObj.getBlockMetadata(((ISEGridNode) neighbor).getXCoord(), ((ISEGridNode) neighbor).getYCoord(), ((ISEGridNode) neighbor).getZCoord());
    			TileEntity neighborTile = worldObj.getTileEntity(((ISEGridNode) neighbor).getXCoord(), ((ISEGridNode) neighbor).getYCoord(), ((ISEGridNode) neighbor).getZCoord());
    				
				if (neighborTile instanceof TileTower && neighborMeta == 1 && ((ISEGridNode) neighbor).getYCoord() == this.yCoord + 2){
					break f;
				}
				
				ISEGridNode gridNode = (ISEGridNode) neighbor;
				this.neighbor =  new int[]{gridNode.getXCoord(), gridNode.getYCoord(), gridNode.getZCoord()};
			}
		}
		
		
		Util.networkManager.updateNetworkFields(this);
	}

	@Override
	public boolean canConnect() {
		return neighbor[1] == -1;
	}

	
	
	//INetworkEventHandler ----------------------------------------------------------------------------------------
	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {
		
	}

	@Override
	public void addNetworkFields(List fields) {
		fields.add("neighbor");
	}
}
