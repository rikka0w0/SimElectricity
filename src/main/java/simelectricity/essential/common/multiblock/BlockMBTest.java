package simelectricity.essential.common.multiblock;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import simelectricity.api.SEAPI;
import simelectricity.essential.client.semachine.ISESidedTextureBlock;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.utils.Utils;

public class BlockMBTest extends SEBlock implements ITileEntityProvider, ISESidedTextureBlock{
	public static MultiBlockStructure qaq;
	public BlockMBTest() {
		super("essential_mbtest", Material.ROCK, SEItemBlock.class);
		
		qaq = new MultiBlockStructure(generateConfiguration());
	}

	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}
	
	///////////////////////////////
	///BlockStates
	///////////////////////////////
	public final static IProperty<Boolean> propertyFormed = PropertyBool.create("formed");
	
	@Override
	protected final BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {propertyFormed});
	}
	
	@Override
    public final IBlockState getStateFromMeta(int meta){
        return super.getDefaultState().withProperty(propertyFormed, meta>0);
    }
	
	@Override
    public final int getMetaFromState(IBlockState state){
		return state.getValue(propertyFormed)?1:0;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		MultiBlockStructure.Result ret = qaq.attempToBuild(world, pos);
		if (ret != null){
			ret.createStructure();
		}
    	return;
    }
   	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
			qaq.restoreStructure(te, state);
		
		super.breakBlock(world, pos, state);
	}
    
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			
			if (te instanceof TileMBTest) {
				Utils.chat(player, ((TileMBTest) te).mbInfo.facing + "," + ((TileMBTest) te).mbInfo.mirrored);
			}
			
		}
		return false;
	}
    
	private MultiBlockStructure.BlockInfo[][][] generateConfiguration(){
		MultiBlockStructure.BlockInfo x = new MultiBlockStructure.BlockInfo(this, 0, this, 1);
		MultiBlockStructure.BlockInfo[][][] configuration = new MultiBlockStructure.BlockInfo[1][][];
		
		configuration[0] = new MultiBlockStructure.BlockInfo[][]{
				{x,null,x},
				{x,x,x,x,x}
		};
		
		return configuration;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		if (meta == 1){
			return new TileMBTest();
		}
		return null;
	}

	@Override
	public String getModelNameFrom(IBlockState blockState) {
		boolean formed = blockState.getValue(propertyFormed);
		return formed ? "electronics_solar_panel" : "electronics_voltage_meter";
	}

	@Override
	public boolean hasSecondState(IBlockState state) {
		return false;
	}
}
