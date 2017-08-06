package simelectricity.essential.common.multiblock;

public interface ISEMultiBlockTile {
	/**
	 * Called when a structure is created by a player
	 * @param mbInfo
	 */
	void onStructureCreated(MultiBlockTileInfo mbInfo);
	
	MultiBlockTileInfo getMultiBlockTileInfo();
	
	void onStructureRemoved();
}
