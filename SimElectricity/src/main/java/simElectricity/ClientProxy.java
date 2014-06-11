package simElectricity;

import simElectricity.Blocks.*;
import simElectricity.Blocks.Client.GuiAdjustableResistor;
import simElectricity.Blocks.Client.GuiElectricFurnace;
import simElectricity.Blocks.Client.GuiQuantumGenerator;
import simElectricity.Blocks.Client.GuiSimpleGenerator;
import simElectricity.Blocks.Client.GuiVoltageMeter;
import simElectricity.Blocks.Client.RenderWire;
import simElectricity.Blocks.WindMill.RenderWindMillTop;
import simElectricity.Blocks.WindMill.TileWindMillTop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public World getClientWorld() {return FMLClientHandler.instance().getClient().theWorld;}	
	
	@Override
	public void registerTileEntitySpecialRenderer() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileWire.class,new RenderWire());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWindMillTop.class,new RenderWindMillTop());
	}

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		TileEntity te=world.getTileEntity(x, y, z);
		
		if (te instanceof TileQuantumGenerator)
			return new GuiQuantumGenerator(player.inventory,(TileQuantumGenerator) te);
		if (te instanceof TileVoltageMeter)
			return new GuiVoltageMeter(player.inventory,(TileVoltageMeter) te);
		if (te instanceof TileElectricFurnace)
			return new GuiElectricFurnace(player.inventory,(TileElectricFurnace) te);
		if (te instanceof TileSimpleGenerator)
			return new GuiSimpleGenerator(player.inventory,(TileSimpleGenerator) te);
		if (te instanceof TileAdjustableResistor)
			return new GuiAdjustableResistor(player.inventory,(TileAdjustableResistor) te);
		
		return null;
    }
}