package simelectricity.essential.api;

import simelectricity.api.node.ISEGridNode;
import net.minecraft.world.World;

public interface ISEHVCableConnector {
	boolean canHVCableConnect(World world, int x, int y, int z);
	ISEGridNode getGridNode(World world, int x, int y, int z);
}
