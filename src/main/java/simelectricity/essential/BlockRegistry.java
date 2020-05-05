package simelectricity.essential;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;
import rikka.librikka.IMetaProvider;
import rikka.librikka.ITileMeta;
import rikka.librikka.block.BlockBase;
import rikka.librikka.container.ContainerHelper;
import rikka.librikka.tileentity.TileEntityHelper;
import simelectricity.essential.cable.*;
import simelectricity.essential.coverpanel.ContainerVoltageSensor;
import simelectricity.essential.grid.*;
//import simelectricity.essential.grid.transformer.BlockDistributionTransformer;
//import simelectricity.essential.grid.transformer.BlockPowerTransformer;
//import simelectricity.essential.grid.transformer.TileDistributionTransformer;
//import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;
//import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder.Render;
//import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Primary;
//import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Secondary;
import simelectricity.essential.machines.*;
import simelectricity.essential.machines.gui.*;

public class BlockRegistry {    
	public final static List<Class<? extends Container>> registeredGuiContainers = new LinkedList<>();
	
	
	public static BlockCable[] blockCable;
	public static BlockWire[] blockWire;

	public static BlockPoleMetal35kV[] metalPole35kV;
	public static BlockPoleConcrete35kV[] concretePole35kV;
    public static BlockCableJoint[] cableJoint;
    public static BlockPoleConcrete[] concretePole;
//    public static BlockPowerTransformer powerTransformer;
//    public static BlockDistributionTransformer distributionTransformer;
//
	public static BlockElectronics[] blockElectronics;
    public static BlockTwoPortElectronics[] blockTwoPortElectronics;

    public static void initBlocks() {
        BlockRegistry.blockCable = BlockCable.create();
        BlockRegistry.blockWire = BlockWire.create();

        BlockRegistry.cableJoint = BlockCableJoint.create();
        BlockRegistry.metalPole35kV = BlockPoleMetal35kV.create();
        BlockRegistry.concretePole35kV = BlockPoleConcrete35kV.create();
        BlockRegistry.concretePole = BlockPoleConcrete.create();
//        BlockRegistry.powerTransformer = new BlockPowerTransformer();
//        BlockRegistry.distributionTransformer = new BlockDistributionTransformer();

          BlockRegistry.blockElectronics = BlockElectronics.create();
          BlockRegistry.blockTwoPortElectronics = BlockTwoPortElectronics.create();
    }
    
    public static void registerBlocks(final IForgeRegistry<Block> registry, boolean isItemBlock) {
    	registerBlocks(registry, isItemBlock, blockCable);
    	registerBlocks(registry, isItemBlock, blockWire);
    	
    	registerBlocks(registry, isItemBlock, metalPole35kV);
    	registerBlocks(registry, isItemBlock, concretePole35kV);
    	registerBlocks(registry, isItemBlock, concretePole);
    	registerBlocks(registry, isItemBlock, cableJoint);
//    			powerPole3,
//    			powerTransformer,
//    			distributionTransformer,
    	
    	registerBlocks(registry, isItemBlock, blockElectronics);
    	registerBlocks(registry, isItemBlock, blockTwoPortElectronics);
    	
    	ttb=new TESRTestBlock();
    	registerBlocks(registry, isItemBlock, ttb);
    }
    public static TESRTestBlock ttb;
    public static TileEntityType<TESRTestBlock.Tile> ttb_tetype;
    public static void registerTileEntities(final IForgeRegistry<TileEntityType<?>> registry) {
    	ttb_tetype=TileEntityHelper.registerTileEntity(registry, TESRTestBlock.Tile.class, ttb);
    	
    	TileEntityHelper.registerTileEntity(registry, TileCable.class, blockCable);
    	TileEntityHelper.registerTileEntity(registry, TileWire.class, blockWire);
    	
    	TileEntityHelper.registerTileEntity(registry, TileMultiBlockPlaceHolder.class, 
    			makeBlockArray(concretePole35kV, metalPole35kV));
    	TileEntityHelper.registerTileEntity(registry, TilePoleMetal35kV.class, metalPole35kV);
    	TileEntityHelper.registerTileEntity(registry, TilePoleMetal35kV.Bottom.class, metalPole35kV);
    	TileEntityHelper.registerTileEntity(registry, TilePoleConcrete35kV.class, concretePole35kV);
    	
    	TileEntityHelper.registerTileEntity(registry, TilePoleConcrete.Pole10Kv.Type0.class, concretePole);
    	TileEntityHelper.registerTileEntity(registry, TilePoleConcrete.Pole10Kv.Type1.class, concretePole);
    	TileEntityHelper.registerTileEntity(registry, TilePoleConcrete.Pole415vType0.class, concretePole);
    	TileEntityHelper.registerTileEntity(registry, TilePoleBranch.Type10kV.class, concretePole);
    	TileEntityHelper.registerTileEntity(registry, TilePoleBranch.Type415V.class, concretePole);

    	
    	TileEntityHelper.registerTileEntity(registry, TileCableJoint.Type10kV.class, cableJoint[BlockCableJoint.Type._10kv.ordinal()]);
    	TileEntityHelper.registerTileEntity(registry, TileCableJoint.Type415V.class, cableJoint[BlockCableJoint.Type._415v.ordinal()]);


//        registerTile(TilePowerTransformerPlaceHolder.class);
//        registerTile(TilePowerTransformerPlaceHolder.Primary.class);
//        registerTile(TilePowerTransformerPlaceHolder.Secondary.class);
//        registerTile(Render.class);
//        registerTile(Primary.class);
//        registerTile(Secondary.class);
//        
//        registerTile(Pole10Kv.Type0.class);
//        registerTile(Pole10Kv.Type1.class);
//        registerTile(Pole415vType0.class);
//        registerTile(TilePoleBranch.Type10kV.class);
//        registerTile(TilePoleBranch.Type415V.class);
//        
//        registerTile(TileDistributionTransformer.Pole10kV.class);
//        registerTile(TileDistributionTransformer.Pole415V.class);
//        registerTile(TileDistributionTransformer.PlaceHolder.class);
//
    	
    	RegisterTEs(registry, blockElectronics);
    	RegisterTEs(registry, blockTwoPortElectronics);
    }
    
    public static void registerContainers(final IForgeRegistry<ContainerType<?>> registry) {
    	registerGuiContainer(registry, ContainerVoltageMeter.class);
    	registerGuiContainer(registry, ContainerQuantumGenerator.class);
    	registerGuiContainer(registry, ContainerAdjustableResistor.class);
    	registerGuiContainer(registry, ContainerElectricFurnace.class);
    	registerGuiContainer(registry, ContainerSE2RF.class);
    	registerGuiContainer(registry, ContainerRF2SE.class);
    	
    	registerGuiContainer(registry, ContainerAdjustableTransformer.class);
    	registerGuiContainer(registry, ContainerCurrentSensor.class);
    	registerGuiContainer(registry, ContainerDiode.class);
    	registerGuiContainer(registry, ContainerSwitch.class);
    	registerGuiContainer(registry, ContainerRelay.class);
    	registerGuiContainer(registry, ContainerPowerMeter.class);
    	
    	registerGuiContainer(registry, ContainerVoltageSensor.class);
    }
    
    
    private static void registerBlocks(IForgeRegistry registry, boolean isItemBlock, BlockBase... blocks) {
    	if (isItemBlock) {
        	for (BlockBase block: blocks)
        		registry.register(block.itemBlock);
    	} else {
    		registry.registerAll(blocks);
    	}
    }
    
    private static void RegisterTEs(IForgeRegistry<TileEntityType<?>> registry, IMetaProvider[] blocks) {
    	for (IMetaProvider<ITileMeta> meta: blocks) {
    		TileEntityHelper.registerTileEntity(registry, meta.meta().teCls(), (Block)meta);
    	}
	}
    
    private static void registerGuiContainer(final IForgeRegistry<ContainerType<?>> registry, Class<? extends Container> containerCls) {
    	ContainerHelper.register(registry, containerCls);
    	registeredGuiContainers.add(containerCls);
    }
    
    private static Block[] makeBlockArray(Block[]... blockArrays) {
    	int size = 0;
    	for (int i=0; i<blockArrays.length; i++)
    		size += blockArrays[i].length;

    	int k=0;
    	Block[] blocks = new Block[size];
    	for (int i=0; i<blockArrays.length; i++) {
    		for (int j=0; j<blockArrays[i].length; j++) {
    			blocks[k] = blockArrays[i][j];
    			k++;
    		}
    	}
    	
    	return blocks;
    }
}
