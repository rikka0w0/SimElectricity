package simelectricity.essential.grid;

import java.util.LinkedList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import simelectricity.essential.BlockRegistry;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;

public class BlockTransmissionTowerBottom extends SEBlock{
	///////////////////
	/// Utils
	///////////////////	
	private static 	int[][] baseCoordOffsetMatrix90 = new int[][]{
			{1,3,5,7},	//facing first
			{3,5,7,1},
			{5,7,1,3},
			{7,1,3,5}
	};
	
	private static int[][] baseCoordOffsetMatrix45 = new int[][]{
			{4,6,0,2},
			{6,0,2,4},
			{0,2,4,6},
			{2,4,6,0}
	};
	/**
	 * Internal, Do not use, Rotate around Y
	 * @param facing minecraft facing, S0, E3
	 * @return
	 */
	public static int[] rotateCoord(int facing, int x, int y, int z, int part){
		//facing coord
		int[][] rotationMatrixCoord = new int[][]{
				{x,y,z, part},		//facing=0 S
				{z,y,-x, part},		//facing=1 W
				{-x,y,-z, part},	//facing=2 N
				{-z,y,x, part}		//facing=3 E
		};
		return rotationMatrixCoord[facing]; 
	}
	
	/**
	 * @param metadata facing/meta of the center block
	 * @return offsets from center
	 */
	public static LinkedList<int[]> getBaseBlockCoordOffsets(int meta){
		LinkedList<int[]> list = new LinkedList();
			
		int facing = meta&7;
		if ((facing&1)==0){	// 90 x n
			facing = facing >> 1;
	    	list.add(rotateCoord(facing, -2, 0, -2, baseCoordOffsetMatrix90[facing][0]));
	    	list.add(rotateCoord(facing, -2, 0, 2, baseCoordOffsetMatrix90[facing][1]));
	    	list.add(rotateCoord(facing, 2, 0, 2, baseCoordOffsetMatrix90[facing][2]));
	    	list.add(rotateCoord(facing, 2, 0, -2, baseCoordOffsetMatrix90[facing][3]));
		}else{
			facing = facing >> 1;
	    	list.add(rotateCoord(facing, 0, 0, 3, baseCoordOffsetMatrix45[facing][0]));
	    	list.add(rotateCoord(facing, 3, 0, 0, baseCoordOffsetMatrix45[facing][1]));
	    	list.add(rotateCoord(facing, 0, 0, -3, baseCoordOffsetMatrix45[facing][2]));
	    	list.add(rotateCoord(facing, -3, 0, 0, baseCoordOffsetMatrix45[facing][3]));
		}
		return list;
	}
	
	/**
	 * @param x	coord of the base block
	 * @param y
	 * @param z
	 * @param meta
	 * @return coord of the center block (y = y of base block)
	 */
	public static int[] getCenterBoxCoord(int x, int y, int z, int meta){
		switch (meta){
		case 1:
			return new int[]{x+2, y, z+2};
		case 3:
			return new int[]{x+2, y, z-2};
		case 7:
			return new int[]{x-2, y, z+2};
		case 5:
			return new int[]{x-2, y, z-2};

			
		case 0:
			return new int[]{x, y, z+3};
		case 2:
			return new int[]{x+3, y, z};
		case 4:
			return new int[]{x, y, z-3};
		case 6:
			return new int[]{x-3, y, z};
		}
		return new int[]{x, y, z};
	}
	
	///////////////////
	/// Initialize
	///////////////////
	public BlockTransmissionTowerBottom() {
		super("essential_transmission_tower_bottom", Material.circuits, SEItemBlock.class);
	}

	////////////////////////////////////
	/// Rendering
	////////////////////////////////////
    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isNormalCube() {
		return false;
	}
    
	@Deprecated
	public static int renderID = 0; 	//Definition has changed from 1.8
	@Override
    public int getRenderType()
    {
        return renderID;
    }
	
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player){
		return this.getPickBlock(target, world, x, y, z);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
    	int meta = world.getBlockMetadata(x, y, z);
		int[] coord = getCenterBoxCoord(x, y, z, meta);
    	coord[1] += 18;
    	Block centerBlock = world.getBlock(coord[0], coord[1], coord[2]);
    	
    	if (centerBlock == BlockRegistry.transmissionTowerTop)
    		return BlockRegistry.transmissionTowerTop.getPickBlock(target, world, coord[0], coord[1], coord[2]);
    	
    	return null;
	}
	
	@Override
	public int damageDropped(int meta){
		return 0;
	}
	
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {    
    	int[] coord = getCenterBoxCoord(x, y, z, meta);
    	coord[1] += 18;
    	Block centerBlock = world.getBlock(coord[0], coord[1], coord[2]);
    	
    	if (centerBlock == BlockRegistry.transmissionTowerTop)
    		world.setBlockToAir(coord[0], coord[1], coord[2]);
    }
    
    @Override
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_){
        return true;
    }
}
