package simelectricity.essential;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.block.ICustomBoundingBox;
import rikka.librikka.gui.AutoGuiHandler;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.TERHelper;
import simelectricity.essential.cable.BlockWire;
import simelectricity.essential.client.BuiltInModelLoader;
import simelectricity.essential.client.ModelDataProvider;
import simelectricity.essential.client.cable.CableModelLoader;
import simelectricity.essential.client.coverpanel.LedPanelRender;
import simelectricity.essential.client.coverpanel.SupportRender;
import simelectricity.essential.client.coverpanel.VoltageSensorRender;
import simelectricity.essential.client.semachine.SEMachineModelLoader;
import simelectricity.essential.client.semachine.SocketRender;
import simelectricity.essential.common.semachine.SEMachineBlock;
import simelectricity.essential.coverpanel.CoverPanelRegistry;
import simelectricity.essential.client.grid.pole.ConcretePole35kVTER;
import simelectricity.essential.client.grid.pole.ConcretePoleTER;
import simelectricity.essential.client.grid.pole.MetalPole35kVBottomTER;
import simelectricity.essential.client.grid.pole.MetalPole35kVTER;
import simelectricity.essential.client.grid.transformer.DistributionTransformerComponentModel;
import simelectricity.essential.client.grid.transformer.DistributionTransformerFormedModel;
import simelectricity.essential.client.grid.transformer.PowerTransformerTER;
import simelectricity.essential.client.grid.pole.ConcretePoleModel;
import simelectricity.essential.client.grid.PowerPoleTER;
import simelectricity.essential.client.grid.GridRenderMonitor;
import simelectricity.essential.grid.BlockPoleConcrete;
import simelectricity.essential.grid.TilePoleBranch;
import simelectricity.essential.grid.TilePoleConcrete;
import simelectricity.essential.grid.TilePoleConcrete35kV;
import simelectricity.essential.grid.TilePoleMetal35kV;
import simelectricity.essential.grid.transformer.BlockDistributionTransformer;
import simelectricity.essential.grid.transformer.EnumDistributionTransformerBlockType;
import simelectricity.essential.grid.transformer.TileDistributionTransformer;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Essential.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistrationHandler {
    public static Map<BlockState, CodeBasedModel> dynamicModels = new HashMap<>();
	
	public static void registerTileEntityRenders() {
		// ConcretePole35kV
		TERHelper.bind(TilePoleConcrete35kV.class, ConcretePole35kVTER::new);
		
		// MetalPole35kV
		TERHelper.bind(TilePoleMetal35kV.class, MetalPole35kVTER::new);
		TERHelper.bind(TilePoleMetal35kV.Bottom.class, MetalPole35kVBottomTER::new);
		
		// ConcretePole
		TERHelper.bind(TilePoleConcrete.Pole10Kv.Type0.class, ConcretePoleTER::new);
		TERHelper.bind(TilePoleConcrete.Pole10Kv.Type1.class, ConcretePoleTER::new);
		TERHelper.bind(TilePoleConcrete.Pole415vType0.class, ConcretePoleTER::new);
		TERHelper.bind(TilePoleBranch.Type10kV.class, ConcretePoleTER::new);
		TERHelper.bind(TilePoleBranch.Type415V.class, ConcretePoleTER::new);

		// PowerTransformer
		TERHelper.bind(TilePowerTransformerPlaceHolder.Render.class, PowerTransformerTER::new);
		TERHelper.bind(TilePowerTransformerWinding.Primary.class, PowerPoleTER::new);
		TERHelper.bind(TilePowerTransformerWinding.Secondary.class, PowerPoleTER::new);
		
		TERHelper.bind(TileDistributionTransformer.Pole10kV.class, PowerPoleTER::new);
		TERHelper.bind(TileDistributionTransformer.Pole415V.class, PowerPoleTER::new);
	}
	
	public static void registerModelLoaders() {
    	ModelLoaderRegistry.registerLoader(SEMachineModelLoader.id, SEMachineModelLoader.instance);
    	ModelLoaderRegistry.registerLoader(CableModelLoader.id, CableModelLoader.instance);
    	ModelLoaderRegistry.registerLoader(BuiltInModelLoader.id, BuiltInModelLoader.instance);
	}
	
	@SubscribeEvent
	public static void onModelRegistryEvent(ModelRegistryEvent event) {
		PowerTransformerTER.onModelRegistryEvent();
		
		// This cannot be placed here yet, due to Forge's bug, use proxy as a temp replacement
//		registerModelLoaders()
	}
	
    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {   	
    	for (CodeBasedModel dynamicModel: dynamicModels.values())
    		dynamicModel.onPreTextureStitchEvent(event);
    	
    	SocketRender.INSTANCE.onPreTextureStitchEvent(event);
    	SupportRender.INSTANCE.onPreTextureStitchEvent(event);
    	VoltageSensorRender.instance.onPreTextureStitchEvent(event);
    	LedPanelRender.instance.onPreTextureStitchEvent(event);
    	
    	PowerPoleTER.onPreTextureStitchEvent(event);
    	PowerTransformerTER.onPreTextureStitchEvent(event);
    	DistributionTransformerFormedModel.instance.onPreTextureStitchEvent(event);
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
    	Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

    	dynamicModels.forEach((blockstate, dynamicModel) -> {
    		dynamicModel.onModelBakeEvent();
    		registry.put(BlockModelShapes.getModelLocation(blockstate), dynamicModel);
    	});

    	SocketRender.INSTANCE.onModelBakeEvent();
    	SupportRender.INSTANCE.onModelBakeEvent();
    	VoltageSensorRender.instance.onModelBakeEvent();
    	LedPanelRender.instance.onModelBakeEvent();
    	
    	PowerPoleTER.onModelBakeEvent();
    	PowerTransformerTER.onModelBakeEvent();
    	DistributionTransformerFormedModel.instance.onModelBakeEvent();

		// Assign item models
    	for (int i=0; i<BlockRegistry.concretePole.length; i++) {
    		Block block = BlockRegistry.concretePole[i];
			BlockState blockstate = block.getDefaultState();
    		ModelResourceLocation resLoc = new ModelResourceLocation(block.getRegistryName(), "inventory");
    		IBakedModel newItemModel = event.getModelRegistry().get(BlockModelShapes.getModelLocation(blockstate));
    		registry.put(resLoc, newItemModel);
    	}
    	
    	// Assign item models and formed block models
		for (EnumDistributionTransformerBlockType blockType: EnumDistributionTransformerBlockType.values()) {
			Block block = BlockRegistry.distributionTransformer[blockType.ordinal()];
			if (blockType.formed) {
				registry.put(BlockModelShapes.getModelLocation(block.getDefaultState()), DistributionTransformerFormedModel.instance);
			} else {
				BlockState blockstate = block.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
	    		ModelResourceLocation resLoc = new ModelResourceLocation(block.getRegistryName(), "inventory");
	    		IBakedModel newItemModel = event.getModelRegistry().get(BlockModelShapes.getModelLocation(blockstate));
	    		registry.put(resLoc, newItemModel);
			}
		}
    }
    
    
    /*
     * Event order:
     * FMLClientSetupEvent
     * TextureStitchEvent.Pre
     * TextureStitchEvent.Post
     * ModelBakeEvent
     */

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event){
		MinecraftForge.EVENT_BUS.addListener(ICustomBoundingBox::onBlockHighLight);
		
		// Register Gui
//		ScreenManager.registerFactory(BlockRegistry.cAdjustableResistor, GuiAdjustableResistor::new);
		BlockRegistry.registeredGuiContainers.forEach(AutoGuiHandler::registerContainerGui);

    	ClientRegistrationHandler.registerTileEntityRenders();
		
    	Predicate<RenderType> multiLayer = (layer) -> {
    		return layer==RenderType.getSolid() || 
    				layer==RenderType.getCutout()|| 
    				layer==RenderType.getCutoutMipped();
    	};
    	
		// Was Block::getBlockLayer
		for (SEMachineBlock sem: BlockRegistry.blockElectronics) {
			RenderTypeLookup.setRenderLayer(sem, multiLayer);
		}
		for (SEMachineBlock sem: BlockRegistry.blockTwoPortElectronics) {
			RenderTypeLookup.setRenderLayer(sem, multiLayer);
		}
		
		for (Block block: BlockRegistry.blockCable) {
			RenderTypeLookup.setRenderLayer(block, multiLayer);
		}
		
		for (BlockWire wire: BlockRegistry.blockWire) {
			RenderTypeLookup.setRenderLayer(wire, RenderType.getSolid());
		}

		for (int i=0; i<BlockRegistry.concretePole.length; i++) {
			BlockRegistry.concretePole[i].getStateContainer().getValidStates().forEach((blockstate) -> {
				DirHorizontal8 facing = blockstate.get(DirHorizontal8.prop);
				BlockPoleConcrete block = (BlockPoleConcrete) blockstate.getBlock();
				BlockPoleConcrete.Type type = block.blockType;

				dynamicModels.put(blockstate, new ConcretePoleModel(type, facing));
			});
		}
		
		for (int i=0; i<BlockRegistry.distributionTransformer.length; i++) {
			BlockDistributionTransformer block = BlockRegistry.distributionTransformer[i];
			final EnumDistributionTransformerBlockType blockType = block.blockType;
			if (!blockType.formed)
				block.getStateContainer().getValidStates().forEach((blockstate) -> {
					Direction facing = blockstate.has(BlockStateProperties.HORIZONTAL_FACING) ? 
							blockstate.get(BlockStateProperties.HORIZONTAL_FACING) : null;
	
					dynamicModels.put(blockstate, new DistributionTransformerComponentModel(blockType, facing));
				});
		}
		
		MinecraftForge.EVENT_BUS.register(GridRenderMonitor.instance);
		CoverPanelRegistry.INSTANCE.registerAllColoredFacadeHost();
	}
	
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper exfh = event.getExistingFileHelper();

		if (event.includeClient()) {
			generator.addProvider(new ModelDataProvider(generator, exfh));
		}
	}
	
//	ModelLoaderRegistry.registerLoader(new ResourceLocation("librikka","virtual"), loader);
//	ModelLoaderRegistry.getModel("", deserializationContext, data)
}
