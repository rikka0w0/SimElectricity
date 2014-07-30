package simElectricity.Common.Blocks.TileEntity;

import java.util.List;

import simElectricity.API.Energy;
import simElectricity.API.IUpdateOnWatch;
import simElectricity.API.Util;
import simElectricity.API.EnergyTile.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileTower extends TileEntity implements IUpdateOnWatch, IManualJunction, IConnectable{
	public int facing;
	public int neighborsInfo[] = new int[] {0,-1,0,0,-1,0};
	protected boolean isAddedToEnergyNet = false;
	
	public void addNeighbor(TileEntity te){
		if (neighborsInfo[1]==-1){
		neighborsInfo[0] = te.xCoord;
		neighborsInfo[1] = te.yCoord;
		neighborsInfo[2] = te.zCoord;
		Energy.postTileRejoinEvent(this);
		}else if(neighborsInfo[4]==-1){
			neighborsInfo[3] = te.xCoord;
			neighborsInfo[4] = te.yCoord;
			neighborsInfo[5] = te.zCoord;	
			Energy.postTileRejoinEvent(this);
		}
	}
	
	public void delNeighbor(TileEntity te){
		for (int i=0;i<neighborsInfo.length;i+=3){
			if (neighborsInfo[i] == te.xCoord ||
				neighborsInfo[i+1] == te.yCoord ||
				neighborsInfo[i+2] == te.zCoord){
				neighborsInfo[i] = 0;
				neighborsInfo[i+1] = -1;
				neighborsInfo[i+2] = 0;				
			}	
		}
		Energy.postTileRejoinEvent(this);
	}
	
	public boolean hasVacant(){
		return neighborsInfo[1]==-1||neighborsInfo[4]==-1;
	}
	
	public static double distanceOf(int[] coordinates){
		return Math.sqrt(Math.pow(coordinates[0]-coordinates[3], 2) +
				  	     Math.pow(coordinates[1]-coordinates[4], 2) +
				  	     Math.pow(coordinates[2]-coordinates[5], 2));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public AxisAlignedBB getRenderBoundingBox(){
    	return INFINITE_EXTENT_AABB;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote && !isAddedToEnergyNet) {
            Energy.postTileAttachEvent(this);
            this.isAddedToEnergyNet = true;
        }
    }

    @Override
    public void invalidate() {
        if (!worldObj.isRemote & isAddedToEnergyNet) {
            Energy.postTileDetachEvent(this);
            this.isAddedToEnergyNet = false;
            
    		for(int i=0;i<neighborsInfo.length;i+=3){
    			TileTower neighbor = (TileTower) getWorldObj().getTileEntity(neighborsInfo[i],neighborsInfo[i+1],neighborsInfo[i+2]);
    			if (neighbor!=null){
    				neighbor.delNeighbor(this);
    				Util.updateTileEntityField(neighbor, "neighborsInfo");
    			}
    		}
        }

        super.invalidate();
    }
	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        
        facing = tagCompound.getInteger("facing");
        neighborsInfo = tagCompound.getIntArray("neighborsInfo");
        if (neighborsInfo==null)
        	neighborsInfo = new int[] {0,-1,0,0,-1,0};
        if (neighborsInfo.length!=6)
        	neighborsInfo = new int[] {0,-1,0,0,-1,0};   
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        
        tagCompound.setInteger("facing", facing);
        tagCompound.setIntArray("neighborsInfo", this.neighborsInfo);
    }

	@Override
	public void onWatch() {
		Util.updateTileEntityField(this, "facing");
		Util.updateTileEntityField(this, "neighborsInfo");
	}

	@Override
	public float getResistance() {return 0;}

	@Override
	public boolean canConnectOnSide(ForgeDirection side) {
		return side == ForgeDirection.DOWN;
	}

	@Override
	public void addNeighbors(List<IBaseComponent> list) {
		TileEntity downTile = Util.getTileEntityonDirection(this, ForgeDirection.DOWN);
		if (downTile instanceof IConductor)
			list.add((IBaseComponent) downTile);
		
		TileEntity te;
		if (neighborsInfo[1]!=-1){
			te = worldObj.getTileEntity(neighborsInfo[0], neighborsInfo[1], neighborsInfo[2]);
			if (te instanceof TileTower)
				list.add((IManualJunction) te);
		}

		if(neighborsInfo[4]!=-1){
			te = worldObj.getTileEntity(neighborsInfo[3], neighborsInfo[4], neighborsInfo[5]);
			if (te instanceof TileTower)
				list.add((IManualJunction) te);					
		}
	}

	@Override
	public float getResistance(IBaseComponent neighbor) {
		if (neighbor == Util.getTileEntityonDirection(this, ForgeDirection.DOWN))
			return 0.1F;
		
		if (neighbor instanceof TileTower){
			return (float) (0.2*distanceOf(new int[]{
					((TileTower) neighbor).xCoord,((TileTower) neighbor).yCoord,((TileTower) neighbor).zCoord,
					xCoord,yCoord,zCoord}));
		}
		
		return Float.MAX_VALUE;
	}
}
