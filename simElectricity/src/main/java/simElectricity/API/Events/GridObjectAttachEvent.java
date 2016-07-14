package simElectricity.API.Events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event;

public class GridObjectAttachEvent extends Event{
    public int x,y,z;
    public byte type;
    public World world;

    public GridObjectAttachEvent(World world, int x, int y, int z, byte type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.world = world;
    }
}
