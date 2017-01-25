package simElectricity.Common.EnergyNet.Events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event;

public class GridConnectionEvent extends Event{
	public int x1,y1,z1,x2,y2,z2;
	public World world;
	public double resistance;


	public GridConnectionEvent(World world, int x1, int y1, int z1, int x2, int y2, int z2, double resistance){
	    this.x1 = x1;
	    this.y1 = y1;
	    this.z1 = z1;
	    this.x2 = x2;
	    this.y2 = y2;
	    this.z2 = z2;
	    this.world = world;
	    this.resistance = resistance;
	}
}
