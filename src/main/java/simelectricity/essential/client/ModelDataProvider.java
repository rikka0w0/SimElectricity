package simelectricity.essential.client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

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
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.common.data.ExistingFileHelper;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.IMetaProvider;
import rikka.librikka.model.GeneratedModelLoader;
import rikka.librikka.model.loader.ISimpleItemDataProvider;
import rikka.librikka.model.loader.ModelGeometryBakeContext;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.Essential;
import simelectricity.essential.ItemRegistry;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.BlockWire;
import simelectricity.essential.cable.ISECableMeta;
import simelectricity.essential.client.cable.CableModelLoader;
import simelectricity.essential.common.semachine.SEMachineBlock;
import simelectricity.essential.grid.BlockCableJoint;
import simelectricity.essential.grid.BlockPoleConcrete;
import simelectricity.essential.grid.BlockPoleConcrete35kV;
import simelectricity.essential.grid.transformer.BlockDistributionTransformer;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;

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
        for (int i=0; i<BlockRegistry.concretePole35kV.length; i++)
            concretePole35kV(BlockRegistry.concretePole35kV[i], i>0);
        for (int i=0; i<BlockRegistry.metalPole35kV.length; i++)
            metalPole35kV(BlockRegistry.metalPole35kV[i], i>0);
        for (BlockPoleConcrete block: BlockRegistry.concretePole)
            concretePole(block);

        for (BlockDistributionTransformer block: BlockRegistry.distributionTransformer)
            distributionTransformer(block);

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

        // Items
        registerSimpleItem(ItemRegistry.itemHVCable);
        registerSimpleItem(ItemRegistry.itemFutaTea);
        registerSimpleItem(ItemRegistry.itemMisc);
        registerSimpleItem(ItemRegistry.itemTools);
    }

    private void registerFake(Block block) {
        String domain = block.getRegistryName().getNamespace();
        String name = block.getRegistryName().getPath();
        
        JsonObject json = GeneratedModelLoader.placeholder();
        ResourceLocation modelResLoc = new ResourceLocation(domain, BuiltInModelLoader.dir + name);
        ModelFile modelGhost = customLoader(modelResLoc, json);
        getVariantBuilder(block).forAllStates(
                (blockstate)->ConfiguredModel.builder().modelFile(modelGhost).build());
        
        models().getBuilder("item/"+name).parent(modelGhost);
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
        JsonObject json = CableModelLoader.serialize(type, insulator, conductor, thickness);

        ResourceLocation modelResLoc = new ResourceLocation(domain, dirLoc + name);
        ModelFile modelFile = customLoader(modelResLoc, json);

        getVariantBuilder(block).forAllStates(
                (blockstate)->ConfiguredModel.builder().modelFile(modelFile).build());
        
        // Generate item model
        registerSimpleItem(block, "item/"+name+"_inventory");
    }
    
    private Pair<ModelFile, ModelFile> dir8Model(JsonObject commonProps, String domain, String path) {
        final JsonObject json = new JsonObject();
        commonProps.entrySet().forEach((entry)->json.add(entry.getKey(), entry.getValue()));
        json.addProperty("offaxis", false);
        ResourceLocation modelResLoc = new ResourceLocation(domain, path);
        ModelFile modelFile = customLoader(modelResLoc, json);
        
        final JsonObject jsonOffAxis = new JsonObject();
        commonProps.entrySet().forEach((entry)->jsonOffAxis.add(entry.getKey(), entry.getValue()));
        jsonOffAxis.addProperty("offaxis", true);
        ResourceLocation modelResLocOffAxis = new ResourceLocation(domain, path + "_45");
        ModelFile modelFileOffAxis = customLoader(modelResLocOffAxis, jsonOffAxis);
        
        return Pair.of(modelFile, modelFileOffAxis);
    }

    private void dir8Block(Block block, Pair<ModelFile, ModelFile> models) {
        dir8Block(block, models.getLeft(), models.getRight());
    }

    private void dir8Block(Block block, ModelFile modelFile, ModelFile modelOffAxis) {
        getVariantBuilder(block).forAllStates((blockstate)-> {
            DirHorizontal8 dir = blockstate.get(DirHorizontal8.prop);
            Pair<Integer, Boolean> encodedDir = ModelGeometryBakeContext.encodeDirection(dir);
            boolean offAxis = encodedDir.getRight();
            int rotation = encodedDir.getLeft();
            return ConfiguredModel.builder()
                    .modelFile(offAxis?modelOffAxis:modelFile)
                    .rotationY(rotation)
                    .build();
        });
    }
    
    ////////////////////
    /// Built-in models
    ////////////////////
    private void cableJoint(BlockCableJoint block) {
        String domain = block.getRegistryName().getNamespace();
        String name = block.getRegistryName().getPath();
        
        String type = block.meta() == BlockCableJoint.Type._415v ? "cable_joint_415v" : "cable_joint_10kv";
        JsonObject json = BuiltInModelLoader.serialize(type);
        
        // BlockStates
        dir8Block(block, dir8Model(json, domain, BuiltInModelLoader.dir + name));

        // Generate item model
        registerSimpleItem(block, "item/"+name+"_inventory");
    }

    private void concretePole35kV(Block block, boolean terminals) {
        String domain = block.getRegistryName().getNamespace();
        String name = block.getRegistryName().getPath();

        String typeName = "concrete_pole_35kv";
        JsonObject json = GeneratedModelLoader.placeholder();
        ResourceLocation modelResLoc = new ResourceLocation(domain, BuiltInModelLoader.dir + name + "_placeholder");
        ModelFile modelGhost = customLoader(modelResLoc, json);

        json = BuiltInModelLoader.serialize(typeName);
        json.addProperty("terminals", terminals);
        json.addProperty("isrod", true);
        ResourceLocation modelResLocRod = new ResourceLocation(domain, BuiltInModelLoader.dir + name + "_rod");
        ModelFile modelRod = customLoader(modelResLocRod, json);
        
        json = BuiltInModelLoader.serialize(typeName);
        json.addProperty("terminals", terminals);
        json.addProperty("isrod", false);
        ResourceLocation modelResLocHost = new ResourceLocation(domain, BuiltInModelLoader.dir + name + "_host");
        ModelFile modelHost = customLoader(modelResLocHost, json);
        
        getVariantBuilder(block).forAllStates((blockstate)-> {
            Direction facing = blockstate.get(BlockStateProperties.HORIZONTAL_FACING);
            BlockPoleConcrete35kV.Type type = blockstate.get(BlockPoleConcrete35kV.propType);
            ModelFile mdl = modelGhost;
            if (type == BlockPoleConcrete35kV.Type.pole || type == BlockPoleConcrete35kV.Type.pole_collisionbox)
                mdl = modelRod;
            else if (type == BlockPoleConcrete35kV.Type.host)
                mdl = modelHost;

            return ConfiguredModel.builder()
                    .modelFile(mdl)
                    .rotationY(ModelGeometryBakeContext.encodeDirection(facing))
                    .build();
        });
        
        // Generate item model
        registerSimpleItem(block, "item/"+name+"_inventory");
    }

    private void metalPole35kV(Block block, boolean terminals) {
        String domain = block.getRegistryName().getNamespace();
        String name = block.getRegistryName().getPath();

        JsonObject json = BuiltInModelLoader.serialize("metal_pole_35kv");
        json.addProperty("terminals", terminals);
        ResourceLocation modelResLoc = new ResourceLocation(domain, BuiltInModelLoader.dir + name);
        ModelFile modelFile = customLoader(modelResLoc, json);

        getVariantBuilder(block).forAllStates((blockstate)->ConfiguredModel.builder().modelFile(modelFile).build());

        // Generate item model
        registerSimpleItem(block, "item/"+name+"_inventory");
    }
    
    private void concretePole(BlockPoleConcrete block) {
        String domain = block.getRegistryName().getNamespace();
        String name = block.getRegistryName().getPath();

        JsonObject json = BuiltInModelLoader.serialize("concrete_pole");
        json.addProperty("part", block.meta().name());

        Pair<ModelFile, ModelFile> models = dir8Model(json, domain, BuiltInModelLoader.dir + name);
        
        // BlockStates
        dir8Block(block, models);

        // Generate item model
        models().getBuilder("item/"+name).parent(models.getLeft());
    }
    
    private void distributionTransformer(BlockDistributionTransformer block) {
        String domain = block.getRegistryName().getNamespace();
        String name = block.getRegistryName().getPath();

        final EnumDistributionTransformerBlockType blockType = block.meta();

        JsonObject json = BuiltInModelLoader.serialize("distribution_transformer");
        json.addProperty("part", blockType.getString());
        json.addProperty("formed", blockType.formed);
        ResourceLocation modelResLoc = new ResourceLocation(domain, BuiltInModelLoader.dir + name);
        ModelFile modelFile = customLoader(modelResLoc, json);
        
        getVariantBuilder(block).forAllStates((blockstate)-> {
        	ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(modelFile);
        	if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
        		Direction facing = blockstate.get(BlockStateProperties.HORIZONTAL_FACING);
        		builder.rotationY(ModelGeometryBakeContext.encodeDirection(facing));
        	}

        	return builder.build();
        });

        // Generate item model
        BlockModelBuilder itemModelBuilder = models().getBuilder("item/"+name);
        if (blockType.formed) {
        	itemModelBuilder.parent(
        		customLoader(
                	new ResourceLocation(domain, BuiltInModelLoader.dir + name + "_inventory"), 
                	GeneratedModelLoader.placeholder()
        		)
        	);
        } else {
        	itemModelBuilder.parent(modelFile);
        }
    }
}
