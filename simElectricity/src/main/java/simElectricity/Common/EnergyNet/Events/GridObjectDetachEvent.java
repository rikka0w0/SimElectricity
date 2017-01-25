package simElectricity.Common.EnergyNet.Events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event;

public class GridObjectDetachEvent extends Event{
    public int x,y,z;
    public byte type;
    public World world;

    public GridObjectDetachEvent(World world, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }
}