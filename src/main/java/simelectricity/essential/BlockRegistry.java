package simelectricity.essential;

import cpw.mods.fml.common.registry.GameRegistry;
import simelectricity.essential.blocks.BlockElectronics;
import simelectricity.essential.blocks.TileVoltageMeter;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.TileCable;
import simelectricity.essential.grid.BlockCableJoint;
import simelectricity.essential.grid.BlockTransmissionTowerCollisionBox;
import simelectricity.essential.grid.BlockTransmissionTowerTop;
import simelectricity.essential.grid.BlockTransmissionTowerBottom;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TileTransmissionTower;

public class BlockRegistry {
	public static BlockCable blockCable;
	
	public static BlockTransmissionTowerTop transmissionTowerTop;
	public static BlockTransmissionTowerBottom transmissionTowerBottom;
	public static BlockTransmissionTowerCollisionBox transmissionTowerCollisionBox;
	public static BlockCableJoint cableJoint;
	
	public static BlockElectronics blockElectronics;
	
	public static void registerBlocks(){
		blockCable = new BlockCable();
		
		transmissionTowerTop = new BlockTransmissionTowerTop();
		transmissionTowerBottom = new BlockTransmissionTowerBottom();
		transmissionTowerCollisionBox = new BlockTransmissionTowerCollisionBox();
		cableJoint = new BlockCableJoint();
		
		blockElectronics = new BlockElectronics();
	}
	
	public static void registerTileEntities(){
		GameRegistry.registerTileEntity(TileCable.class, "SEECable");
		GameRegistry.registerTileEntity(TileTransmissionTower.class, "SEETransmissionTower");
		GameRegistry.registerTileEntity(TileCableJoint.class, "SEECableJoint");
		
		GameRegistry.registerTileEntity(TileVoltageMeter.class, "SEEVoltageMeter");
	}	
}
