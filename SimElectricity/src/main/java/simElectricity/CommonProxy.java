package simElectricity;

import simElectricity.Blocks.*;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z){return null;}	
	public void registerTileEntitySpecialRenderer() {}
	public World getClientWorld() {return null;}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		TileEntity te=world.getTileEntity(x, y, z);
		
		if (te instanceof TileQuantumGenerator)
			return new ContainerQuantumGenerator(player.inventory,(TileQuantumGenerator) te);
		if (te instanceof TileVoltageMeter)
			return new ContainerVoltageMeter(player.inventory,(TileVoltageMeter) te);
		if (te instanceof TileElectricFurnace)
			return new ContainerElectricFurnace(player.inventory,(TileElectricFurnace) te);
		if (te instanceof TileSimpleGenerator)
			return new ContainerSimpleGenerator(player.inventory,(TileSimpleGenerator) te);
		
		return null;
	}
}
