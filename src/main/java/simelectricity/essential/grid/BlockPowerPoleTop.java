/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simelectricity.essential.grid;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.common.UnlistedNonNullProperty;

public class BlockPowerPoleTop extends SEModelBlock implements ITileEntityProvider, ISESubBlock{
	public static final String[] subNames = {"0","1"};
	
	///////////////////
	/// Initialize
	///////////////////
	public BlockPowerPoleTop() {
		super("essential_powerpole", Material.GLASS, ItemBlock.class);
	}

	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return subNames;
	}

	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TilePowerPole();
    }
	
	public static class ItemBlock extends SEItemBlock implements ISESimpleTextureItem{
		public ItemBlock(Block block) {super(block);}
	    
		@Override
		@SideOnly(Side.CLIENT)
		public String getIconName(int damage) {
			return "essential_powerpole_" + damage;
		}
		
	    @Override
	    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState){
	    	//Place center block
	    	BlockPos centerPos = pos.add(0, 18, 0);  	
	    	
			for (BlockInfo info: getCollisionBoxBlockOffsets(newState)){
				BlockPos realPos = info.getRealPos(centerPos);
				world.setBlockState(realPos, BlockRegistry.powerPoleCollisionBox.getStateFromMeta(info.part));
			}
	    	
	    	boolean ret = super.placeBlockAt(stack, player, world, centerPos, side, hitX, hitY, hitZ, newState);
	    	
	    	//Place base blocks
	    	for (BlockInfo info: BlockPowerPoleBottom.getBaseBlockCoordOffsets(newState)){
	    		BlockPos realPos = info.getRealPos(pos);
	    		world.setBlockState(realPos, BlockRegistry.powerPoleBottom.getStateFromMeta(info.part));
	    	}
	    	
	    	return ret;
	    }
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.0, 0.0, 0.0, 1, 0.05, 1);
	}
	///////////////////////////////
	///BlockStates
	///////////////////////////////
	@Override
	protected final BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, 
				new IProperty[] {Properties.propertyType, Properties.propertyFacing},
				new IUnlistedProperty[] {UnlistedNonNullProperty.propertyGridTile});
	}
	
	@Override
    public final IBlockState getStateFromMeta(int meta){
		meta &= 15;
        return super.getDefaultState().withProperty(Properties.propertyType, meta>>3).withProperty(Properties.propertyFacing, meta & 7);
    }
	
	@Override
    public final int getMetaFromState(IBlockState state){
		int type = state.getValue(Properties.propertyType);
		int facing = state.getValue(Properties.propertyFacing);
		int meta = (type<<3) | facing;
		return meta;
    }
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState retval = (IExtendedBlockState)state;
			
			TileEntity te = world.getTileEntity(pos);
			
			if (te instanceof ISEGridTile) {
				retval = retval.withProperty(UnlistedNonNullProperty.propertyGridTile, new WeakReference<>((ISEGridTile) te));
			}
			
			return retval;
		}
		return state;
	}
	
	///////////////////////////////////////
	/// Utils
	///////////////////////////////////////
	//facing ID
	private static int[][] collisionBoxCoordOffsetMatrix = new int[][]{
			{0,1,2,3,4,5,6,7,8},	//facing=0
			{0,2,3,4,1,6,7,8,5},	//facing=1
			{0,3,4,1,2,7,8,5,6},	//facing=2
			{0,4,1,2,3,8,5,6,7}		//facing=3
	};
	
	private static BlockInfo createCollisionBoxCoordOffset(int facing, int x, int y, int z, int part) {
		return new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, x, y, z), collisionBoxCoordOffsetMatrix[facing][part]);
	}
	
	public static LinkedList<BlockInfo> getCollisionBoxBlockOffsets(IBlockState state){
		LinkedList<BlockInfo> list = new LinkedList();

		int facing = state.getValue(Properties.propertyFacing);
		if ((facing&1)==0){	// 90 x n
			facing = facing >> 1;
			
			list.add(createCollisionBoxCoordOffset(facing,1,0,0,0));
			list.add(createCollisionBoxCoordOffset(facing,2,0,0,0));
			list.add(createCollisionBoxCoordOffset(facing,3,0,0,0));
			list.add(createCollisionBoxCoordOffset(facing,4,0,0,0));
			list.add(createCollisionBoxCoordOffset(facing,-1,0,0,0));
			list.add(createCollisionBoxCoordOffset(facing,-2,0,0,0));
			list.add(createCollisionBoxCoordOffset(facing,-3,0,0,0));
			list.add(createCollisionBoxCoordOffset(facing,-4,0,0,0));
			
			list.add(createCollisionBoxCoordOffset(facing,0,0,1,3));
			list.add(createCollisionBoxCoordOffset(facing,1,0,1,3));
			list.add(createCollisionBoxCoordOffset(facing,2,0,1,3));
			list.add(createCollisionBoxCoordOffset(facing,3,0,1,3));
			list.add(createCollisionBoxCoordOffset(facing,4,0,1,3));
			list.add(createCollisionBoxCoordOffset(facing,-1,0,1,3));
			list.add(createCollisionBoxCoordOffset(facing,-2,0,1,3));
			list.add(createCollisionBoxCoordOffset(facing,-3,0,1,3));
			list.add(createCollisionBoxCoordOffset(facing,-4,0,1,3));
			
			list.add(createCollisionBoxCoordOffset(facing,0,0,-1,1));
			list.add(createCollisionBoxCoordOffset(facing,1,0,-1,1));
			list.add(createCollisionBoxCoordOffset(facing,2,0,-1,1));
			list.add(createCollisionBoxCoordOffset(facing,3,0,-1,1));
			list.add(createCollisionBoxCoordOffset(facing,4,0,-1,1));
			list.add(createCollisionBoxCoordOffset(facing,-1,0,-1,1));
			list.add(createCollisionBoxCoordOffset(facing,-2,0,-1,1));
			list.add(createCollisionBoxCoordOffset(facing,-3,0,-1,1));
			list.add(createCollisionBoxCoordOffset(facing,-4,0,-1,1));		
			
			list.add(createCollisionBoxCoordOffset(facing,5,0,1,8));
			list.add(createCollisionBoxCoordOffset(facing,5,0,-1,5));
			list.add(createCollisionBoxCoordOffset(facing,-5,0,-1,6));
			list.add(createCollisionBoxCoordOffset(facing,-5,0,1,7));
		}else{	//45 x n
			facing = facing >> 1;

			list.add(createCollisionBoxCoordOffset(facing, 4,0,-3,0));
			list.add(createCollisionBoxCoordOffset(facing, 3,0,-2,0));
			list.add(createCollisionBoxCoordOffset(facing, 2,0,-1,0));
			list.add(createCollisionBoxCoordOffset(facing, 1,0,0,0));
			list.add(createCollisionBoxCoordOffset(facing, 0,0,1,0));
			list.add(createCollisionBoxCoordOffset(facing, -1,0,2,0));
			list.add(createCollisionBoxCoordOffset(facing, -2,0,3,0));
			list.add(createCollisionBoxCoordOffset(facing, -3,0,4,0));
			
			
			list.add(createCollisionBoxCoordOffset(facing, 3,0,-4,0));
			list.add(createCollisionBoxCoordOffset(facing, 2,0,-3,0));
			list.add(createCollisionBoxCoordOffset(facing, 1,0,-2,0));
			list.add(createCollisionBoxCoordOffset(facing, 0,0,-1,0));
			list.add(createCollisionBoxCoordOffset(facing, -1,0,0,0));
			list.add(createCollisionBoxCoordOffset(facing, -2,0,1,0));
			list.add(createCollisionBoxCoordOffset(facing, -3,0,2,0));
			list.add(createCollisionBoxCoordOffset(facing, -4,0,3,0));
			
			list.add(createCollisionBoxCoordOffset(facing, 3,0,-3,0));
			list.add(createCollisionBoxCoordOffset(facing, 2,0,-2,0));
			list.add(createCollisionBoxCoordOffset(facing, 1,0,-1,0));
			list.add(createCollisionBoxCoordOffset(facing, -1,0,1,0));
			list.add(createCollisionBoxCoordOffset(facing, -2,0,2,0));
			list.add(createCollisionBoxCoordOffset(facing, -3,0,3,0));
		}
		
		return list;
	}

	//////////////////////////////////////
	/////Item drops and Block activities
	//////////////////////////////////////
	@Override
	public int damageDropped(IBlockState state){
		return state.getValue(Properties.propertyType);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int damage, EntityLivingBase placer) {
		IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, damage, placer);
		int facingInt = 8 - MathHelper.floor((placer.rotationYaw) * 8.0F / 360.0F + 0.5D) & 7;
    	int type = damage;
    	return state.withProperty(Properties.propertyType, type).withProperty(Properties.propertyFacing, facingInt);
	}
	
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote)
            return; 
        
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISEGridTile)
        	SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, 3));     
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {    	
    	LinkedList<BlockInfo> list = getCollisionBoxBlockOffsets(state);
    	
    	//Attempt to remove any collision box block and bottom(base) block
    	
    	for (BlockInfo info: list){
    		BlockPos realPos = info.getRealPos(pos);
    		if (world.getBlockState(realPos).getBlock() == BlockRegistry.powerPoleCollisionBox)
    			world.setBlockToAir(realPos);
    	}
    	
    	list = BlockPowerPoleBottom.getBaseBlockCoordOffsets(state);
    	for (BlockInfo info: list){
    		BlockPos realPos = info.getRealPos(pos).add(0, -18, 0);
    		if (world.getBlockState(realPos).getBlock() == BlockRegistry.powerPoleBottom)
    			world.setBlockToAir(realPos);
    	}
    	
    	TileEntity te = world.getTileEntity(pos);	//Do this before the tileEntity is removed!
    	if (te instanceof ISEGridTile)
    		SEAPI.energyNetAgent.detachGridNode(world, ((ISEGridTile) te).getGridNode());
    	
    	super.breakBlock(world, pos, state);
    }
}
