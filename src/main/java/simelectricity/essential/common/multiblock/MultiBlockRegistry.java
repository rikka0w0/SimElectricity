package simelectricity.essential.common.multiblock;

import java.util.LinkedList;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


/**
 * Inspired by Immersive Engineering
 * @author Rikka0_0
 */
public class MultiBlockRegistry {
	public static void registerMultiBlocks(){
		new BlockMBTest();
	}
	
	
	
	
	public static final LinkedList<ISEMultiBlock> registeredMultiBlocks = new LinkedList();
	
	public static void registerMultiBlock(ISEMultiBlock multiBlock){
		registeredMultiBlocks.add(multiBlock);
	}
	
	public static boolean onBlockPlaced(World world, int x, int y, int z, int side, EntityPlayer player){
		return false;
	}

	public static void registerTE() {
		GameRegistry.registerTileEntity(TileMBTest.class, "mbtest");
	}
}
