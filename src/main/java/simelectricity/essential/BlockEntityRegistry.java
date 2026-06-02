package simelectricity.essential;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import rikka.librikka.blockentity.BlockEntityHelper;
import simelectricity.essential.cable.*;
import simelectricity.essential.grid.*;
import simelectricity.essential.grid.transformer.*;
import simelectricity.essential.machines.blockentity.*;
import simelectricity.essential.machines.BlockElectronics;
import simelectricity.essential.machines.BlockTwoPortElectronics;

public class BlockEntityRegistry {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Essential.MODID);

	private static <T extends BlockEntity> void register(
			Class<T> beClass, 
			BlockEntityHelper.BEConstructor<T> beConstructor, 
			Block... validBlocks) {
		String name = BlockEntityHelper.getRegistryName(beClass);
		BLOCK_ENTITIES.register(name, () -> BlockEntityHelper.of(beClass, beConstructor, validBlocks));
	}

	public static void init(IEventBus modEventBus) {
		electronics();
		electronics_2port();

		register(BlockEntityCable.class, BlockEntityCable::new, BlockRegistry.blockCable);
		register(BlockEntityWire.class, BlockEntityWire::new, BlockRegistry.blockWire);

		register(BlockEntityMultiBlockPlaceHolder.class, BlockEntityMultiBlockPlaceHolder::new,
				makeBlockArray(BlockRegistry.concretePole35kV, BlockRegistry.metalPole35kV,
						BlockRegistry.distributionTransformer));
		register(BlockEntityPoleMetal35kV.class, BlockEntityPoleMetal35kV::new,
				BlockRegistry.metalPole35kV);
		register(BlockEntityPoleMetal35kV.Bottom.class, BlockEntityPoleMetal35kV.Bottom::new,
				BlockRegistry.metalPole35kV);
		register(BlockEntityPoleConcrete35kV.class, BlockEntityPoleConcrete35kV::new,
				BlockRegistry.concretePole35kV);

		register(BlockEntityPoleConcrete.Pole10Kv.Type0.class,
				BlockEntityPoleConcrete.Pole10Kv.Type0::new, BlockRegistry.concretePole);
		register(BlockEntityPoleConcrete.Pole10Kv.Type1.class,
				BlockEntityPoleConcrete.Pole10Kv.Type1::new, BlockRegistry.concretePole);
		register(BlockEntityPoleConcrete.Pole415vType0.class,
				BlockEntityPoleConcrete.Pole415vType0::new, BlockRegistry.concretePole);
		register(BlockEntityPoleBranch.Type10kV.class, BlockEntityPoleBranch.Type10kV::new,
				BlockRegistry.concretePole);
		register(BlockEntityPoleBranch.Type415V.class, BlockEntityPoleBranch.Type415V::new,
				BlockRegistry.concretePole);

		register(BlockEntityCableJoint.Type10kV.class, BlockEntityCableJoint.Type10kV::new,
				BlockRegistry.cableJoint[BlockCableJoint.Type._10kv.ordinal()]);
		register(BlockEntityCableJoint.Type415V.class, BlockEntityCableJoint.Type415V::new,
				BlockRegistry.cableJoint[BlockCableJoint.Type._415v.ordinal()]);

		register(BlockEntityPowerTransformerPlaceHolder.class,
				BlockEntityPowerTransformerPlaceHolder::new, BlockRegistry.powerTransformer);
		register(BlockEntityPowerTransformerPlaceHolder.Primary.class,
				BlockEntityPowerTransformerPlaceHolder.Primary::new, BlockRegistry.powerTransformer);
		register(BlockEntityPowerTransformerPlaceHolder.Secondary.class,
				BlockEntityPowerTransformerPlaceHolder.Secondary::new, BlockRegistry.powerTransformer);
		register(BlockEntityPowerTransformerPlaceHolder.Render.class,
				BlockEntityPowerTransformerPlaceHolder.Render::new, BlockRegistry.powerTransformer);
		register(BlockEntityPowerTransformerWinding.Primary.class,
				BlockEntityPowerTransformerWinding.Primary::new, BlockRegistry.powerTransformer);
		register(BlockEntityPowerTransformerWinding.Secondary.class,
				BlockEntityPowerTransformerWinding.Secondary::new, BlockRegistry.powerTransformer);

		register(BlockEntityDistributionTransformer.Pole10kV.class,
				BlockEntityDistributionTransformer.Pole10kV::new, BlockRegistry.distributionTransformer);
		register(BlockEntityDistributionTransformer.Pole415V.class,
				BlockEntityDistributionTransformer.Pole415V::new, BlockRegistry.distributionTransformer);

		BLOCK_ENTITIES.register(modEventBus);
	}

	private static void electronics() {
		register(BlockEntityVoltageMeter.class, BlockEntityVoltageMeter::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.voltage_meter.ordinal()]);
		register(BlockEntityQuantumGenerator.class, BlockEntityQuantumGenerator::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.quantum_generator.ordinal()]);
		register(BlockEntityAdjustableResistor.class, BlockEntityAdjustableResistor::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.adjustable_resistor.ordinal()]);
		register(BlockEntityIncandescentLamp.class, BlockEntityIncandescentLamp::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.incandescent_lamp.ordinal()]);
		register(BlockEntityElectricFurnace.class, BlockEntityElectricFurnace::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.electric_furnace.ordinal()]);
		register(BlockEntitySE2RF.class, BlockEntitySE2RF::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.transformer_se2rf.ordinal()]);
		register(BlockEntityRF2SE.class, BlockEntityRF2SE::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.transformer_rf2se.ordinal()]);
	}

	private static void electronics_2port() {
		register(BlockEntityAdjustableTransformer.class, BlockEntityAdjustableTransformer::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.adjustable_transformer
						.ordinal()]);
		register(BlockEntityCurrentSensor.class, BlockEntityCurrentSensor::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.current_sensor.ordinal()]);
		register(BlockEntityDiode.class, BlockEntityDiode::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.diode.ordinal()]);
		register(BlockEntitySwitch.class, BlockEntitySwitch::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.circuit_breaker
						.ordinal()]);
		register(BlockEntityRelay.class, BlockEntityRelay::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.relay.ordinal()]);
		register(BlockEntityPowerMeter.class, BlockEntityPowerMeter::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.power_meter.ordinal()]);
	}

	private static Block[] makeBlockArray(Block[]... blockArrays) {
		int size = 0;
		for (int i = 0; i < blockArrays.length; i++)
			size += blockArrays[i].length;

		int k = 0;
		Block[] blocks = new Block[size];
		for (int i = 0; i < blockArrays.length; i++) {
			for (int j = 0; j < blockArrays[i].length; j++) {
				blocks[k] = blockArrays[i][j];
				k++;
			}
		}

		return blocks;
	}
}
