package simelectricity.essential.grid;

import java.lang.ref.WeakReference;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.common.UnlistedNonNullProperty;

public class BlockCableJoint extends SEBlock implements ITileEntityProvider, ISEHVCableConnector, ISESimpleTextureItem{
	public BlockCableJoint() {
		super("essential_cable_joint", Material.GLASS, SEItemBlock.class);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getIconName(int damage) {
		return "essential_cable_joint";
	}
	
	///////////////////////////////
	///BlockStates
	///////////////////////////////
	@Override
	protected final BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, 
				new IProperty[] {Properties.propertyFacing},
				new IUnlistedProperty[] {UnlistedNonNullProperty.propertyGridTile});
	}
	
	@Override
    public final IBlockState getStateFromMeta(int meta){		
        return super.getDefaultState().withProperty(Properties.propertyFacing, meta & 7);
    }
	
	@Override
    public final int getMetaFromState(IBlockState state){
		int facing = state.getValue(Properties.propertyFacing);
		return facing;
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

	///////////////////
	/// Initialize
	///////////////////
	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileCableJoint();
	}
	
	////////////////////////////////////
	/// Rendering
	////////////////////////////////////
    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}
	
	//////////////////////////////////////
	/////Item drops and Block activities
	//////////////////////////////////////
	@Override
    public int damageDropped(IBlockState state){
        return 0;
    }
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
		int facingInt = 8 - MathHelper.floor((placer.rotationYaw) * 8.0F / 360.0F + 0.5D) & 7;
		return state.withProperty(Properties.propertyFacing, facingInt);
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
    	TileEntity te = world.getTileEntity(pos);	//Do this before the tileEntity is removed!
    	if (te instanceof ISEGridTile)
    		SEAPI.energyNetAgent.detachGridNode(world, ((ISEGridTile) te).getGridNode());
    	
        super.breakBlock(world, pos, state);
    }
    
	//////////////////////////////////////
	/// ISEHVCableConnector
	//////////////////////////////////////
	@Override
	public boolean canHVCableConnect(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileCableJoint)
			return ((TileCableJoint) te).canConnect();
		else
			return false;
	}

	@Override
	public ISEGridNode getNode(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof ISEGridTile)
			return ((ISEGridTile) te).getGridNode();
		
		return null;
	}
}
