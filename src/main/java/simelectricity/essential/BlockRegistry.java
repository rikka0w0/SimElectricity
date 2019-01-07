package simelectricity.essential;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import rikka.librikka.block.BlockBase;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.TileCable;
import simelectricity.essential.grid.*;
import simelectricity.essential.grid.TilePowerPole3.Pole10Kv;
import simelectricity.essential.grid.TilePowerPole3.Pole415vType0;
import simelectricity.essential.grid.transformer.BlockDistributionTransformer;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;
import simelectricity.essential.grid.transformer.TileDistributionTransformer;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder.Render;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Primary;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Secondary;
import simelectricity.essential.machines.BlockElectronics;
import simelectricity.essential.machines.BlockTwoPortElectronics;
import simelectricity.essential.machines.tile.*;

//@GameRegistry.ObjectHolder(SETemplate.MODID)
public class BlockRegistry {
	public static BlockCable blockCable;

    public static BlockPowerPoleTop powerPoleTop;
    public static BlockPowerPoleBottom powerPoleBottom;
    public static BlockPowerPoleCollisionBox powerPoleCollisionBox;
    public static BlockCableJoint cableJoint;
    public static BlockPowerPole2 powerPole2;
    public static BlockPowerPole3 powerPole3;
    public static BlockPowerTransformer powerTransformer;
    public static BlockDistributionTransformer distributionTransformer;

    public static BlockElectronics blockElectronics;
    public static BlockTwoPortElectronics blockTwoPortElectronics;
    
    public static void initBlocks() {
        BlockRegistry.blockCable = new BlockCable();

        BlockRegistry.powerPoleTop = new BlockPowerPoleTop();
        BlockRegistry.powerPoleBottom = new BlockPowerPoleBottom();
        BlockRegistry.powerPoleCollisionBox = new BlockPowerPoleCollisionBox();
        BlockRegistry.cableJoint = new BlockCableJoint();
        BlockRegistry.powerPole2 = new BlockPowerPole2();
        BlockRegistry.powerPole3 = new BlockPowerPole3();
        BlockRegistry.powerTransformer = new BlockPowerTransformer();
        BlockRegistry.distributionTransformer = new BlockDistributionTransformer();

        BlockRegistry.blockElectronics = new BlockElectronics();
        BlockRegistry.blockTwoPortElectronics = new BlockTwoPortElectronics();
    }
    
    public static void registerBlocks(IForgeRegistry registry, boolean isItemBlock) {
    	registerBlocks(registry, isItemBlock,
    			blockCable,
    			
    			powerPoleTop,
    			powerPoleBottom,
    			powerPoleCollisionBox,
    			cableJoint,
    			powerPole2,
    			powerPole3,
    			powerTransformer,
    			distributionTransformer,
    			
    			blockElectronics,
    			blockTwoPortElectronics
    			);
    }
    
    public static void registerTileEntities() {
    	registerTile(TileCable.class);
    	registerTile(BlockPowerPoleBottom.Tile.class);
        registerTile(TilePowerPole.class);
        registerTile(TileCableJoint.Type10kV.class);
        registerTile(TileCableJoint.Type415V.class);
        registerTile(TilePowerPole2.class);
        
        registerTile(TilePowerTransformerPlaceHolder.class);
        registerTile(TilePowerTransformerPlaceHolder.Primary.class);
        registerTile(TilePowerTransformerPlaceHolder.Secondary.class);
        registerTile(Render.class);
        registerTile(Primary.class);
        registerTile(Secondary.class);
        
        registerTile(Pole10Kv.Type0.class);
        registerTile(Pole10Kv.Type1.class);
        registerTile(Pole415vType0.class);
        registerTile(TilePoleBranch.Type10kV.class);
        registerTile(TilePoleBranch.Type415V.class);
        
        registerTile(TileDistributionTransformer.Pole10kV.class);
        registerTile(TileDistributionTransformer.Pole415V.class);
        registerTile(TileDistributionTransformer.PlaceHolder.class);

        registerTile(TileVoltageMeter.class);
        registerTile(TileQuantumGenerator.class);
        registerTile(TileAdjustableResistor.class);
        registerTile(TileIncandescentLamp.class);
        registerTile(TileElectricFurnace.class);
        registerTile(TileSE2RF.class);
        registerTile(TileRF2SE.class);

        registerTile(TileAdjustableTransformer.class);
        registerTile(TileCurrentSensor.class);
        registerTile(TileDiode.class);
        registerTile(TileSwitch.class);
        registerTile(TileRelay.class);
        registerTile(TilePowerMeter.class);
    }
    
    private static void registerBlocks(IForgeRegistry registry, boolean isItemBlock, BlockBase... blocks) {
    	if (isItemBlock) {
        	for (BlockBase block: blocks)
        		registry.register(block.itemBlock);
    	} else {
    		registry.registerAll(blocks);
    	}
    }
    
    private static void registerTile(Class<? extends TileEntity> teClass) {
    	String registryName = teClass.getName();
    	registryName = registryName.substring(registryName.lastIndexOf(".") + 1);
    	registryName = Essential.MODID + ":" + registryName;
    	GameRegistry.registerTileEntity(teClass, registryName);
    }
}
