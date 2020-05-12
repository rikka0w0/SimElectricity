package simelectricity.essential;

import java.util.HashMap;
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
import net.minecraftforge.client.event.ModelRegistryEvent;
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
import simelectricity.essential.client.coverpanel.BlockColorHandler;
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
import simelectricity.essential.client.grid.transformer.DistributionTransformerComponentModel;
import simelectricity.essential.client.grid.transformer.DistributionTransformerFormedModel;
import simelectricity.essential.client.grid.transformer.FastTESRPowerTransformer;
import simelectricity.essential.client.grid.pole.ConcretePoleModel;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.GridRenderMonitor;
import simelectricity.essential.grid.BlockCableJoint;
import simelectricity.essential.grid.BlockPoleConcrete35kV;
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

@Mod.EventBusSubscriber(modid = Essential.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
		TERHelper.bind(TilePowerTransformerPlaceHolder.Render.class, FastTESRPowerTransformer::new);
		TERHelper.bind(TilePowerTransformerWinding.Primary.class, FastTESRPowerPole::new);
		TERHelper.bind(TilePowerTransformerWinding.Secondary.class, FastTESRPowerPole::new);
		
		ClientRegistry.bindTileEntityRenderer(BlockRegistry.ttb_tetype, TESR::new);
		TERHelper.bind(TileDistributionTransformer.Pole10kV.class, FastTESRPowerPole::new);
		TERHelper.bind(TileDistributionTransformer.Pole415V.class, FastTESRPowerPole::new);
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onModelRegistryEvent(ModelRegistryEvent event) {
		FastTESRPowerTransformer.onModelRegistryEvent();
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
    	FastTESRPowerTransformer.onPreTextureStitchEvent(event);
    	DistributionTransformerFormedModel.instance.onPreTextureStitchEvent(event);
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
    	FastTESRPowerTransformer.onModelBakeEvent();
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
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event){        
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
		
        for (Block block: SEEAPI.coloredBlocks) {
            Minecraft.getInstance().getBlockColors().register(BlockColorHandler.colorHandler, block);
        }
	}
//	ModelLoaderRegistry.registerLoader(new ResourceLocation("librikka","virtual"), loader);
//	ModelLoaderRegistry.getModel("", deserializationContext, data)

	// Get unbaked model
//	IUnbakedModel adj = ModelLoader.instance().getUnbakedModel(new ResourceLocation(Essential.MODID, "block/electronics_adjustable_resistor"));
//	IUnbakedModel machine = ModelLoader.instance().getUnbakedModel(new ResourceLocation(Essential.MODID, "block/machine"));
//	adj = null;
	// ModelBakery public IUnbakedModel getUnbakedModel(ResourceLocation modelLocation)
}
