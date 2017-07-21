package simelectricity.essential;

import simelectricity.essential.common.ISEGuiProvider;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
		
		if (te instanceof ISEGuiProvider)
			return ((ISEGuiProvider) te).getServerContainer(ForgeDirection.getOrientation(ID));
		
		return BlockRegistry.getContainer(te, player);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}
}
