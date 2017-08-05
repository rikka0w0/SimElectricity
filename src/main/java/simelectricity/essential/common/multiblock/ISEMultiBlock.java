package simelectricity.essential.common.multiblock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Inspired by Immersive Engineering
 * @author Rikka0_0
 */
public interface ISEMultiBlock {
	public boolean isBlockTrigger(Block b, int meta);
	
	/**
	 * Check structure and create MultiBlock
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @param player
	 * @return true if MultiBlock structure is successfully created
	 */
	public boolean attempCreateStructure(World world, int x, int y, int z, int side, EntityPlayer player);
}
