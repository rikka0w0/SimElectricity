package simelectricity.essential;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.inventory.container.Container;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.gui.AutoGuiHandler;
import rikka.librikka.model.CodeBasedModel;
import rikka.librikka.model.loader.TERHelper;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.BlockWire;
import simelectricity.essential.client.cable.CableModel;
import simelectricity.essential.client.cable.WireModel;
import simelectricity.essential.client.coverpanel.LedPanelRender;
import simelectricity.essential.client.coverpanel.SupportRender;
import simelectricity.essential.client.coverpanel.VoltageSensorRender;
import simelectricity.essential.client.semachine.SEMachineModel;
import simelectricity.essential.client.semachine.SocketRender;
import simelectricity.essential.common.semachine.SEMachineBlock;
import simelectricity.essential.client.grid.pole.CableJointModel;
import simelectricity.essential.client.grid.pole.ConcretePole35kVModel;
import simelectricity.essential.client.grid.pole.ConcretePole35kVTER;
import simelectricity.essential.client.grid.pole.ConcretePoleTER;
import simelectricity.essential.client.grid.pole.MetalPole35kVBottomTER;
import simelectricity.essential.client.grid.pole.MetalPole35kVModel;
import simelectricity.essential.client.grid.pole.MetalPole35kVTER;
import simelectricity.essential.client.grid.pole.ConcretePoleModel;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.GridRenderMonitor;
//import simelectricity.essential.client.grid.transformer.FastTESRPowerTransformer;
//import simelectricity.essential.grid.transformer.TileDistributionTransformer;
//import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;
//import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Primary;
//import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Secondary;
import simelectricity.essential.grid.BlockCableJoint;
import simelectricity.essential.grid.BlockPoleConcrete35kV;
import simelectricity.essential.grid.BlockPoleConcrete;
import simelectricity.essential.grid.TilePoleBranch;
import simelectricity.essential.grid.TilePoleConcrete;
import simelectricity.essential.grid.TilePoleConcrete35kV;
import simelectricity.essential.grid.TilePoleMetal35kV;

@Mod.EventBusSubscriber(modid = Essential.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistrationHandler {
    public static Map<BlockState, CodeBasedModel> dynamicModels = new HashMap<>();
	
	public static void registerTileEntityRenders() {
		TERHelper.bind(TilePoleConcrete35kV.class, ConcretePole35kVTER::new);
		TERHelper.bind(TilePoleMetal35kV.class, MetalPole35kVTER::new);
		TERHelper.bind(TilePoleMetal35kV.Bottom.class, MetalPole35kVBottomTER::new);
		TERHelper.bind(TilePoleConcrete.Pole10Kv.Type0.class, ConcretePoleTER::new);
		TERHelper.bind(TilePoleConcrete.Pole10Kv.Type1.class, ConcretePoleTER::new);
		TERHelper.bind(TilePoleConcrete.Pole415vType0.class, ConcretePoleTER::new);
		TERHelper.bind(TilePoleBranch.Type10kV.class, ConcretePoleTER::new);
		TERHelper.bind(TilePoleBranch.Type415V.class, ConcretePoleTER::new);

		ClientRegistry.bindTileEntityRenderer(BlockRegistry.ttb_tetype, TESR::new);
//        FastTESRPowerPole.register(Primary.class);
//        FastTESRPowerPole.register(Secondary.class);
//        
//        FastTESRPowerPole.register(TileDistributionTransformer.Pole10kV.class);
//        FastTESRPowerPole.register(TileDistributionTransformer.Pole415V.class);

//        ClientRegistry.bindTileEntitySpecialRenderer(TilePowerTransformerPlaceHolder.Render.class, FastTESRPowerTransformer.instance);
	}
	
    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {   	
    	for (CodeBasedModel dynamicModel: dynamicModels.values())
    		dynamicModel.onPreTextureStitchEvent(event);
    	
    	MetalPole35kVModel.instance.onPreTextureStitchEvent(event);
    	
    	SocketRender.INSTANCE.onPreTextureStitchEvent(event);
    	SupportRender.INSTANCE.onPreTextureStitchEvent(event);
    	VoltageSensorRender.instance.onPreTextureStitchEvent(event);
    	LedPanelRender.instance.onPreTextureStitchEvent(event);
    	
    	FastTESRPowerPole.onPreTextureStitchEvent(event);
    }
    
    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Post event) {
//    	event.getMap().getSprite(location)
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {  	
    	Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
    	
		for (SEMachineBlock sem: BlockRegistry.blockElectronics)
			SEMachineModel.replace(registry, sem);
		for (SEMachineBlock sem: BlockRegistry.blockTwoPortElectronics)
			SEMachineModel.replace(registry, sem);
    	
    	dynamicModels.forEach((blockstate, dynamicModel) -> {
    		dynamicModel.onModelBakeEvent();
    		registry.put(BlockModelShapes.getModelLocation(blockstate), dynamicModel);
    	});
    	
    	MetalPole35kVModel.instance.onModelBakeEvent();
		for (int i=0; i<BlockRegistry.metalPole35kV.length; i++) {
			BlockRegistry.metalPole35kV[i].getStateContainer().getValidStates().forEach((blockstate) -> {
				registry.put(BlockModelShapes.getModelLocation(blockstate), MetalPole35kVModel.instance);
			});
		}
    	
    	SocketRender.INSTANCE.onModelBakeEvent();
    	SupportRender.INSTANCE.onModelBakeEvent();
    	VoltageSensorRender.instance.onModelBakeEvent();
    	LedPanelRender.instance.onModelBakeEvent();
    	
    	FastTESRPowerPole.onModelBakeEvent();
    	

		// Assign item models
    	for (int i=0; i<BlockRegistry.concretePole.length; i++) {
    		Block block = BlockRegistry.concretePole[i];
    		ModelResourceLocation resLoc = new ModelResourceLocation(block.getRegistryName(), "inventory");
    		IBakedModel newItemModel = event.getModelRegistry().get(BlockModelShapes.getModelLocation(block.getDefaultState()));
    		registry.put(resLoc, newItemModel);
    	}
    	
//      event.getModelRegistry().put(new ModelResourceLocation(), null);
//  	Minecraft.getInstance().getModelManager().getModel(location)
//  	net.minecraft.client.renderer.model.BlockModel
    }
    
    
    /*
     * Event order:
     * FMLClientSetupEvent
     * TextureStitchEvent.Pre
     * TextureStitchEvent.Post
     * ModelBakeEvent
     */
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event){
        //Initialize client-side API
        SEEAPI.coloredBlocks = new LinkedList<Block>();
        
		// Register Gui
//		ScreenManager.registerFactory(BlockRegistry.cAdjustableResistor, GuiAdjustableResistor::new);
		for (Class<? extends Container> containerCls: BlockRegistry.registeredGuiContainers) {
			AutoGuiHandler.registerContainerGui(containerCls);
		}
        
        Minecraft.getInstance().getBlockColors().register((blockstate, lightreader, pos, tintIndex) -> {
        	return Minecraft.getInstance().getBlockColors().getColor(lightreader.getBlockState(pos.down()), lightreader, pos, tintIndex);
        }, BlockRegistry.ttb);
        RenderTypeLookup.setRenderLayer(BlockRegistry.ttb, (layer)->true);
        
//    	SEEAPI.coloredBlocks.add(BlockRegistry.blockCable);
    	ClientRegistrationHandler.registerTileEntityRenders();
		
		// Was Block::getBlockLayer
		for (SEMachineBlock sem: BlockRegistry.blockElectronics) {
			RenderTypeLookup.setRenderLayer(sem, RenderType.getCutoutMipped());
		}
		for (SEMachineBlock sem: BlockRegistry.blockTwoPortElectronics) {
			RenderTypeLookup.setRenderLayer(sem, RenderType.getCutoutMipped());
		}
		
		for (BlockCable cable: BlockRegistry.blockCable) {
			RenderTypeLookup.setRenderLayer(cable, (layer)-> {
				return layer==RenderType.getSolid() || layer==RenderType.getCutoutMipped();
			}
			);
			
			cable.getStateContainer().getValidStates().forEach((blockstate) ->
				dynamicModels.put(blockstate, new CableModel(cable))
			);
		}
		for (BlockWire wire: BlockRegistry.blockWire) {
			RenderTypeLookup.setRenderLayer(wire, (layer)-> {
				return layer==RenderType.getSolid();
			}
			);
			
			wire.getStateContainer().getValidStates().forEach((blockstate) ->
				dynamicModels.put(blockstate, new WireModel(wire))
			);
		}

		BlockRegistry.cableJoint[BlockCableJoint.Type._10kv.ordinal()]
				.getStateContainer().getValidStates().forEach((blockstate) -> {
			dynamicModels.put(blockstate, new CableJointModel.Type10kV(blockstate));
		});
		BlockRegistry.cableJoint[BlockCableJoint.Type._415v.ordinal()]
				.getStateContainer().getValidStates().forEach((blockstate) -> {
			dynamicModels.put(blockstate, new CableJointModel.Type415V(blockstate));
		});
		
		for (int i=0; i<BlockRegistry.concretePole35kV.length; i++) {
			final int modelType = i;
			BlockRegistry.concretePole35kV[i].getStateContainer().getValidStates().forEach((blockstate) -> {
				Direction facing = blockstate.get(BlockStateProperties.HORIZONTAL_FACING);
				BlockPoleConcrete35kV.Type type = blockstate.get(BlockPoleConcrete35kV.propType);
				
				if (type == BlockPoleConcrete35kV.Type.pole || type == BlockPoleConcrete35kV.Type.pole_collisionbox)
					dynamicModels.put(blockstate, new ConcretePole35kVModel(facing, modelType, true));
				else if (type == BlockPoleConcrete35kV.Type.host)
					dynamicModels.put(blockstate, new ConcretePole35kVModel(facing, modelType, false));
			});
		}
		
		for (int i=0; i<BlockRegistry.concretePole.length; i++) {
			final int modelType = i;
			BlockRegistry.concretePole[i].getStateContainer().getValidStates().forEach((blockstate) -> {
				DirHorizontal8 facing = blockstate.get(DirHorizontal8.prop);
				BlockPoleConcrete block = (BlockPoleConcrete) blockstate.getBlock();
				BlockPoleConcrete.Type type = block.blockType;

				dynamicModels.put(blockstate, new ConcretePoleModel(type, facing));
			});
		}

		MinecraftForge.EVENT_BUS.register(GridRenderMonitor.instance);
		
//		new ResourceLocation("minecraft","elements")
//    	ModelLoaderRegistry.registerLoader(new ResourceLocation("librikka","virtual"), loader);
//		ModelLoaderRegistry.getModel("", deserializationContext, data)

		// Get unbaked model
//		IUnbakedModel adj = ModelLoader.instance().getUnbakedModel(new ResourceLocation(Essential.MODID, "block/electronics_adjustable_resistor"));
//		IUnbakedModel machine = ModelLoader.instance().getUnbakedModel(new ResourceLocation(Essential.MODID, "block/machine"));
//		adj = null;
		// ModelBakery public IUnbakedModel getUnbakedModel(ResourceLocation modelLocation)
	}	
}
