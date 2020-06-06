package simelectricity.essential;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import simelectricity.essential.client.coverpanel.BlockColorHandler;

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
    	// Need this to prevent crash during data generation
    	if (Minecraft.getInstance()==null || Minecraft.getInstance().getResourceManager() == null)
    		return;

    	ClientRegistrationHandler.registerModelLoaders();
    }
}
