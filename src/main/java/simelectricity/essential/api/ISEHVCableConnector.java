package simelectricity.essential.api;

import net.minecraft.world.World;

public interface ISEHVCableConnector {
	boolean canHVCableConnect(World world, int x, int y, int z);
	int[] getGridNodeCoord(World world, int x, int y, int z);
}
