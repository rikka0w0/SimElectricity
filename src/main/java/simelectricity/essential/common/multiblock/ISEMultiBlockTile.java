package simelectricity.essential.common.multiblock;

public interface ISEMultiBlockTile {
	void onStructureCreated(MultiBlockTileInfo mbInfo);
	
	MultiBlockTileInfo getMultiBlockTileInfo();
}
