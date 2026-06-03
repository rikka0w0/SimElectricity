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
import simelectricity.essential.grid.transformer.EnumPowerTransformerBlockType;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;
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
		// Allocate arrays
		blockCable = new BlockCable[BlockCable.CableTypes.values().length];
		blockWire = new BlockWire[BlockWire.Type.values().length];
		cableJoint = new BlockCableJoint[BlockCableJoint.Type.values().length];
		metalPole35kV = new BlockPoleMetal35kV[2];
		concretePole35kV = new BlockPoleConcrete35kV[2];
		concretePole = new BlockPoleConcrete[BlockPoleConcrete.Type.values().length];
		powerTransformer = new BlockPowerTransformer[EnumPowerTransformerBlockType.values().length];
		distributionTransformer = new BlockDistributionTransformer[EnumDistributionTransformerBlockType.values().length];
		blockElectronics = new BlockElectronics[BlockElectronics.Type.values().length];
		blockTwoPortElectronics = new BlockTwoPortElectronics[BlockTwoPortElectronics.Type.values().length];

		// 1. BlockCable
		for (BlockCable.CableTypes meta : BlockCable.CableTypes.values()) {
			final int index = meta.ordinal();
			final String name = "cable_" + meta.name();
			var blockHolder = BLOCKS.register(name, () -> BlockCable.createBlock(meta));
			ITEMS.register(name, () -> {
				BlockCable block = (BlockCable) blockHolder.get();
				blockCable[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}

		// 2. BlockWire
		for (BlockWire.Type meta : BlockWire.Type.values()) {
			final int index = meta.ordinal();
			final String name = "wire_" + meta.name();
			var blockHolder = BLOCKS.register(name, () -> BlockWire.createBlock(meta));
			ITEMS.register(name, () -> {
				BlockWire block = (BlockWire) blockHolder.get();
				blockWire[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}

		// 3. BlockCableJoint
		for (BlockCableJoint.Type meta : BlockCableJoint.Type.values()) {
			final int index = meta.ordinal();
			final String name = "cable_joint" + meta.name();
			var blockHolder = BLOCKS.register(name, () -> BlockCableJoint.createBlock(meta));
			ITEMS.register(name, () -> {
				BlockCableJoint block = (BlockCableJoint) blockHolder.get();
				cableJoint[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}

		// 4. BlockPoleMetal35kV
		for (int i = 0; i < 2; i++) {
			final int index = i;
			final String name = "pole_metal_35kv_" + index;
			var blockHolder = BLOCKS.register(name, () -> BlockPoleMetal35kV.createBlock(index));
			ITEMS.register(name, () -> {
				BlockPoleMetal35kV block = (BlockPoleMetal35kV) blockHolder.get();
				metalPole35kV[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}

		// 5. BlockPoleConcrete35kV
		for (int i = 0; i < 2; i++) {
			final int index = i;
			final String name = "pole_concrete_35kv_" + index;
			var blockHolder = BLOCKS.register(name, () -> BlockPoleConcrete35kV.createBlock(index));
			ITEMS.register(name, () -> {
				BlockPoleConcrete35kV block = (BlockPoleConcrete35kV) blockHolder.get();
				concretePole35kV[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}

		// 6. BlockPoleConcrete
		for (BlockPoleConcrete.Type meta : BlockPoleConcrete.Type.values()) {
			final int index = meta.ordinal();
			final String name = "pole_concrete_" + meta.name();
			var blockHolder = BLOCKS.register(name, () -> BlockPoleConcrete.createBlock(meta));
			ITEMS.register(name, () -> {
				BlockPoleConcrete block = (BlockPoleConcrete) blockHolder.get();
				concretePole[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}

		// 7. BlockPowerTransformer
		for (EnumPowerTransformerBlockType meta : EnumPowerTransformerBlockType.values()) {
			final int index = meta.ordinal();
			final String name = "transformer_35kv_10kv_" + meta.getSerializedName();
			var blockHolder = BLOCKS.register(name, () -> BlockPowerTransformer.createBlock(meta));
			ITEMS.register(name, () -> {
				BlockPowerTransformer block = (BlockPowerTransformer) blockHolder.get();
				powerTransformer[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}
		// 8. BlockDistributionTransformer
		for (EnumDistributionTransformerBlockType meta : EnumDistributionTransformerBlockType.values()) {
			final int index = meta.ordinal();
			final String name = "transformer_10kv_415v_" + meta.getSerializedName();
			var blockHolder = BLOCKS.register(name, () -> BlockDistributionTransformer.createBlock(meta));
			ITEMS.register(name, () -> {
				BlockDistributionTransformer block = (BlockDistributionTransformer) blockHolder.get();
				distributionTransformer[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}

		// 9. BlockElectronics
		for (BlockElectronics.Type meta : BlockElectronics.Type.values()) {
			final int index = meta.ordinal();
			final String name = "electronics_" + meta.name();
			var blockHolder = BLOCKS.register(name, () -> BlockElectronics.createBlock(meta));
			ITEMS.register(name, () -> {
				BlockElectronics block = (BlockElectronics) blockHolder.get();
				blockElectronics[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}

		// 10. BlockTwoPortElectronics
		for (BlockTwoPortElectronics.Type meta : BlockTwoPortElectronics.Type.values()) {
			final int index = meta.ordinal();
			final String name = "electronics2_" + meta.name();
			var blockHolder = BLOCKS.register(name, () -> BlockTwoPortElectronics.createBlock(meta));
			ITEMS.register(name, () -> {
				BlockTwoPortElectronics block = (BlockTwoPortElectronics) blockHolder.get();
				blockTwoPortElectronics[index] = block;
				Item item = block.asItem();
				if (item != null) {
					synchronized (blockItems) {
						if (!blockItems.contains(item)) blockItems.add(item);
					}
				}
				return item;
			});
		}

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
