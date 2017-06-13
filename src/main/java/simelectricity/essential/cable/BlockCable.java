package simelectricity.essential.cable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import simelectricity.api.SEAPI;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.cable.render.RenderBlockCable;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.utils.MatrixTranformations;
import simelectricity.essential.utils.SERenderHelper;
import simelectricity.essential.utils.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * The collision & ray trace is inspired by BuildCraft
 * @author Rikka0_0
 */
public class BlockCable extends SEBlock implements ITileEntityProvider, ISESubBlock{
	///////////////////////////////
	/// Cable Properties
	///////////////////////////////
	public final String[] subNames;
	public final double[] thickness;
	public final double[] resistances;
	
	private final Class<? extends TileCable> tileEntityClass;
	
	///////////////////////////////
	///Block Properties
	///////////////////////////////
	public BlockCable() {
		this("essential_cable", Material.circuits, ItemBlock.class, 
				new String[]{"copper_thin", "copper_medium", "copper_thick"},
				new double[]{0.22, 0.32, 0.42},
				new double[]{0.1, 0.01, 0.001},
				TileCable.class);
		setHardness(0.2F);		
	}
	
	protected BlockCable(String name, Material material, Class<? extends ItemBlock> itemBlockClass,
							String[] cableTypes, double[] thicknessList, double[] resistanceList, Class<? extends TileCable> tileEntityClass) {
		super(name, material, itemBlockClass);
		this.subNames = cableTypes;
		this.thickness = thicknessList;
		this.resistances = resistanceList;
		this.tileEntityClass = tileEntityClass;
		
		// TODO make this client only if possible
		this.insulatorTexture = new IIcon[subNames.length]; 
		this.copperTexture = new IIcon[subNames.length]; 
		this.inventoryTexture = new IIcon[subNames.length];
	}
	
	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileCable cable;
		try {
			cable = this.tileEntityClass.getConstructor().newInstance();
			if (!world.isRemote)	//createNewTileEntity is only called by server when the block is firstly placed
				cable.setResistanceOnPlace(resistances[meta]);
			return cable;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return subNames;
	}

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }
    
	////////////////////////////////////
	/// Rendering
	////////////////////////////////////
    @SideOnly(Side.CLIENT)
    public double[][][][] cableBranchModels;    
	//Replaced by json in 1.8 and above
	@Deprecated
	public final IIcon[] insulatorTexture; 
	@Deprecated
	public final IIcon[] copperTexture; 
	@Deprecated
	public final IIcon[] inventoryTexture;
	@Deprecated
	public static class ItemBlock extends SEItemBlock{	
		public ItemBlock(Block block) {super(block);}
		
	    @SideOnly(Side.CLIENT)
	    public IIcon getIconFromDamage(int damage){
	    	return ((BlockCable)field_150939_a).inventoryTexture[damage];
	    }
	}
	@Deprecated
	public static int renderID = 0; 	//Definition has changed from 1.8
	@Override
    public int getRenderType()
    {
        return renderID;
    }
    
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isNormalCube() {
		return false;
	}
	
	@Override
	public boolean canRenderInPass(int pass) {
		RenderBlockCable.renderPass = pass;
		return true;
	}
	
	@Override
	public int getRenderBlockPass() {
		return 1;
	}
	
	@Deprecated	//Removed in 1.8 and above
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
		for (int i = 0; i<subNames.length; i++){
			insulatorTexture[i] = iconRegister.registerIcon("sime_essential:cable/" + registryName + "_" + subNames[i] + "_insulator");
			copperTexture[i] = iconRegister.registerIcon("sime_essential:cable/" + registryName + "_" + subNames[i] + "_copper");
			inventoryTexture[i] = iconRegister.registerIcon("sime_essential:cable/" + registryName + "_" + subNames[i] + "_inventory");
		}
    }
	
	@Deprecated	//Removed in 1.8 and above
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
		if (side == 8)
			return insulatorTexture[meta];
		if (side == 9)
			return copperTexture[meta];
		
		return inventoryTexture[meta];
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side){
		return false;
    }
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		
		return tile instanceof ISEGenericCable 
				? 
				((ISEGenericCable)tile).getCoverPanelOnSide(side) != null 
				:
				false;
	}
	
	//////////////////////////////////
	///CollisionBoxes
	//////////////////////////////////
	//Custom ray trace
	private static void addCollisionBoxToList(int x, int y, int z, AxisAlignedBB addCollisionBoxToList, List collidingBoxes,
			double minX, double minY, double minZ, double maxX, double maxY, double maxZ){
		AxisAlignedBB axisalignedbb1 = AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
	
        if (axisalignedbb1 != null && addCollisionBoxToList.intersectsWith(axisalignedbb1))
        	collidingBoxes.add(axisalignedbb1);
	}
	
	private static AxisAlignedBB getCoverPanelBoundingBox(ForgeDirection side) {
		if (side == ForgeDirection.UNKNOWN)
			return null;
		
		float[][] bounds = new float[3][2];
		// X START - END
		bounds[0][0] = 0;
		bounds[0][1] = 1;
		// Y START - END
		bounds[1][0] = 0;
		bounds[1][1] = (float) CoverPanel.thickness;
		// Z START - END
		bounds[2][0] = 0;
		bounds[2][1] = 1;

		MatrixTranformations.transform(bounds, side);
		return AxisAlignedBB.getBoundingBox(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]);
	}
	
	private static AxisAlignedBB getCableBoundingBox(ForgeDirection side, float thickness) {
		float min = 0.5F - thickness/2;
		float max = 0.5F + thickness/2;

		if (side == ForgeDirection.UNKNOWN) {
			return AxisAlignedBB.getBoundingBox(min, min, min, max, max, max);
		}

		float[][] bounds = new float[3][2];
		// X START - END
		bounds[0][0] = min;
		bounds[0][1] = max;
		// Y START - END
		bounds[1][0] = 0;
		bounds[1][1] = min;
		// Z START - END
		bounds[2][0] = min;
		bounds[2][1] = max;

		MatrixTranformations.transform(bounds, side);
		return AxisAlignedBB.getBoundingBox(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]);
	}

	private RaytraceResult doRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
		TileEntity te = world.getTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		
		MovingObjectPosition[] hits = new MovingObjectPosition[31];
		AxisAlignedBB[] boxes = new AxisAlignedBB[31];
		ForgeDirection[] sideHit = new ForgeDirection[31];
		Arrays.fill(sideHit, ForgeDirection.UNKNOWN);
		
		for (ForgeDirection side : ForgeDirection.values()) {
			if (side == ForgeDirection.UNKNOWN || ((ISEGenericCable)te).connectedOnSide(side)) {
				AxisAlignedBB bb = getCableBoundingBox(side, (float) this.thickness[meta]);
				setBlockBounds((float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
				boxes[side.ordinal()] = bb;
				hits[side.ordinal()] = super.collisionRayTrace(world, x, y, z, origin, direction);
				sideHit[side.ordinal()] = side;
			}
		}
		
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
			if (((ISEGenericCable)te).getCoverPanelOnSide(side) != null){
				AxisAlignedBB bb = getCoverPanelBoundingBox(side);
				setBlockBounds((float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
				boxes[7 + side.ordinal()] = bb;
				hits[7 + side.ordinal()] = super.collisionRayTrace(world, x, y, z, origin, direction);
				sideHit[7 + side.ordinal()] = side;
			}
		}
		
		
		//get closest hit
		double minLengthSquared = Double.POSITIVE_INFINITY;
		int minIndex = -1;

		for (int i = 0; i < hits.length; i++) {
			MovingObjectPosition hit = hits[i];
			if (hit == null)
				continue;

			double lengthSquared = hit.hitVec.squareDistanceTo(origin);

			if (lengthSquared < minLengthSquared) {
				minLengthSquared = lengthSquared;
				minIndex = i;
			}
		}

		// reset bounds
		setBlockBounds(0, 0, 0, 1, 1, 1);

		if (minIndex == -1) {
			return null;	//The player is looking at the block, but his eye sight does not intersect with any part.
		} else {
			return new RaytraceResult(minIndex < 7, hits[minIndex], boxes[minIndex], sideHit[minIndex]);
		}
	}
	
	public RaytraceResult doRayTrace(World world, int x, int y, int z, EntityPlayer player) {
		double reachDistance = 5;

		if (player instanceof EntityPlayerMP) {
			reachDistance = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}

		double eyeHeight = world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight();
		Vec3 lookVec = player.getLookVec();
		Vec3 origin = Vec3.createVectorHelper(player.posX, player.posY + eyeHeight, player.posZ);
		Vec3 direction = origin.addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);

		return doRayTrace(world, x, y, z, origin, direction);
	}
	
	//Override MC functions
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
		RaytraceResult raytraceResult = doRayTrace(world, x, y, z, origin, direction);

		if (raytraceResult == null) {
			return null;
		} else {
			return raytraceResult.movingObjectPosition;
		}
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z,
			AxisAlignedBB axisAlignedBB, List collidingBoxes, Entity par7Entity){
		
		TileEntity te = world.getTileEntity(x, y, z);
		
		if (!(te instanceof ISEGenericCable))
			return;
		
		int meta = world.getBlockMetadata(x, y, z);
		ISEGenericCable cable = (ISEGenericCable)te;
		
		double min = 0.5 - thickness[meta]/2;
		double max = 0.5 + thickness[meta]/2;
	
		//Center
		addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, min, min, min, max, max, max);
	
		//Branches
		if (cable.connectedOnSide(ForgeDirection.DOWN))
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, min, 0, min, max, max, max);
		
		if (cable.connectedOnSide(ForgeDirection.UP))
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, min, min, min, max, 1, max);
		
		if (cable.connectedOnSide(ForgeDirection.NORTH))
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, min, min, 0, max, max, max);
		
		if (cable.connectedOnSide(ForgeDirection.SOUTH))
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, min, min, min, max, max, 1);
				
		if (cable.connectedOnSide(ForgeDirection.WEST))
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, min, min, max, max, max);
		
		if (cable.connectedOnSide(ForgeDirection.EAST))
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, min, min, min, 1, max, max);

		//Cover panel
		if (cable.getCoverPanelOnSide(ForgeDirection.DOWN) != null)
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0, 1, CoverPanel.thickness, 1);
		
		if (cable.getCoverPanelOnSide(ForgeDirection.UP) != null)
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 1 - CoverPanel.thickness, 0, 1, 1, 1);
		
		if (cable.getCoverPanelOnSide(ForgeDirection.NORTH) != null)
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0, 1, 1, CoverPanel.thickness);
		
		if (cable.getCoverPanelOnSide(ForgeDirection.SOUTH) != null)
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 1 - CoverPanel.thickness, 1, 1, 1);
				
		if (cable.getCoverPanelOnSide(ForgeDirection.WEST) != null)
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0, CoverPanel.thickness, 1, 1);
		
		if (cable.getCoverPanelOnSide(ForgeDirection.EAST) != null)
			addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 1 - CoverPanel.thickness, 0, 0, 1, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		RaytraceResult rayTraceResult = doRayTrace(world, x, y, z, Minecraft.getMinecraft().thePlayer);
			
		if (rayTraceResult != null && rayTraceResult.boundingBox != null && (!rayTraceResult.hitCenter)) {
			return rayTraceResult.boundingBox.offset(x, y, z);
		}
		TileEntity te = world.getTileEntity(x, y, z);
		
		if (te instanceof ISEGenericCable){
			int meta = world.getBlockMetadata(x, y, z);
			ISEGenericCable cable = (ISEGenericCable)te;
			
			double x1,y1,z1, x2,y2,z2;
			x1= 0.5 - thickness[meta]/2;
			y1= x1;
			z1= x1;
			x2= 0.5 + thickness[meta]/2;
			y2= x2;
			z2= x2;
			
			if (cable.connectedOnSide(ForgeDirection.DOWN))
				y1 = 0;
			
			if (cable.connectedOnSide(ForgeDirection.UP))
				y2 = 1;
			
			if (cable.connectedOnSide(ForgeDirection.NORTH))
				z1 = 0;
			
			if (cable.connectedOnSide(ForgeDirection.SOUTH))
				z2 = 1;
					
			if (cable.connectedOnSide(ForgeDirection.WEST))
				x1 = 0;
			
			if (cable.connectedOnSide(ForgeDirection.EAST))
				x2 = 1;
			
			return AxisAlignedBB.getBoundingBox(x1,y1,z1,x2,y2,z2).offset(x, y, z).expand(0.01, 0.01, 0.01);
		}
		
		return null;
	}

	//////////////////////////////////////
	/////Item drops and Block activities
	//////////////////////////////////////    
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_){
        TileEntity te = world.getTileEntity(x, y, z);
        
        if (!(te instanceof ISEGenericCable))
        	return false;		//Normally this could not happen, but just in case!
               
        ItemStack itemStack = player.getCurrentEquippedItem();
        if (itemStack == null)
        	return false;
        
        if (itemStack.stackSize == 0)
        	return false;
    
        ISEGenericCable cable = (ISEGenericCable) te;
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        
        ISECoverPanel coverPanel = SEEAPI.coverPanelFactory.fromItemStack(itemStack);
        if (coverPanel != null){
        	if (cable.getCoverPanelOnSide(direction) != null)
        		return false;	//Already have a cover panel installed
        	
        	if (!player.capabilities.isCreativeMode){
	        	itemStack.stackSize--;
	        	if (itemStack.stackSize == 0)
	        		itemStack = null;
        	}
        	
        	if (!world.isRemote){	//Handle on server side
        		cable.installCoverPanel(direction, coverPanel);
        		world.notifyBlocksOfNeighborChange(x, y, z, BlockRegistry.blockCable);
        	}
        	return true;
        }
        
        return false;
    }
	
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ISEGenericCable))
        	return;		//Normally this could not happen, but just in case!
    	
        ISEGenericCable cable = (ISEGenericCable) te;
        cable.onCableRenderingUpdateRequested();
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ISEGenericCable))
        	return;		//Normally this could not happen, but just in case!
        
        ISEGenericCable cable = (ISEGenericCable) te;
        cable.onCableRenderingUpdateRequested();
    }
    
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {	
		TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ISEGenericCable))
        	return null;		//Normally this could not happen, but just in case!
    	
        RaytraceResult result = doRayTrace(world, x, y, z, player);
        if (result != null && result.boundingBox != null) {
        	if (result.hitCenter){
        		return createStackedBlock(getDamageValue(world, x, y, z));
        	}else{
                ISEGenericCable cable = (ISEGenericCable) te;
                return cable.getCoverPanelOnSide(result.sideHit).getCoverPanelItem();
        	}
        }
        
		return null;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return null; // TODO QAQ!!!
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
        if (!world.isRemote){
    		TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof ISEGenericCable))
            	return;		//Normally this could not happen, but just in case!
            
            ISEGenericCable cable = (ISEGenericCable) te;
           
            for(ForgeDirection direction: ForgeDirection.VALID_DIRECTIONS){
            	ISECoverPanel coverPanel = cable.getCoverPanelOnSide(direction);
            	if (coverPanel != null){
            		Utils.dropItemIntoWorld(world, x, y, z, coverPanel.getCoverPanelItem());
            	}
            }
        }
		super.breakBlock(world, x, y, z, block, par6);
	}
}
