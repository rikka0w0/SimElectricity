package SimElectricity;

import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxy extends mod_SimElectricity{
    @Override
    public void registerTileEntitySpecialRenderer(){
    	
    }

    @Override
    public World getClientWorld(){return FMLClientHandler.instance().getClient().theWorld;}
}