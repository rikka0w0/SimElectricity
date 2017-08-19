package simelectricity.essential.grid.transformer;

import net.minecraft.nbt.NBTTagCompound;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.common.multiblock.ISEMultiBlockTile;
import simelectricity.essential.common.multiblock.MultiBlockTileInfo;

public class TilePowerTransformerSecondary extends SEEnergyTile implements ISEMultiBlockTile, ISEGridTile{
	private MultiBlockTileInfo mbInfo;
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		mbInfo = new MultiBlockTileInfo(nbt);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		mbInfo.saveToNBT(nbt);
		return super.writeToNBT(nbt);
	}
	
	//////////////////////////////
	/////ISEMultiBlockTile
	//////////////////////////////
	@Override
	public MultiBlockTileInfo getMultiBlockTileInfo() {
		return this.mbInfo;
	}
	
	@Override
	public void onStructureCreating(MultiBlockTileInfo mbInfo) {
		this.mbInfo = mbInfo;
		this.markDirty();
		
		this.gridNode = SEAPI.energyNetAgent.newGridNode(pos, 3);
		SEAPI.energyNetAgent.attachGridNode(world, gridNode);
	}

	@Override
	public void onStructureCreated() {

	}

	@Override
	public void onStructureRemoved() {
		SEAPI.energyNetAgent.detachGridNode(world, gridNode);
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
		
	}
}
