package simelectricity.essential;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import simelectricity.essential.common.ISEGuiProvider;

public class CommonProxy implements IGuiHandler {
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

    @Override
    public final Object getServerGuiElement(int ID, EntityPlayer player, World world,
                                            int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        if (te instanceof ISEGuiProvider)
            return ((ISEGuiProvider) te).getServerContainer(EnumFacing.getFront(ID));

        return BlockRegistry.getContainer(te, player);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {
        return null;
    }
}
