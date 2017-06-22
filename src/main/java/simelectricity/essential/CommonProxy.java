package simelectricity.essential;

import simelectricity.essential.machines.gui.ContainerVoltageMeter;
import simelectricity.essential.machines.tile.TileVoltageMeter;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler{	
	public EntityPlayer getClientPlayer() {
		return null;
	}
	
	public World getClientWorld() {
		return null;
	}

	public Object getClientThread() {
		return null;
	}
	
	public void registerRenders() {

	}

	@Override
	public final Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		
		if (te instanceof TileVoltageMeter)
			return new ContainerVoltageMeter(te);
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}
}
