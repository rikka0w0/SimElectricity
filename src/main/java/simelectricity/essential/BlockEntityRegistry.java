package simelectricity.essential;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.IForgeRegistry;
import rikka.librikka.blockentity.BlockEntityHelper;
import simelectricity.essential.cable.*;
import simelectricity.essential.grid.*;
import simelectricity.essential.grid.transformer.*;
import simelectricity.essential.machines.*;
import simelectricity.essential.machines.tile.*;

public class BlockEntityRegistry {
	private static void electronics(IForgeRegistry<BlockEntityType<?>> registry) {
		BlockEntityHelper.register(registry, TileVoltageMeter.class, TileVoltageMeter::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.voltage_meter.ordinal()]);
		BlockEntityHelper.register(registry, TileQuantumGenerator.class, TileQuantumGenerator::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.quantum_generator.ordinal()]);
		BlockEntityHelper.register(registry, TileAdjustableResistor.class, TileAdjustableResistor::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.adjustable_resistor.ordinal()]);
		BlockEntityHelper.register(registry, TileIncandescentLamp.class, TileIncandescentLamp::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.incandescent_lamp.ordinal()]);
		BlockEntityHelper.register(registry, TileElectricFurnace.class, TileElectricFurnace::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.electric_furnace.ordinal()]);
		BlockEntityHelper.register(registry, TileSE2RF.class, TileSE2RF::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.transformer_se2rf.ordinal()]);
		BlockEntityHelper.register(registry, TileRF2SE.class, TileRF2SE::new,
				BlockRegistry.blockElectronics[BlockElectronics.Type.transformer_se2rf.ordinal()]);
	}

	private static void electronics_2port(IForgeRegistry<BlockEntityType<?>> registry) {
		BlockEntityHelper.register(registry, TileAdjustableTransformer.class, TileAdjustableTransformer::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.adjustable_transformer
						.ordinal()]);
		BlockEntityHelper.register(registry, TileCurrentSensor.class, TileCurrentSensor::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.current_sensor.ordinal()]);
		BlockEntityHelper.register(registry, TileDiode.class, TileDiode::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.diode.ordinal()]);
		BlockEntityHelper.register(registry, TileSwitch.class, TileSwitch::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.circuit_breaker
						.ordinal()]);
		BlockEntityHelper.register(registry, TileRelay.class, TileRelay::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.relay.ordinal()]);
		BlockEntityHelper.register(registry, TilePowerMeter.class, TilePowerMeter::new,
				BlockRegistry.blockTwoPortElectronics[BlockTwoPortElectronics.Type.power_meter.ordinal()]);
	}

	public static void registerAll(IForgeRegistry<BlockEntityType<?>> registry) {
		electronics(registry);
		electronics_2port(registry);

		BlockEntityHelper.register(registry, TileCable.class, TileCable::new, BlockRegistry.blockCable);
		BlockEntityHelper.register(registry, TileWire.class, TileWire::new, BlockRegistry.blockWire);

		BlockEntityHelper.register(registry, TileMultiBlockPlaceHolder.class, TileMultiBlockPlaceHolder::new,
				makeBlockArray(BlockRegistry.concretePole35kV, BlockRegistry.metalPole35kV,
						BlockRegistry.distributionTransformer));
		BlockEntityHelper.register(registry, TilePoleMetal35kV.class, TilePoleMetal35kV::new,
				BlockRegistry.metalPole35kV);
		BlockEntityHelper.register(registry, TilePoleMetal35kV.Bottom.class, TilePoleMetal35kV.Bottom::new,
				BlockRegistry.metalPole35kV);
		BlockEntityHelper.register(registry, TilePoleConcrete35kV.class, TilePoleConcrete35kV::new,
				BlockRegistry.concretePole35kV);

		BlockEntityHelper.register(registry, TilePoleConcrete.Pole10Kv.Type0.class,
				TilePoleConcrete.Pole10Kv.Type0::new, BlockRegistry.concretePole);
		BlockEntityHelper.register(registry, TilePoleConcrete.Pole10Kv.Type1.class,
				TilePoleConcrete.Pole10Kv.Type1::new, BlockRegistry.concretePole);
		BlockEntityHelper.register(registry, TilePoleConcrete.Pole415vType0.class,
				TilePoleConcrete.Pole415vType0::new, BlockRegistry.concretePole);
		BlockEntityHelper.register(registry, TilePoleBranch.Type10kV.class, TilePoleBranch.Type10kV::new,
				BlockRegistry.concretePole);
		BlockEntityHelper.register(registry, TilePoleBranch.Type415V.class, TilePoleBranch.Type415V::new,
				BlockRegistry.concretePole);

		BlockEntityHelper.register(registry, TileCableJoint.Type10kV.class, TileCableJoint.Type10kV::new,
				BlockRegistry.cableJoint[BlockCableJoint.Type._10kv.ordinal()]);
		BlockEntityHelper.register(registry, TileCableJoint.Type415V.class, TileCableJoint.Type415V::new,
				BlockRegistry.cableJoint[BlockCableJoint.Type._415v.ordinal()]);

		BlockEntityHelper.register(registry, TilePowerTransformerPlaceHolder.class,
				TilePowerTransformerPlaceHolder::new, BlockRegistry.powerTransformer);
		BlockEntityHelper.register(registry, TilePowerTransformerPlaceHolder.Primary.class,
				TilePowerTransformerPlaceHolder.Primary::new, BlockRegistry.powerTransformer);
		BlockEntityHelper.register(registry, TilePowerTransformerPlaceHolder.Secondary.class,
				TilePowerTransformerPlaceHolder.Secondary::new, BlockRegistry.powerTransformer);
		BlockEntityHelper.register(registry, TilePowerTransformerPlaceHolder.Render.class,
				TilePowerTransformerPlaceHolder.Render::new, BlockRegistry.powerTransformer);
		BlockEntityHelper.register(registry, TilePowerTransformerWinding.Primary.class,
				TilePowerTransformerWinding.Primary::new, BlockRegistry.powerTransformer);
		BlockEntityHelper.register(registry, TilePowerTransformerWinding.Secondary.class,
				TilePowerTransformerWinding.Secondary::new, BlockRegistry.powerTransformer);

		BlockEntityHelper.register(registry, TileDistributionTransformer.Pole10kV.class,
				TileDistributionTransformer.Pole10kV::new, BlockRegistry.distributionTransformer);
		BlockEntityHelper.register(registry, TileDistributionTransformer.Pole415V.class,
				TileDistributionTransformer.Pole415V::new, BlockRegistry.distributionTransformer);
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
