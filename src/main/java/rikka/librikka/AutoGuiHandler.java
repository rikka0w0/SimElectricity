package rikka.librikka;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import rikka.librikka.container.IContainerWithGui;
import rikka.librikka.tileentity.IGuiProviderTile;

public class AutoGuiHandler implements IGuiHandler{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        
        if (te instanceof IGuiProviderTile)
            return ((IGuiProviderTile) te).getContainer(player, EnumFacing.getFront(ID));
        
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    	Object guiContainer = getServerGuiElement(ID, player, world, x, y, z);
    	if (guiContainer instanceof IContainerWithGui)
    		return ((IContainerWithGui) guiContainer).createGui();
    	
		return null;
	}
}
