package simelectricity.essential;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import rikka.librikka.block.BlockBase;
import rikka.librikka.container.ContainerHelper;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.cable.*;
import simelectricity.essential.coverpanel.ContainerVoltageSensor;
import simelectricity.essential.grid.*;
import simelectricity.essential.grid.transformer.BlockDistributionTransformer;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;
import simelectricity.essential.machines.*;
import simelectricity.essential.machines.gui.*;

public class BlockRegistry {
	public final static List<Class<? extends AbstractContainerMenu>> registeredGuiContainers = new LinkedList<>();
	private final static Set<Item> blockItems = new LinkedHashSet<>();

	public static BlockCable[] blockCable;
	public static BlockWire[] blockWire;

	public static BlockPoleMetal35kV[] metalPole35kV;
	public static BlockPoleConcrete35kV[] concretePole35kV;
    public static BlockCableJoint[] cableJoint;
    public static BlockPoleConcrete[] concretePole;
    public static BlockPowerTransformer[] powerTransformer;
    public static BlockDistributionTransformer[] distributionTransformer;

	public static BlockElectronics[] blockElectronics;
    public static BlockTwoPortElectronics[] blockTwoPortElectronics;

    public static void initBlocks() {
        BlockRegistry.blockCable = BlockCable.create();
        BlockRegistry.blockWire = BlockWire.create();

        BlockRegistry.cableJoint = BlockCableJoint.create();
        BlockRegistry.metalPole35kV = BlockPoleMetal35kV.create();
        BlockRegistry.concretePole35kV = BlockPoleConcrete35kV.create();
        BlockRegistry.concretePole = BlockPoleConcrete.create();

		BlockRegistry.powerTransformer = BlockPowerTransformer.create();
		BlockPowerTransformer.createBluePrint();
		BlockRegistry.distributionTransformer = BlockDistributionTransformer.create();
		BlockDistributionTransformer.createBluePrint();

		BlockRegistry.blockElectronics = BlockElectronics.create();
		BlockRegistry.blockTwoPortElectronics = BlockTwoPortElectronics.create();
    }

    public static void registerBlocks(final IForgeRegistry<Block> registry) {
    	registerBlocks(registry, blockCable);
    	registerBlocks(registry, blockWire);
    	SEEAPI.coverPanelRegistry.registerColoredFacadeHost(blockCable);

    	registerBlocks(registry, metalPole35kV);
    	registerBlocks(registry, concretePole35kV);
    	registerBlocks(registry, concretePole);
    	registerBlocks(registry, cableJoint);
    	registerBlocks(registry, powerTransformer);
    	registerBlocks(registry, distributionTransformer);

    	registerBlocks(registry, blockElectronics);
    	SEEAPI.coverPanelRegistry.registerColoredFacadeHost(blockElectronics);
    	registerBlocks(registry, blockTwoPortElectronics);
    	SEEAPI.coverPanelRegistry.registerColoredFacadeHost(blockTwoPortElectronics);
    }

    public static void registerBlockItems(final IForgeRegistry<Item> registry) {
    	blockItems.forEach(registry::register);
    }

    public static void registerContainers(final IForgeRegistry<MenuType<?>> registry) {
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


    private static void registerBlocks(IForgeRegistry<Block> registry, BlockBase... blocks) {
    	registry.registerAll(blocks);

    	for (BlockBase block: blocks)
    		blockItems.add(block.asItem());
    }

    private static void registerGuiContainer(final IForgeRegistry<MenuType<?>> registry, Class<? extends AbstractContainerMenu> containerCls) {
    	ContainerHelper.register(registry, containerCls);
    	registeredGuiContainers.add(containerCls);
    }
}
