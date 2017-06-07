package simelectricity.essential;

import simelectricity.essential.cable.TileCable;
import simelectricity.essential.cable.render.RenderBlockCable;
import simelectricity.essential.cable.render.RenderTileCable;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy{	
	@Override
	public World getClientWorld(){
		return Minecraft.getMinecraft().theWorld;
	}
	
	@Override
	public Object getClientThread() {
		return Minecraft.getMinecraft();
	}
	
	@Override
	public void registerRenders() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileCable.class, new RenderTileCable());
		
		new RenderBlockCable();
	}
}
