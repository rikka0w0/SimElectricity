package simelectricity.essential;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;

public class CommonProxy  {
    public EntityPlayer getClientPlayer() {
        return null;
    }

    public World getClientWorld() {
        return null;
    }

    public IThreadListener getClientThread() {
        return null;
    }

    public void preInit() {
    }

    public void init() {
    }

    public void postInit() {
    }
}
