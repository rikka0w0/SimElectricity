package simelectricity.essential.common.multiblock;

public interface ISEMultiBlockTile {
	/**
	 * Called when a structure is created by a player
	 * @param mbInfo
	 */
	void onStructureCreating(MultiBlockTileInfo mbInfo);
	
	MultiBlockTileInfo getMultiBlockTileInfo();
	
	/**
	 * Only the tileEntity which is directly removed by the player can receive this event
	 */
	void onStructureRemoved();
}
