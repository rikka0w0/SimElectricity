package simelectricity.essential.grid.transformer;

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
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.common.multiblock.MultiBlockStructure;
import simelectricity.essential.common.multiblock.MultiBlockStructure.BlockInfo;
import simelectricity.essential.grid.SEModelBlock;

public class BlockPowerTransformer extends SEModelBlock implements ITileEntityProvider, ISESubBlock, ISESimpleTextureItem, ISEHVCableConnector {
	public static final String[] subNames = EnumBlockType.getRawStructureNames();
	
	public final MultiBlockStructure structureTemplate;
	
	public BlockPowerTransformer() {
		super("essential_powertransformer", Material.GLASS, SEItemBlock.class);
		
		this.structureTemplate = createStructureTemplate();
	}
	
	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return subNames;
	}
	
	@Override
	public String getIconName(int damage) {
		return "powertransformer_" + subNames[damage];
	}
	
	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		EnumBlockType blockType = EnumBlockType.fromInt(meta);
		
		if (!blockType.formed)
			return null;
		
		switch (blockType) {
		case Placeholder:
			return new TilePowerTransformerPlaceHolder();
		case PlaceholderPrimary:
			break;
		case PlaceholderSecondary:
			break;
		case Primary:
			return new TilePowerTransformerPrimary();
		case Render:
			break;
		case Secondary:
			return new TilePowerTransformerSecondary();
		default:
			break;
		}
		
		return new TilePowerTransformerPlaceHolder();
	}
	
	///////////////////////////////
	///BlockStates
	///////////////////////////////
	@Override
	protected final BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {EnumBlockType.property});
	}
	
	@Override
    public final IBlockState getStateFromMeta(int meta){
        return stateFromType(EnumBlockType.fromInt(meta));
    }
	
	@Override
    public final int getMetaFromState(IBlockState state){
		return state.getValue(EnumBlockType.property).index;
    }
	
	public IBlockState stateFromType(EnumBlockType blockType) {
		return super.getDefaultState().withProperty(EnumBlockType.property, blockType);
	}
	
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }
	
	///////////////////////////////
	/// Block activities
	///////////////////////////////
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (world.isRemote)
			return;
    	
    	MultiBlockStructure.Result ret = structureTemplate.attempToBuild(world, pos);
		if (ret != null){
			ret.createStructure();
		}
    	return;
    }
   	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
			structureTemplate.restoreStructure(te, state);
		
		super.breakBlock(world, pos, state);
	}
    
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity te = world.getTileEntity(pos);
		
		if (!world.isRemote && te != null)
			System.out.println(te.getClass().toString());
		return false;
	}
	
	public MultiBlockStructure createStructureTemplate() {
		//y,z,x facing NORTH(Z-), do not change 
		BlockInfo[][][] configuration = new BlockInfo[5][][];
		
		BlockInfo core2PH = new BlockInfo(stateFromType(EnumBlockType.IronCore), stateFromType(EnumBlockType.Placeholder));
		BlockInfo coil2PH = new BlockInfo(stateFromType(EnumBlockType.Winding), stateFromType(EnumBlockType.Placeholder));
		BlockInfo support2PH = new BlockInfo(stateFromType(EnumBlockType.OilTankSupport), stateFromType(EnumBlockType.Placeholder));
		BlockInfo pipe2PH = new BlockInfo(stateFromType(EnumBlockType.OilPipe), stateFromType(EnumBlockType.Placeholder));
		BlockInfo tank2PH = new BlockInfo(stateFromType(EnumBlockType.OilTank), stateFromType(EnumBlockType.Placeholder));
		BlockInfo casing2PH = new BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.Placeholder));
		BlockInfo casing2PHpri = new BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.PlaceholderPrimary));
		BlockInfo casing2PHsec = new BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.PlaceholderSecondary));
		BlockInfo casing2pri = new BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.Primary));
		BlockInfo casing2sec = new BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.Secondary));
		BlockInfo casing2render = new BlockInfo(stateFromType(EnumBlockType.Casing), stateFromType(EnumBlockType.Render));
		
		
		//  .-->x+ (East)
		//  |                           Facing/Looking at North(x-)
		// \|/
		//  z+ (South)
		configuration[0] = new BlockInfo[][]{
			{null			,casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2PHpri,	null},
			{casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2PHpri,	casing2PHpri},
			{casing2PH		,casing2PH		,casing2PH		,casing2PH		,casing2PH		,casing2PH	,	casing2PH},
			{casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec,	casing2PHsec},
			{null			,casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec,	null}
		};
		
		configuration[1] = new BlockInfo[][]{
			{null			,casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2PHpri,	null},
			{casing2PHpri	,coil2PH		,coil2PH		,coil2PH		,coil2PH		,coil2PH	,	casing2PHpri},
			{casing2PH		,coil2PH		,core2PH		,core2PH		,core2PH		,coil2PH	,	casing2PH},
			{casing2PHsec	,coil2PH		,coil2PH		,coil2PH		,coil2PH		,coil2PH	,	casing2PHsec},
			{null			,casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec,	null}
		};
		
		configuration[2] = new BlockInfo[][]{
			{null			,casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2PHpri,	null},
			{casing2PHpri	,casing2PHpri	,casing2PHpri	,casing2pri		,casing2PHpri	,casing2PHpri,	casing2PHpri},
			{casing2PH		,casing2PH		,casing2PH		,casing2render	,casing2PH		,casing2PH	,	casing2PH},
			{casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2sec		,casing2PHsec,	casing2PHsec},
			{support2PH		,casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec	,casing2PHsec,	null}
		};
		
		configuration[3] = new BlockInfo[][]{
			{null			,null			,null			,null			,null			,null		,	null},
			{null			,null			,null			,null			,null			,null		,	null},
			{support2PH		,null			,null			,null			,null			,null		,	null},
			{null			,pipe2PH		,null			,null			,null			,null		,	null},
			{support2PH		,null			,null			,null			,null			,null		,	null}
		};
		
		configuration[4] = new BlockInfo[][]{
			{null			,null			,null			,null			,null			,null		,	null},
			{null			,null			,null			,null			,null			,null		,	null},
			{tank2PH		,null			,null			,null			,null			,null		,	null},
			{tank2PH		,pipe2PH		,null			,null			,null			,null		,	null},
			{tank2PH		,null			,null			,null			,null			,null		,	null}
		};
		
		return new MultiBlockStructure(configuration);
	}

	@Override
	public ISESimulatable getNode(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof ISEGridTile) {
			return ((ISEGridTile) te).getGridNode();
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canHVCableConnect(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof ISEGridTile) {
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}
}
