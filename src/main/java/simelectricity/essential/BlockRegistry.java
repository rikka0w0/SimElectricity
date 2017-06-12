package simelectricity.essential;

import cpw.mods.fml.common.registry.GameRegistry;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.TileCable;

public class BlockRegistry {
	public static BlockCable blockCable;
	
	public static void registerBlocks(){
		blockCable = new BlockCable();
	}
	
	public static void registerTileEntities(){
		GameRegistry.registerTileEntity(TileCable.class, "TileCable");
	}	
}
