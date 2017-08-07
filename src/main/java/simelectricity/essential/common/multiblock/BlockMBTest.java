package simelectricity.essential.common.multiblock;

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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.utils.Utils;

public class BlockMBTest extends SEBlock implements ITileEntityProvider{
	public static MultiBlockStructure qaq;
	public BlockMBTest() {
		super("essential_mbtest", Material.rock, SEItemBlock.class);
		
		qaq = new MultiBlockStructure(generateConfiguration());
	}

	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}
	

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
		MultiBlockStructure.Result ret = qaq.check(world, x, y, z);
		if (ret != null){
			ret.createStructure();
		}
    	return;
    }
   	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (meta == 1)
			qaq.restoreStructure(world.getTileEntity(x, y, z), block, meta);
		
		super.breakBlock(world, x, y, z, block, meta);
	}
    
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_){
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
	protected final IIcon[][] iconBuffer = new IIcon[2][6];
	@Deprecated	//Removed in 1.8 and above
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
		iconBuffer[0][0] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[0][1] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[0][2] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[0][3] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[0][4] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[0][5] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		
		iconBuffer[1][0] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
		iconBuffer[1][1] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
		iconBuffer[1][2] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
		iconBuffer[1][3] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_front");
		iconBuffer[1][4] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
		iconBuffer[1][5] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
	}
	
	@Deprecated	//Removed in 1.8 and above
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
		return iconBuffer[meta][Utils.sideAndFacingToSpriteOffset[side][3]];	//2 - North, Default facing
	}

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
    	TileEntity te = world.getTileEntity(x, y, z);
    	int meta = world.getBlockMetadata(x, y, z);
    	
    	if (te instanceof TileMBTest){
    		MultiBlockTileInfo info = ((TileMBTest) te).getMultiBlockTileInfo();
    		if (info != null){
    			ForgeDirection facing = info.facing;
    			return iconBuffer[meta][Utils.sideAndFacingToSpriteOffset[side][facing.ordinal()]];
    		}
    		
    		
    		return iconBuffer[meta][Utils.sideAndFacingToSpriteOffset[side][3]];
    	}
    	
    	return iconBuffer[meta][3];
    }
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		if (meta == 1){
			return new TileMBTest();
		}
		return null;
	}
}
