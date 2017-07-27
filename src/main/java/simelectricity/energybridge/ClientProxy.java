package simelectricity.energybridge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import simelectricity.essential.cable.render.RenderBlockCable;

public class ClientProxy extends CommonProxy{
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z) {

		return null;
	}
	
	@Override
	public void registerRenders() {
		RenderBlockCable.bakeCableModel(EnergyBridge.bicc);
	}
}
