package simelectricity.essential;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.TileCable;
import simelectricity.essential.grid.BlockCableJoint;
import simelectricity.essential.grid.BlockPowerPole2;
import simelectricity.essential.grid.BlockPowerPoleBottom;
import simelectricity.essential.grid.BlockPowerPoleCollisionBox;
import simelectricity.essential.grid.BlockPowerPoleTop;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TilePowerPole;
import simelectricity.essential.grid.TilePowerPole2;
import simelectricity.essential.machines.BlockElectronics;
import simelectricity.essential.machines.BlockTwoPortElectronics;
import simelectricity.essential.machines.gui.ContainerAdjustableResistor;
import simelectricity.essential.machines.gui.ContainerAdjustableTransformer;
import simelectricity.essential.machines.gui.ContainerCurrentSensor;
import simelectricity.essential.machines.gui.ContainerDiode;
import simelectricity.essential.machines.gui.ContainerQuantumGenerator;
import simelectricity.essential.machines.gui.ContainerSwitch;
import simelectricity.essential.machines.gui.ContainerVoltageMeter;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.machines.tile.TileAdjustableTransformer;
import simelectricity.essential.machines.tile.TileCurrentSensor;
import simelectricity.essential.machines.tile.TileDiode;
import simelectricity.essential.machines.tile.TileIncandescentLamp;
import simelectricity.essential.machines.tile.TileQuantumGenerator;
import simelectricity.essential.machines.tile.TileSolarPanel;
import simelectricity.essential.machines.tile.TileSwitch;
import simelectricity.essential.machines.tile.TileVoltageMeter;

//@GameRegistry.ObjectHolder(SETemplate.MODID)
public class BlockRegistry {
	public static BlockCable blockCable;
	
	public static BlockPowerPoleTop powerPoleTop;
	public static BlockPowerPoleBottom powerPoleBottom;
	public static BlockPowerPoleCollisionBox powerPoleCollisionBox;
	public static BlockCableJoint cableJoint;
	public static BlockPowerPole2 powerPole2;
	
	public static BlockElectronics blockElectronics;
	public static BlockTwoPortElectronics blockTwoPortElectronics;
	
	public static void registerBlocks(){
		blockCable = new BlockCable();
		
		powerPoleTop = new BlockPowerPoleTop();
		powerPoleBottom = new BlockPowerPoleBottom();
		powerPoleCollisionBox = new BlockPowerPoleCollisionBox();
		cableJoint = new BlockCableJoint();
		powerPole2 = new BlockPowerPole2();
		
		blockElectronics = new BlockElectronics();
		blockTwoPortElectronics = new BlockTwoPortElectronics();
	}
	
	public static void registerTileEntities(){
		GameRegistry.registerTileEntity(TileCable.class, "SEECable");
		GameRegistry.registerTileEntity(TilePowerPole.class, "SEEPowerPole");
		GameRegistry.registerTileEntity(TileCableJoint.class, "SEECableJoint");
		GameRegistry.registerTileEntity(TilePowerPole2.class, "SEEPowerPole2");
		
		GameRegistry.registerTileEntity(TileVoltageMeter.class, "SEEVoltageMeter");
		GameRegistry.registerTileEntity(TileQuantumGenerator.class, "SEEQuantumGenerator");
		GameRegistry.registerTileEntity(TileAdjustableResistor.class, "SEEAdjustableResistor");
		GameRegistry.registerTileEntity(TileIncandescentLamp.class, "SEEIncandescentLamp");
		GameRegistry.registerTileEntity(TileSolarPanel.class, "SEESolarPanel");
		
		GameRegistry.registerTileEntity(TileAdjustableTransformer.class, "SEEAdjustableTransformer");
		GameRegistry.registerTileEntity(TileCurrentSensor.class, "SEECurrentSensor");
		GameRegistry.registerTileEntity(TileDiode.class, "SEEDiode");
		GameRegistry.registerTileEntity(TileSwitch.class, "SEESwitch");
	}
	
	public static Container getContainer(TileEntity te, EntityPlayer player){
		if (te instanceof TileVoltageMeter)
			return new ContainerVoltageMeter(te);
		if (te instanceof TileQuantumGenerator)
			return new ContainerQuantumGenerator(te);
		if (te instanceof TileAdjustableResistor)
			return new ContainerAdjustableResistor(te);
		
		if (te instanceof TileAdjustableTransformer)
			return new ContainerAdjustableTransformer(te);
		if (te instanceof TileCurrentSensor)
			return new ContainerCurrentSensor(te);
		if (te instanceof TileDiode)
			return new ContainerDiode(te);
		if (te instanceof TileSwitch)
			return new ContainerSwitch(te);
		
		return null;
	}
}
