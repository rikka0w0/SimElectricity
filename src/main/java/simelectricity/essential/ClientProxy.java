package simelectricity.essential;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import simelectricity.essential.client.coverpanel.BlockColorHandler;
import simelectricity.essential.client.semachine.SEMachineModelLoader;

public class ClientProxy extends CommonProxy {
    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public void registerColoredFacadeHost(Block block) {
    	Minecraft.getInstance().getBlockColors().register(BlockColorHandler.colorHandler, block);
    }
    
    @Override
    public void registerModelLoaders() {
    	ModelLoaderRegistry.registerLoader(SEMachineModelLoader.id, SEMachineModelLoader.instance);
    }
}
