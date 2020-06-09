package simelectricity.essential.client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
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
import rikka.librikka.DirHorizontal8;
import rikka.librikka.IMetaProvider;
import rikka.librikka.model.loader.ISimpleItemDataProvider;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.Essential;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.BlockWire;
import simelectricity.essential.cable.ISECableMeta;
import simelectricity.essential.client.cable.CableModelLoader;
import simelectricity.essential.common.semachine.SEMachineBlock;
import simelectricity.essential.grid.BlockCableJoint;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;

public final class ModelDataProvider extends BlockStateProvider implements ISimpleItemDataProvider {    
	private final DataGenerator generator;
	private final ExistingFileHelper exfh;
	public ModelDataProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Essential.MODID, exFileHelper);
        this.generator = gen;
        this.exfh = exFileHelper;
    }
	
	private final Map<ResourceLocation, JsonObject> customLoaders = new HashMap<>();
    private ModelFile customLoader(ResourceLocation modelResLoc, JsonObject json) {
    	customLoaders.put(modelResLoc, json);
    	return new ModelFile.UncheckedModelFile(modelResLoc);
    }
	
    @Override
    public void act(DirectoryCache cache) throws IOException {
    	super.act(cache);

    	Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    	for (Entry<ResourceLocation, JsonObject> entry: customLoaders.entrySet()) {
            ResourceLocation loc = entry.getKey();
            Path path = generator.getOutputFolder().resolve(
            		"assets/" + loc.getNamespace() + "/models/" + loc.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, entry.getValue(), path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    	}
    }
	
	@Override
	protected void registerStatesAndModels() {
		// Blocks
		for (SEMachineBlock sem: BlockRegistry.blockElectronics)
			machineModel(sem);
		for (SEMachineBlock sem: BlockRegistry.blockTwoPortElectronics)
			machineModel(sem);
		
		for (BlockCable cable: BlockRegistry.blockCable)
			cableModel(cable, "cable");
		for (BlockWire wire: BlockRegistry.blockWire)
			cableModel(wire, "wire");
		
		for (BlockCableJoint block: BlockRegistry.cableJoint)
			cableJoint(block);
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

		final ModelFile modelFile = new ModelFile.ExistingModelFile(mcLoc("block/torch"), exfh);
		builder.forAllStates((blockstate)->ConfiguredModel.builder().modelFile(modelFile).build());
		
		registerSimpleItem(block, mcLoc("block/stone"));
	}
	
    private void machineModel(SEMachineBlock block) {
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
    
    private <T extends Block & IMetaProvider<ISECableMeta>> void cableModel(T block, String type) {
    	String domain = block.getRegistryName().getNamespace();
    	String name = block.getRegistryName().getPath();
    	float thickness = block.meta().thickness();
    	
    	String dirLoc = "block/" + type + "/";
    	ResourceLocation insulator = new ResourceLocation(domain, dirLoc + name + "_insulator");
    	ResourceLocation conductor = new ResourceLocation(domain, dirLoc + name + "_conductor");
    	JsonObject json = CableModelLoader.instance.serialize(type, insulator, conductor, thickness);

    	ResourceLocation modelResLoc = new ResourceLocation(domain, dirLoc + name);
    	ModelFile modelFile = customLoader(modelResLoc, json);
    	VariantBlockStateBuilder builder = getVariantBuilder(block);
    	builder.forAllStates((blockstate)->ConfiguredModel.builder().modelFile(modelFile).build());
    	
		// Generate item model
		registerSimpleItem(block, "item/"+name+"_inventory");
    }

    private void cableJoint(BlockCableJoint block) {
    	String domain = block.getRegistryName().getNamespace();
    	String name = block.getRegistryName().getPath();
    	
    	String dirLoc = "block/";
    	
    	String type = block.meta() == BlockCableJoint.Type._415v ? "cable_joint_415v" : "cable_joint_10kv";
		
    	JsonObject json = new JsonObject();
		json.addProperty("loader", BuiltInModelLoader.id.toString());
		json.addProperty("type", type);
		json.addProperty("offaxis", false);
    	ResourceLocation modelResLoc = new ResourceLocation(domain, dirLoc + name);
    	ModelFile modelFile = customLoader(modelResLoc, json);
    	
    	json = new JsonObject();
		json.addProperty("loader", BuiltInModelLoader.id.toString());
		json.addProperty("type", type);
		json.addProperty("offaxis", true);
    	ResourceLocation modelResLocOffAxis = new ResourceLocation(domain, dirLoc + name + "_45");
    	ModelFile modelFileOffAxis = customLoader(modelResLocOffAxis, json);
    	
    	getVariantBuilder(block).forAllStates((blockstate)-> {
    		DirHorizontal8 dir = blockstate.get(DirHorizontal8.prop);
    		boolean offAxis = dir != DirHorizontal8.fromDirection4(dir.toDirection4());
    		int rotation = ((dir.ordinal()&7)>>1) * 90;
    		return ConfiguredModel.builder().modelFile(offAxis?modelFileOffAxis:modelFile).rotationY(rotation).build();
    	});
		
		// Generate item model
		registerSimpleItem(block, "item/"+name+"_inventory");
    }
}
