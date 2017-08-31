package rikka.librikka.multiblock;

public interface IMultiBlockTile {
    MultiBlockTileInfo getMultiBlockTileInfo();

    /**
     * Called when a structure is being created (by a player), all tileEntity will receive this
     *
     * @param mbInfo
     */
    void onStructureCreating(MultiBlockTileInfo mbInfo);

    /**
     * Called when a structure is created by a player, all tileEntity will receive this
     *
     */
    void onStructureCreated();

    /**
     * Fired when the structure is destroyed (player/ explosion...)
     */
    void onStructureRemoved();
}
