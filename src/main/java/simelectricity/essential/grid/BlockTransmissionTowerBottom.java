package simelectricity.essential.grid;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.SEItemBlock;

public class BlockTransmissionTowerBottom extends SEModelBlock implements ISEHVCableConnector, ISESimpleTextureItem {
	///////////////////
	/// Initialize
	///////////////////
	public BlockTransmissionTowerBottom() {
		super("essential_transmission_tower_bottom", Material.GLASS, SEItemBlock.class);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getIconName(int damage) {
		return "essential_transmission_tower_0";	//There's no way to obtain this block, so just return a existing texture
	}
	///////////////////////////////
	///BlockStates
	///////////////////////////////
	@Override
	protected final BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.propertyFacing});
	}
	
	@Override
    public final IBlockState getStateFromMeta(int meta){
        return super.getDefaultState().withProperty(Properties.propertyFacing, meta & 7);
    }
	
	@Override
    public final int getMetaFromState(IBlockState state){
		return state.getValue(Properties.propertyFacing);
    }
	
	
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
	public static Vec3i rotateCoord(int facing, int x, int y, int z){
		//facing coord
		int[][] rotationMatrixCoord = new int[][]{
				{x,y,z},		//facing=0 S
				{z,y,-x},		//facing=1 W
				{-x,y,-z},		//facing=2 N
				{-z,y,x}		//facing=3 E
		};
		
		int[] coord = rotationMatrixCoord[facing];
		return new Vec3i(coord[0], coord[1], coord[2]);
	}
	
	/**
	 * @param metadata facing/meta of the center block
	 * @return offsets from center
	 */
	public static LinkedList<BlockInfo> getBaseBlockCoordOffsets(IBlockState state){
		LinkedList<BlockInfo> list = new LinkedList();
			
		int facing = state.getValue(Properties.propertyFacing);
		if ((facing&1)==0){	// 90 x n
			facing = facing >> 1;
	    	list.add(new BlockInfo(rotateCoord(facing, -2, 0, -2), 	baseCoordOffsetMatrix90[facing][0]));
	    	list.add(new BlockInfo(rotateCoord(facing, -2, 0, 2), 	baseCoordOffsetMatrix90[facing][1]));
	    	list.add(new BlockInfo(rotateCoord(facing, 2, 0, 2), 	baseCoordOffsetMatrix90[facing][2]));
	    	list.add(new BlockInfo(rotateCoord(facing, 2, 0, -2), 	baseCoordOffsetMatrix90[facing][3]));
		}else{
			facing = facing >> 1;
	    	list.add(new BlockInfo(rotateCoord(facing, 0, 0, 3),	baseCoordOffsetMatrix45[facing][0]));
	    	list.add(new BlockInfo(rotateCoord(facing, 3, 0, 0),	baseCoordOffsetMatrix45[facing][1]));
	    	list.add(new BlockInfo(rotateCoord(facing, 0, 0, -3),	baseCoordOffsetMatrix45[facing][2]));
	    	list.add(new BlockInfo(rotateCoord(facing, -3, 0, 0),	baseCoordOffsetMatrix45[facing][3]));
		}
		return list;
	}
	
	public static Vec3i getCenterBoxOffset(int facing) {
		switch (facing){
		case 1:
			return new Vec3i(+2, +18, +2);
		case 3:
			return new Vec3i(+2, +18, -2);
		case 7:
			return new Vec3i(-2, +18, +2);
		case 5:
			return new Vec3i(-2, +18, -2);

			
		case 0:
			return new Vec3i(0, +18, +3);
		case 2:
			return new Vec3i(+3, +18, 0);
		case 4:
			return new Vec3i(0, +18, -3);
		case 6:
			return new Vec3i(-3, +18, 0);
		}
		
		return new Vec3i(0, +18, 0);
	}
	
	/**
	 * @param x	coord of the base block
	 * @param y
	 * @param z
	 * @param meta
	 * @return coord of the center block
	 */
	public static BlockPos getCenterBoxCoord(BlockPos basePos, IBlockState state){
		int facing = state.getValue(Properties.propertyFacing);
		Vec3i offset = getCenterBoxOffset(facing);
		return basePos.add(offset); 
	}


	//////////////////////////////////////
	/////Item drops and Block activities
	//////////////////////////////////////
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		BlockPos centerPos = getCenterBoxCoord(pos, state);
		IBlockState centerState = world.getBlockState(centerPos);
    	
    	if (centerState.getBlock() == BlockRegistry.transmissionTowerTop)
    		return BlockRegistry.transmissionTowerTop.getPickBlock(centerState, null, world, centerPos, player);
    	
    	return null;
	}
	
	@Override
	public int damageDropped(IBlockState state){
		return 0;	//Prevent crash QAQ!!!
	}
	
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {    
		BlockPos centerPos = getCenterBoxCoord(pos, state);
    	Block centerBlock = world.getBlockState(centerPos).getBlock();
    	
    	if (centerBlock == BlockRegistry.transmissionTowerTop)
    		world.setBlockToAir(centerPos);
    	
    	super.breakBlock(world, pos, state);
    }
    
	//////////////////////////////////////
	/// ISEHVCableConnector
	//////////////////////////////////////
	@Override
	public boolean canHVCableConnect(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		BlockPos centerPos = getCenterBoxCoord(pos, state);
		TileEntity te = world.getTileEntity(centerPos);
		if (te instanceof TileTransmissionTower)
			return ((TileTransmissionTower) te).canConnect();
		else
			return false;
	}

	@Override
	public ISEGridNode getNode(World world, BlockPos pos){
		IBlockState state = world.getBlockState(pos);
		BlockPos centerPos = getCenterBoxCoord(pos, state);
		TileEntity te = world.getTileEntity(centerPos);
		if (te instanceof ISEGridTile)
			return ((ISEGridTile) te).getGridNode();
		
		return null;
	}
}
