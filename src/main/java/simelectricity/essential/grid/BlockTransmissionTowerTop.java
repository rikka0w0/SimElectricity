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

import java.util.LinkedList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import simelectricity.api.SEAPI;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;

public class BlockTransmissionTowerTop extends SEBlock implements ITileEntityProvider, ISESubBlock{
	public static final String[] subNames = {"0","1"};
	
	///////////////////
	/// Initialize
	///////////////////
	public BlockTransmissionTowerTop() {
		super("essential_transmission_tower", Material.circuits, ItemBlock.class);
		this.inventoryTexture = new IIcon[subNames.length];
		
		setBlockBounds(0.0F, 0.0F, 0.0F, 1F, 0.05F, 1F);
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
        return new TileTransmissionTower();
    }
	
	public static class ItemBlock extends SEItemBlock{
		public ItemBlock(Block block) {super(block);}
		
		@Deprecated
	    @SideOnly(Side.CLIENT)
	    public IIcon getIconFromDamage(int damage){
	    	return ((BlockTransmissionTowerTop)field_150939_a).inventoryTexture[damage];
	    }
	    
	    @Override
	    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata){
	    	//Place center block
	    	int centerY = y + 18;
	    	metadata = metadata << 3;
	    	int facing = 8 - MathHelper.floor_double((player.rotationYaw) * 8.0F / 360.0F + 0.5D) & 7;
	    	metadata |= facing;
	    	
			for (int[] pos: getCollisionBoxBlockOffsets(metadata)){
				world.setBlock(x + pos[0], centerY + pos[1], z + pos[2], BlockRegistry.transmissionTowerCollisionBox, pos[3], 3);
			}
	    	
	    	boolean ret = super.placeBlockAt(stack, player, world, x, centerY, z, side, hitX, hitY, hitZ, metadata);
	    	
	    	//Place base blocks
	    	for (int[] pos: BlockTransmissionTowerBottom.getBaseBlockCoordOffsets(metadata)){
	    		world.setBlock(x+pos[0], y+pos[1], z+pos[2], BlockRegistry.transmissionTowerBottom, pos[3], 3);
	    	}
	    	
	    	return ret;
	    }
	}
	
    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }
	
	///////////////////////////////////////
	/// Utils
	///////////////////////////////////////
	private static int[] createCollisionBoxCoordOffset(int facing, int x, int y, int z, int part){
		//facing ID
		int[][] collisionBoxCoordOffsetMatrix = new int[][]{
				{0,1,2,3,4,5,6,7,8},	//facing=0
				{0,2,3,4,1,6,7,8,5},	//facing=1
				{0,3,4,1,2,7,8,5,6},	//facing=2
				{0,4,1,2,3,8,5,6,7}		//facing=3
		};
				
		return BlockTransmissionTowerBottom.rotateCoord(facing, x, y, z, collisionBoxCoordOffsetMatrix[facing][part]);
	}
	
	public static LinkedList<int[]> getCollisionBoxBlockOffsets(int meta){
		LinkedList<int[]> list = new LinkedList();

		int facing = meta&7;
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote)
            return; 

        SEAPI.energyNetAgent.attachGridObject(world, x, y, z, (byte)0);
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {    	
    	LinkedList<int[]>  list = getCollisionBoxBlockOffsets(meta);
    	
    	//Attempt to remove any collision box block and bottom(base) block
    	
    	for (int[] part: list){
    		if (world.getBlock(x+part[0], y+part[1], z+part[2]) == BlockRegistry.transmissionTowerCollisionBox)
    		world.setBlockToAir(x+part[0], y+part[1], z+part[2]);
    	}
    	
    	list = BlockTransmissionTowerBottom.getBaseBlockCoordOffsets(meta);
    	for (int[] part: list){
    		if (world.getBlock(x+part[0], y+part[1]-18, z+part[2]) == BlockRegistry.transmissionTowerBottom)
    		world.setBlockToAir(x+part[0], y+part[1]-18, z+part[2]);
    	}
    	
    	SEAPI.energyNetAgent.detachGridObject(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

	////////////////////////////////////
	/// Rendering
	////////////////////////////////////
	public final IIcon[] inventoryTexture;
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
	
	@Override
	public int getRenderBlockPass() {
		return 0;
	}
    
	@Deprecated
	public static int renderID = 0; 	//Definition has changed from 1.8
	@Override
    public int getRenderType()
    {
        return renderID;
    }
	
	@Deprecated
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
    	for (int i=0; i<subNames.length; i++){
    		this.inventoryTexture[i] = r.registerIcon("sime_essential:essential_transmission_tower");
    	}
    }

	@Override
	public int damageDropped(int meta){
		return meta>>3;
	}
    
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player){
	//public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
	    //return new ItemStack(itemBlock, 1, this.getMetaFromState(world.getBlockState(pos)));
		int meta = world.getBlockMetadata(x, y, z)>>3;
		return new ItemStack(itemBlock, 1, meta);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		int meta = world.getBlockMetadata(x, y, z)>>3;
		return new ItemStack(itemBlock, 1, meta);
	}
}
