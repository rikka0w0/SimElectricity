package simElectricity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import simElectricity.Test.GuiBatteryBox;
import simElectricity.Test.TileBatteryBox;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxy_Test extends mod_SimElectricity_Test {
	@Override
	public World getClientWorld() {return FMLClientHandler.instance().getClient().theWorld;}	
	
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		TileEntity te=world.getTileEntity(x, y, z);
		
		if (te instanceof TileBatteryBox)
			return new GuiBatteryBox(player.inventory,(TileBatteryBox) te);
				
		return null;	
    }
}