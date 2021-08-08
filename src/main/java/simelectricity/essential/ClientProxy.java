package simelectricity.essential;

import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import simelectricity.essential.client.coverpanel.BlockColorHandler;

public class ClientProxy extends CommonProxy {
    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
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
