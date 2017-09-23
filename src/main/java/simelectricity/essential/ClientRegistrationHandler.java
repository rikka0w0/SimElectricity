package simelectricity.essential;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import rikka.librikka.model.loader.AdvancedModelLoader;
import simelectricity.essential.client.cable.CableStateMapper;
import simelectricity.essential.client.grid.GridStateMapper;
import simelectricity.essential.client.grid.TileRenderPowerPole;
import simelectricity.essential.client.grid.transformer.PowerTransformerStateMapper;
import simelectricity.essential.client.semachine.SEMachineStateMapper;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TilePowerPole;
import simelectricity.essential.grid.TilePowerPole2;
import simelectricity.essential.grid.TilePowerPole3.Pole10Kv;
import simelectricity.essential.grid.TilePowerPole3.Pole415vType0;
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

        PowerTransformerStateMapper ptStateMapper = new PowerTransformerStateMapper(Essential.MODID);
        loader.registerModelLoader(ptStateMapper);
        ptStateMapper.register(BlockRegistry.powerTransformer);
	}
	
	public static void registerTileEntityRenders() {
        TileRenderPowerPole.register(TileCableJoint.class);
        TileRenderPowerPole.register(TilePowerPole.class);
        TileRenderPowerPole.register(TilePowerPole2.class);
        TileRenderPowerPole.register(Primary.class);
        TileRenderPowerPole.register(Secondary.class);
        TileRenderPowerPole.register(Pole10Kv.Type0.class);
        TileRenderPowerPole.register(Pole10Kv.Type1.class);
        TileRenderPowerPole.register(Pole415vType0.class);
	}
}
