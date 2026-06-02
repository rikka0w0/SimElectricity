package simelectricity.essential;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
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
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Essential.MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Essential.MODID);
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, Essential.MODID);

	public final static List<Class<? extends AbstractContainerMenu>> registeredGuiContainers = new ArrayList<>();
	private final static List<Item> blockItems = new ArrayList<>();

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

	public static void init(IEventBus modEventBus) {
		blockCable = BlockCable.create();
		blockWire = BlockWire.create();
		cableJoint = BlockCableJoint.create();
		metalPole35kV = BlockPoleMetal35kV.create();
		concretePole35kV = BlockPoleConcrete35kV.create();
		concretePole = BlockPoleConcrete.create();
		powerTransformer = BlockPowerTransformer.create();
		BlockPowerTransformer.createBluePrint();
		distributionTransformer = BlockDistributionTransformer.create();
		BlockDistributionTransformer.createBluePrint();
		blockElectronics = BlockElectronics.create();
		blockTwoPortElectronics = BlockTwoPortElectronics.create();

		// Register blocks and items
		registerBlocks(blockCable);
		registerBlocks(blockWire);
		registerBlocks(metalPole35kV);
		registerBlocks(concretePole35kV);
		registerBlocks(concretePole);
		registerBlocks(cableJoint);
		registerBlocks(powerTransformer);
		registerBlocks(distributionTransformer);
		registerBlocks(blockElectronics);
		registerBlocks(blockTwoPortElectronics);

		// Cover Panel colored facade hosts
		SEEAPI.coverPanelRegistry.registerColoredFacadeHost(blockCable);
		SEEAPI.coverPanelRegistry.registerColoredFacadeHost(blockElectronics);
		SEEAPI.coverPanelRegistry.registerColoredFacadeHost(blockTwoPortElectronics);

		// Register containers
		registerGuiContainer(ContainerVoltageMeter.class);
		registerGuiContainer(ContainerQuantumGenerator.class);
		registerGuiContainer(ContainerAdjustableResistor.class);
		registerGuiContainer(ContainerElectricFurnace.class);
		registerGuiContainer(ContainerSE2RF.class);
		registerGuiContainer(ContainerRF2SE.class);
		registerGuiContainer(ContainerAdjustableTransformer.class);
		registerGuiContainer(ContainerCurrentSensor.class);
		registerGuiContainer(ContainerDiode.class);
		registerGuiContainer(ContainerSwitch.class);
		registerGuiContainer(ContainerRelay.class);
		registerGuiContainer(ContainerPowerMeter.class);
		registerGuiContainer(ContainerVoltageSensor.class);

		BLOCKS.register(modEventBus);
		ITEMS.register(modEventBus);
		CONTAINERS.register(modEventBus);
	}

	private static void registerBlocks(BlockBase[] blocks) {
		for (BlockBase block : blocks) {
			BLOCKS.register(block.registryName, () -> block);
			Item item = block.asItem();
			if (item != null) {
				ITEMS.register(block.registryName, () -> item);
				blockItems.add(item);
			}
		}
	}

	private static <T extends AbstractContainerMenu> void registerGuiContainer(Class<T> containerCls) {
		String name = rikka.librikka.blockentity.BlockEntityHelper.getRegistryName(containerCls);
		CONTAINERS.register(name, () -> ContainerHelper.of(containerCls));
		registeredGuiContainers.add(containerCls);
	}

	public static void addItemsToCreativeTab(CreativeModeTab.Output output) {
		for (Item item : blockItems) {
			output.accept(item);
		}
	}
}
