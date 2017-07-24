package simelectricity.essential.grid;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.utils.Utils;

public class BlockTransmissionTower2 extends SEBlock implements ITileEntityProvider, ISESubBlock, ISEHVCableConnector{
	public static final String[] subNames = {"0" , "1"};
	public final IIcon[] inventoryTexture;
	
	public BlockTransmissionTower2() {
		super("essential_transmission_tower2", Material.glass, ItemBlock.class);
		this.inventoryTexture = new IIcon[subNames.length];
	}

	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return subNames;
	}
	
	public static class ItemBlock extends SEItemBlock{
		public ItemBlock(Block block) {super(block);}
		
		@Override
		@Deprecated
	    @SideOnly(Side.CLIENT)
	    public IIcon getIconFromDamage(int damage){
	    	return ((BlockTransmissionTower2)field_150939_a).inventoryTexture[damage];
	    }
		
	    @Override
	    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata){
	    	metadata = metadata << 3;
	    	int facing = 4 - MathHelper.floor_double((player.rotationYaw) * 4.0F / 360.0F + 0.5D) & 3;
	    	metadata |= facing;
	    	
	    	for (int[] pos: getRodBlockOffsets(metadata))
	    		world.setBlock(x + pos[0], y + pos[1], z + pos[2], field_150939_a, pos[3], 3);
	    	
			int cbtype = (facing == 0 || facing == 2) ? 9 : 10;
	    	for (int[] posXZOffset: getCollisionBoxBlockXZOffsets(metadata))
	    		world.setBlock(x + posXZOffset[0], y + 11, z + posXZOffset[1], BlockRegistry.transmissionTowerCollisionBox, cbtype, 3);
	    	
	    	//metedata [type][isRod][rotation]
	    	return super.placeBlockAt(stack, player, world, x, y + 11, z, side, hitX, hitY, hitZ, metadata);
	    }
	}
	
	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}
	
    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }
    
	@Override
	public int damageDropped(int meta){
		return meta>>3;
	}
    
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player){
		return this.getPickBlock(target, world, x, y, z);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		TileEntity te = getCenterTileFromRodPos(world, x, y, z);
		if (te instanceof TileTransmissionTower2){
			int meta = world.getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
			return new ItemStack(itemBlock, 1, typeFromMeta(meta) ? 1 : 0);
		}
		return null;
	}
    
	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubBlocks(Item itemIn, CreativeTabs tab, List subItems){
		subItems.add(new ItemStack(this, 1, 0));
		subItems.add(new ItemStack(this, 1, 1));
    }
	
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote)
            return; 

        SEAPI.energyNetAgent.attachGridObject(world, SEAPI.energyNetAgent.newGridNode(x, y, z, (byte)0));
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		int facing = facingFromMeta(meta);
		boolean isRod = isRod(meta);
		
		if (isRod){
			int xc, yc, zc;
			xc = x + rodBlockOffsetMatrix[facing][0];
			zc = z + rodBlockOffsetMatrix[facing][1];
			if (typeFromMeta(meta)){
				yc = y + 11;
			}else{
				yc = getBottomRodY(world, x, y, z) + 11;
			}

			Block blockCenter = world.getBlock(xc, yc, zc);
			int metaCenter = world.getBlockMetadata(xc, yc, zc);
			
			if (blockCenter == this && !isRod(metaCenter))
				world.setBlockToAir(xc, yc, zc);
		}else{
			//Center Block
	    	TileEntity te = world.getTileEntity(x, y, z);	//Do this before the tileEntity is removed!
	    	if (te instanceof ISEGridTile)
	    		SEAPI.energyNetAgent.detachGridObject(world, ((ISEGridTile) te).getGridNode());
	    	
	    	for (int[] pos: getRodBlockOffsets(meta)){
	    		Block rodBlock = world.getBlock(x+pos[0], y+pos[1]-11, z+pos[2]);
	    		int rodBlockMeta = world.getBlockMetadata(x+pos[0], y+pos[1]-11, z+pos[2]);
	    		
	    		if (rodBlock == this && isRod(rodBlockMeta))
	    			world.setBlockToAir(x+pos[0], y+pos[1]-11, z+pos[2]);
	    	}
	    	
	    	for (int[] posXZOffset: getCollisionBoxBlockXZOffsets(meta)){
	    		Block cbBlock = world.getBlock(x + posXZOffset[0], y, z + posXZOffset[1]);
	    		if (cbBlock == BlockRegistry.transmissionTowerCollisionBox)
	    			world.setBlockToAir(x + posXZOffset[0], y, z + posXZOffset[1]);
	    	}
		}

    	
    	super.breakBlock(world, x, y, z, block, meta);
    }

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		if (!isRod(meta))
			return new TileTransmissionTower2();
		return null;
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z,
			AxisAlignedBB axisAlignedBB, List collidingBoxes, Entity par7Entity){

		int meta = world.getBlockMetadata(x, y, z);
		int facing = facingFromMeta(meta);

		if (isRod(meta)){
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
			
			Block blockCenter = world.getBlock(x + rodBlockOffsetMatrix[facing][0], y, z + rodBlockOffsetMatrix[facing][1]);
			
			if (blockCenter == this){
				int metaCenter = world.getBlockMetadata(x + rodBlockOffsetMatrix[facing][0], y, z + rodBlockOffsetMatrix[facing][1]);
				
				if (!isRod(metaCenter)){
					if (facing == 0 || facing == 2)
						Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0.125F, 1, 0.25F, 0.875F);
					else
						Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0.125F, 0, 0, 0.875F, 0.25F, 1);
				}
			}
		}else{
			if (facing == 0 || facing == 2){
				Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0.125F, 1, 0.25F, 0.875F);
			}else{
				Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0.125F, 0, 0, 0.875F, 0.25F, 1);
			}
		}
	}
	
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		int facing = facingFromMeta(meta);
		
		if (isRod(meta)){
			Block blockCenter = world.getBlock(x + rodBlockOffsetMatrix[facing][0], y, z + rodBlockOffsetMatrix[facing][1]);
			if (blockCenter == this){
				int metaCenter = world.getBlockMetadata(x + rodBlockOffsetMatrix[facing][0], y, z + rodBlockOffsetMatrix[facing][1]);
				if ((metaCenter&4) == 0)
					if (facing == 0 || facing == 2){
						setBlockBounds(0.125F, 0, 0, 0.875F, 1, 1);
					}else{
						setBlockBounds(0, 0, 0.125F, 1, 1, 0.875F);	
					}
				else
					setBlockBounds(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
			}else{
				setBlockBounds(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
			}
		}else{
			if (facing == 0 || facing == 2){
				setBlockBounds(0, 0, 0.125F, 1, 0.25F, 0.875F);
			}else{
				setBlockBounds(0.125F, 0, 0, 0.875F, 0.25F, 1);
			}
		}
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
	
	@Deprecated
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
    	for (int i=0; i<subNames.length; i++){
    		this.inventoryTexture[i] = r.registerIcon("sime_essential:transmission/essential_transmission_tower2");
    	}
    }
	///////////////////
	/// Utils
	///////////////////	
	private static int[][] rodBlockOffsetMatrix = new int[][]{
		{0, 3},	//{x,z}
		{3, 0},
		{0, -3},
		{-3, 0}
	};
	
	private static LinkedList<int[]> getRodBlockOffsets(int meta){
		LinkedList<int[]> list = new LinkedList();
		
		int facing = facingFromMeta(meta);
		int facing1 = (facing - 1)&3;
		int facing2 = (facing + 1)&3;
		
		list.add(new int[]{rodBlockOffsetMatrix[facing1][0], 0, rodBlockOffsetMatrix[facing1][1], facing2 | 12});
		list.add(new int[]{rodBlockOffsetMatrix[facing2][0], 0, rodBlockOffsetMatrix[facing2][1], facing1 | 12});
		
		for (int i=1; i<15; i++){
			list.add(new int[]{rodBlockOffsetMatrix[facing1][0], i, rodBlockOffsetMatrix[facing1][1], facing2 | 4});
			list.add(new int[]{rodBlockOffsetMatrix[facing2][0], i, rodBlockOffsetMatrix[facing2][1], facing1 | 4});
		}
		
		return list;
	}
	
	private int getBottomRodY(IBlockAccess world, int x, int y, int z){
		Block block;
		int meta;
		
		for (int count=0; count<20; count++){
			 block = world.getBlock(x, y, z);
			 meta = world.getBlockMetadata(x, y, z);
			 
			 if (block == this && isRod(meta) && (meta & 8) > 0)
				 return y;
			 
			 y--;
		}
		
		return -1;
	}
	
	public static int facingFromMeta(int meta){
		return meta&3;
	}
	
	public static boolean isRod(int meta){
		return (meta & 4)>0;
	}
	
	public static boolean typeFromMeta(int meta){
		return (meta & 8) > 0;
	}
	
	private static int[][] cbOffsetMatrix = new int[][]{
		{0, 1},	//{x,z}
		{1, 0},
		{0, -1},
		{-1, 0}
	};
	
	private static LinkedList<int[]> getCollisionBoxBlockXZOffsets(int meta){
		LinkedList<int[]> list = new LinkedList();
		
		int facing = facingFromMeta(meta);
		
		int facing1 = (facing - 1)&3;
		int xoffset = cbOffsetMatrix[facing1][0];
		int zoffset = cbOffsetMatrix[facing1][1];
		list.add(new int[]{xoffset,zoffset});
		list.add(new int[]{xoffset*2,zoffset});
		list.add(new int[]{xoffset*4,zoffset});
		list.add(new int[]{xoffset*5,zoffset});
		
		list.add(new int[]{xoffset*-1,zoffset});
		list.add(new int[]{xoffset*-2,zoffset});
		list.add(new int[]{xoffset*-4,zoffset});
		list.add(new int[]{xoffset*-5,zoffset});
		
		return list;
	}

	private TileEntity getCenterTileFromRodPos(World world, int x, int y, int z){
		int meta = world.getBlockMetadata(x, y, z);
		int facing = facingFromMeta(meta);
		boolean isRod = isRod(meta);
		
		if (isRod){
			int xc, yc, zc;
			xc = x + rodBlockOffsetMatrix[facing][0];
			zc = z + rodBlockOffsetMatrix[facing][1];
			if (typeFromMeta(meta)){
				yc = y + 11;
			}else{
				yc = getBottomRodY(world, x, y, z) + 11;
			}

			Block blockCenter = world.getBlock(xc, yc, zc);
			int metaCenter = world.getBlockMetadata(xc, yc, zc);
			
			if (blockCenter == this && !isRod(metaCenter))
				return world.getTileEntity(xc, yc, zc);
			
			return null;
		}
		
		return world.getTileEntity(x, y, z);
	}
	
	@Override
	public boolean canHVCableConnect(World world, int x, int y, int z) {
		TileEntity te = getCenterTileFromRodPos(world, x, y, z);
		
		if (te instanceof TileTransmissionTower2)
			return ((TileTransmissionTower2) te).canConnect();
		else
			return false;
	}

	@Override
	public ISEGridNode getGridNode(World world, int x, int y, int z) {
		TileEntity te = getCenterTileFromRodPos(world, x, y, z);
		
		if (te instanceof ISEGridTile)
			return ((ISEGridTile) te).getGridNode();
		
		return null;
	}
}
