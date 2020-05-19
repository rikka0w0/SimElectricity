package simelectricity.essential.client;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import rikka.librikka.model.loader.ISimpleItemDataProvider;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.Essential;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.common.semachine.SEMachineBlock;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;

public final class ModelDataProvider extends BlockStateProvider implements ISimpleItemDataProvider {
	private final ExistingFileHelper exfh;
	public ModelDataProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Essential.MODID, exFileHelper);
        this.exfh = exFileHelper;
    }
	
	@Override
	protected void registerStatesAndModels() {
		// Blocks
		for (SEMachineBlock sem: BlockRegistry.blockElectronics)
			registerSEMBlock(sem);
		for (SEMachineBlock sem: BlockRegistry.blockTwoPortElectronics)
			registerSEMBlock(sem);
		
		for (Block cable: BlockRegistry.blockCable)
			registerDynamic(cable);
		for (Block wire: BlockRegistry.blockWire)
			registerDynamic(wire);
		
		for (Block block: BlockRegistry.cableJoint)
			registerDynamic(block);
		for (Block block: BlockRegistry.concretePole35kV)
			registerDynamic(block);
		for (Block block: BlockRegistry.metalPole35kV)
			registerDynamic(block);
		for (Block block: BlockRegistry.concretePole)
			registerDynamic2(block);
		
		for (BlockPowerTransformer block: BlockRegistry.powerTransformer) {
			if (block.blockType.formed) {
				registerFake(block);
			} else {
				VariantBlockStateBuilder builder = getVariantBuilder(block);
				String namespace = block.getRegistryName().getNamespace();
				String blockName =  block.getRegistryName().getPath();
				ModelFile modelFile = new ModelFile.ExistingModelFile(new ResourceLocation(namespace, "block/"+blockName), exfh);	
				builder.forAllStates((blockstate) -> ConfiguredModel.builder().modelFile(modelFile).build());
				
				BlockModelBuilder itemModelBuilder = models().getBuilder("item/"+blockName);
				itemModelBuilder.parent(modelFile);
			}
		}
		
		for (Block block: BlockRegistry.distributionTransformer)
			registerDynamic2(block);

		
		// Items
		registerSimpleItem(ItemRegistry.itemHVCable);
		registerSimpleItem(ItemRegistry.itemFutaTea);
		registerSimpleItem(ItemRegistry.itemMisc);
		registerSimpleItem(ItemRegistry.itemTools);
	}
	/**
	 * Both the block model and item model are dynamic
	 * @param block
	 */
	private void registerDynamic2(Block block) {
		VariantBlockStateBuilder builder = getVariantBuilder(block);
		String namespace = block.getRegistryName().getNamespace();
		String blockName =  block.getRegistryName().getPath();
		
		final ModelFile modelFile = new ModelFile.ExistingModelFile(mcLoc("block/torch"), exfh);
		builder.forAllStates((blockstate)->ConfiguredModel.builder().modelFile(modelFile).build());
		
		String itemModelPath = "item/"+block.getRegistryName().getPath();
		BlockModelBuilder itemModelBuilder = models().getBuilder(itemModelPath);
		itemModelBuilder.parent(new ModelFile.ExistingModelFile(mcLoc("item/generated"), models().existingFileHelper));
		itemModelBuilder.texture("layer0", "minecraft:block/stone");
	}
	
	/**
	 * Block model is dynamic, item model is from a png image
	 * @param block
	 */
	private void registerDynamic(Block block) {
		VariantBlockStateBuilder builder = getVariantBuilder(block);
		String namespace = block.getRegistryName().getNamespace();
		String blockName =  block.getRegistryName().getPath();

//		BlockModelBuilder cableDummyModel = models().getBuilder("block/"+blockName);
//		cableDummyModel.parent(new ModelFile.ExistingModelFile(mcLoc("block/cube_all"), exfh));
//		cableDummyModel.texture("all", mcLoc("block/stone"));
		
		final ModelFile modelFile = new ModelFile.ExistingModelFile(mcLoc("block/torch"), exfh);
		builder.forAllStates((blockstate)->ConfiguredModel.builder().modelFile(modelFile).build());
		
		// Generate item model
		registerSimpleItem(block, "item/"+blockName+"_inventory");
	}
	
	private void registerFake (Block block) {
		VariantBlockStateBuilder builder = getVariantBuilder(block);
		String namespace = block.getRegistryName().getNamespace();
		String blockName =  block.getRegistryName().getPath();

		final ModelFile modelFile = new ModelFile.ExistingModelFile(mcLoc("block/torch"), exfh);
		builder.forAllStates((blockstate)->ConfiguredModel.builder().modelFile(modelFile).build());
		
		registerSimpleItem(block, mcLoc("block/stone"));
	}
	
    private void registerSEMBlock(SEMachineBlock block) {
		VariantBlockStateBuilder builder = getVariantBuilder(block);
		String namespace = block.getRegistryName().getNamespace();
		String blockName =  block.getRegistryName().getPath();
		ModelFile modelFile = new ModelFile.ExistingModelFile(new ResourceLocation(namespace, "block/"+blockName), exfh);
		ModelFile modelFile2 = null;
		
		int[] angleYs = new int[] {0, 0, 0, 180, 270, 90};
		int[] angleXs = new int[] {90, 270, 0, 0, 0, 0};
		
		boolean hasSecondState = block.hasSecondState();
		if (hasSecondState) {
			modelFile2 = new ModelFile.ExistingModelFile(new ResourceLocation(namespace, "block/"+blockName+"_2"), exfh);
		}
		
		for (Direction dir : BlockStateProperties.FACING.getAllowedValues()) {
			int angleY = angleYs[dir.ordinal()];
			int angleX = angleXs[dir.ordinal()];
			
			if (hasSecondState) {
				builder
	            .partialState()
	                .with(BlockStateProperties.FACING, dir)
	                .with(BlockStateProperties.POWERED, false)
	                .modelForState()
	                    .modelFile(modelFile2)
	                    .rotationY(angleY)
	                    .rotationX(angleX)
	                .addModel();
				builder
	            .partialState()
	                .with(BlockStateProperties.FACING, dir)
	                .with(BlockStateProperties.POWERED, true)
	                .modelForState()
	                    .modelFile(modelFile)
	                    .rotationY(angleY)
	                    .rotationX(angleX)
	                .addModel();
			} else {
				builder
	            .partialState()
	                .with(BlockStateProperties.FACING, dir)
	                .modelForState()
	                    .modelFile(modelFile)
	                    .rotationY(angleY)
	                    .rotationX(angleX)
	                .addModel();		
			}
		}
		
		// Generate item model
		BlockModelBuilder itemModelBuilder = models().getBuilder("item/"+blockName);
		itemModelBuilder.parent(modelFile);
		// TODO: remove useObjModel() in the future
		if (block.useObjModel()) {
			itemModelBuilder.transforms()
			.transform(Perspective.GUI)
			.rotation(30, 225, 0)
			.translation(1, 0, 0)
			.scale(0.7F)
			.end();
			
			itemModelBuilder.transforms()
			.transform(Perspective.FIRSTPERSON_LEFT)
			.scale(0.5F)
			.end();
			
			itemModelBuilder.transforms()
			.transform(Perspective.FIRSTPERSON_RIGHT)
			.scale(0.5F)
			.end();
			
			itemModelBuilder.transforms()
			.transform(Perspective.THIRDPERSON_LEFT)
			.rotation(30, 45, 0)
			.scale(0.5F)
			.end();
			
			itemModelBuilder.transforms()
			.transform(Perspective.THIRDPERSON_RIGHT)
			.rotation(30, 45, 0)
			.scale(0.5F)
			.end();
			
			itemModelBuilder.transforms()
			.transform(Perspective.GROUND)
			.scale(0.35F)
			.end();
		}
    }
}
