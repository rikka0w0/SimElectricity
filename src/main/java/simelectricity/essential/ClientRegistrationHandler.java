package simelectricity.essential;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import rikka.librikka.model.loader.AdvancedModelLoader;
import simelectricity.essential.client.cable.CableStateMapper;
import simelectricity.essential.client.coverpanel.SupportRender;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.GridStateMapper;
import simelectricity.essential.client.grid.transformer.FastTESRPowerTransformer;
import simelectricity.essential.client.grid.transformer.TransformerStateMapper;
import simelectricity.essential.client.semachine.SEMachineStateMapper;
import simelectricity.essential.client.semachine.SocketRender;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TilePowerPole;
import simelectricity.essential.grid.TilePowerPole2;
import simelectricity.essential.grid.TilePowerPole3.Pole10Kv;
import simelectricity.essential.grid.TilePowerPole3.Pole415vType0;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Primary;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Secondary;

@Mod.EventBusSubscriber(modid = Essential.MODID, value = Side.CLIENT)
public class ClientRegistrationHandler {
	@SubscribeEvent
	public static void registerModel(ModelRegistryEvent event) {
        AdvancedModelLoader loader = new AdvancedModelLoader(Essential.MODID);
        loader.registerInventoryIcon(ItemRegistry.itemHVCable);
        loader.registerInventoryIcon(ItemRegistry.itemVitaTea);
        loader.registerInventoryIcon(ItemRegistry.itemMisc);
        loader.registerInventoryIcon(ItemRegistry.itemTools);

        SEMachineStateMapper semStateMapper = new SEMachineStateMapper(Essential.MODID);
        loader.registerModelLoader(semStateMapper);
        semStateMapper.register(BlockRegistry.blockElectronics);
        semStateMapper.register(BlockRegistry.blockTwoPortElectronics);

        CableStateMapper cStateMapper = new CableStateMapper(Essential.MODID);
        loader.registerModelLoader(cStateMapper);
        cStateMapper.register(BlockRegistry.blockCable);
        loader.registerInventoryIcon(BlockRegistry.blockCable.itemBlock);

        GridStateMapper gStateMapper = new GridStateMapper(Essential.MODID);
        loader.registerModelLoader(gStateMapper);
        gStateMapper.register(BlockRegistry.cableJoint);
        loader.registerInventoryIcon(BlockRegistry.cableJoint.itemBlock);
        gStateMapper.register(BlockRegistry.powerPoleBottom);
        loader.registerInventoryIcon(BlockRegistry.powerPoleBottom.itemBlock);
        gStateMapper.register(BlockRegistry.powerPoleCollisionBox);
        loader.registerInventoryIcon(BlockRegistry.powerPoleCollisionBox.itemBlock);
        gStateMapper.register(BlockRegistry.powerPoleTop);
        loader.registerInventoryIcon(BlockRegistry.powerPoleTop.itemBlock);
        gStateMapper.register(BlockRegistry.powerPole2);
        loader.registerInventoryIcon(BlockRegistry.powerPole2.itemBlock);
        gStateMapper.register(BlockRegistry.powerPole3);

        TransformerStateMapper transformerStateMapper = new TransformerStateMapper(Essential.MODID);
        loader.registerModelLoader(transformerStateMapper);
        transformerStateMapper.register(BlockRegistry.powerTransformer);
        transformerStateMapper.register(BlockRegistry.distributionTransformer);
	}
	
    @SubscribeEvent
    public static void preStitchTexture(TextureStitchEvent.Pre event) {
    	TextureMap map = event.getMap();
    	
    	SocketRender.stitchTexture(map);
    	SupportRender.stitchTexture(map);
    	FastTESRPowerPole.stitchTexture(map);
    }
	
	public static void registerTileEntityRenders() {
		FastTESRPowerPole.register(TileCableJoint.class);
        FastTESRPowerPole.register(TilePowerPole.class);
        FastTESRPowerPole.register(TilePowerPole2.class);
        FastTESRPowerPole.register(Primary.class);
        FastTESRPowerPole.register(Secondary.class);
        FastTESRPowerPole.register(Pole10Kv.Type0.class);
        FastTESRPowerPole.register(Pole10Kv.Type1.class);
        FastTESRPowerPole.register(Pole415vType0.class);
        
        ClientRegistry.bindTileEntitySpecialRenderer(TilePowerTransformerPlaceHolder.Render.class, new FastTESRPowerTransformer());
	}
}
