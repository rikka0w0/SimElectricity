package simelectricity.essential.api;

import net.minecraft.world.World;

public interface ISEHVCableConnector extends ISENodeDelegateBlock{
	boolean canHVCableConnect(World world, int x, int y, int z);
}
