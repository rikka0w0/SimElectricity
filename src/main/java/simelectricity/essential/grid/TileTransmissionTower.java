package simelectricity.essential.grid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.client.grid.ISETransmissionTower;
import simelectricity.essential.client.grid.TransmissionTowerRenderHelper;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.utils.Utils;


public class TileTransmissionTower extends SEEnergyTile implements ISEGridTile, ISETransmissionTower{
	private BlockPos neighbor1, neighbor2;
	private TransmissionTowerRenderHelper renderHelper;
	
	@SideOnly(Side.CLIENT)
	protected int getTypeFromMeta(){
		return getBlockMetadata() >> 3;
	}
	
	@SideOnly(Side.CLIENT)
	protected TransmissionTowerRenderHelper createRenderHelper(){
		return new TransmissionTowerRenderHelper(this,2,
				getTypeFromMeta() == 0
				?
				new double[]{-1, 18-18.0, -4.5, -0.7, 23-18.0, 0, -1, 18-18.0, 4.5,
								1, 18-18.0, -4.5, 0.7, 23-18.0, 0, 1, 18-18.0, 4.5}
				:
				new double[]{0, 16-18.0, -4.9, 0, 23-18.0, 3.95, 0, 16-18.0, 4.9}
				);
	}
	
	//////////////////////////////
	/////ITransmissionTower
	//////////////////////////////
	@SideOnly(Side.CLIENT)
	@Override
	public void updateRenderInfo() {
		getRenderHelper().updateRenderData(neighbor1, neighbor2);
		if (getTypeFromMeta() == 0)
			this.markForRenderUpdate();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public TransmissionTowerRenderHelper getRenderHelper() {
        //Create renderHelper on client side
        if (world.isRemote){
			if (renderHelper == null)
				renderHelper = createRenderHelper();
        	return renderHelper;
        }else{
        	return null;
        }
	}

	@SideOnly(Side.CLIENT)
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
		neighbor1 = null;
		neighbor2 = null;
		
		ISEGridNode[] neighbors = gridNode.getNeighborList();
		if (neighbors.length == 1) {
			neighbor1 = neighbors[0].getPos();
		}else if (neighbors.length > 1) {
			neighbor1 = neighbors[0].getPos();
			neighbor2 = neighbors[1].getPos();
		}

		this.markTileEntityForS2CSync();
		
		notifyNeighbor(neighbor1);
		notifyNeighbor(neighbor2);
	}

	public boolean canConnect() {
		return neighbor1==null || neighbor2==null;
	}
	
	private void notifyNeighbor(BlockPos neighbor) {
		if (neighbor == null)
			return;
		IBlockState state = world.getBlockState(neighbor);
		world.notifyBlockUpdate(neighbor1, state, state, 2);
	}	
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
	///Sync
	/////////////////////////////////////////////////////////
	@Override
	public void prepareS2CPacketData(NBTTagCompound nbt) {
		Utils.saveToNbt(nbt, "neighbor1", neighbor1);
		Utils.saveToNbt(nbt, "neighbor2", neighbor2);
	}
	
	@Override
	@SideOnly(value = Side.CLIENT)
	public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
		neighbor1 = Utils.posFromNbt(nbt, "neighbor1");
		neighbor2 = Utils.posFromNbt(nbt, "neighbor2");

		this.updateRenderInfo();
		
		updateRenderInfo(neighbor1);
		updateRenderInfo(neighbor2);
		
		super.onSyncDataFromServerArrived(nbt);
	}
	
	@SideOnly(value = Side.CLIENT)
	private void updateRenderInfo(BlockPos neighborPos) {
		if (neighborPos == null)
			return;
		
		TileEntity neighbor = world.getTileEntity(neighborPos);
		if (neighbor instanceof ISETransmissionTower)
			((ISETransmissionTower)neighbor).updateRenderInfo();
	}
}
