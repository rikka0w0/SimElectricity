package simelectricity.essential;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.client.coverpanel.LedPanelRender;
import simelectricity.essential.client.coverpanel.VoltageSensorRender;
import simelectricity.essential.client.grid.GridRenderMonitor;

import java.util.LinkedList;


public class ClientProxy extends CommonProxy {
    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public IThreadListener getClientThread() {
        return Minecraft.getMinecraft();
    }

    @Override
    public void preInit() {
        //Initialize client-side API
        SEEAPI.coloredBlocks = new LinkedList<Block>();

        //Initialize coverpanel render
        new VoltageSensorRender();
        new LedPanelRender();
    }

    @Override
    public void init() {
    	SEEAPI.coloredBlocks.add(BlockRegistry.blockCable);
    	
    	ClientRegistrationHandler.registerTileEntityRenders();
    }

    @Override
    public void postInit() {
    	MinecraftForge.EVENT_BUS.register(new GridRenderMonitor());
    }
}
