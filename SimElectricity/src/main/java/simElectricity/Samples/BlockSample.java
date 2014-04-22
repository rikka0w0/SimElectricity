package simElectricity.Samples;

import java.util.List;
import java.util.Random;

import simElectricity.API.IEnergyTile;
import simElectricity.API.Util;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSample extends BlockContainer {
	public static final String[] subNames = { "Battery", "Conductor",
			"Resistor","SwitchOff","SwitchOn" };
	private IIcon[] iconBuffer = new IIcon[5];

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
	@Override
    public boolean canProvidePower()
    {
        return true;
    }
	
	@Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int p_149748_5_)
    {
		TileEntity te=world.getTileEntity(x, y, z);
		if (!(te instanceof TileSampleResistor))
			return 0;
		
		TileSampleResistor r=(TileSampleResistor)te;
		if(r.isWorking)		
			return 15;
		else
			return 0;
    }
    
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }
	
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack){
        int heading = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int pitch = Math.round(player.rotationPitch);
        
        TileEntity te = world.getTileEntity(x, y, z);
        
        if (!(te instanceof TileSampleEnergyTile))
        	return;
        
        ((TileSampleEnergyTile)te).functionalSide=Util.getPlayerSight(player).getOpposite();
        
    	if (world.isRemote)
    		return;
    	//Server side only!
        Util.updateTileEntityField(te, "functionalSide");
    }
	
	
	// Get TileEntities
	@Override
	public TileEntity createTileEntity(World world, int meta) {
		switch (meta) {
		case 0:
			return new TileSampleBattery();
		case 1:
			return new TileSampleConductor();
		case 2:
			return new TileSampleResistor();
		case 3:
			return null;
		case 4:
			return new TileSampleConductor();
		default:
			return null;
		}
	}

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r)
    {
    	iconBuffer[0] = r.registerIcon("simElectricity:Block_SampleBattery");
    	iconBuffer[1] = r.registerIcon("simElectricity:Block_SampleConductor");
    	iconBuffer[2] = r.registerIcon("simElectricity:Block_SampleResistor");
    	iconBuffer[3] = r.registerIcon("simElectricity:Block_SampleSwitch_Off");
    	iconBuffer[4] = r.registerIcon("simElectricity:Block_SampleSwitch_On");
//		super.registerIcons(par1IconRegister);
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#getBlockTexture(net.minecraft.world.IBlockAccess, int, int, int, int)
	 */
    @SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
    	int blockMeta = par1iBlockAccess.getBlockMetadata(par2, par3, par4);
    	return iconBuffer[blockMeta];
//		return super.getBlockTexture(par1iBlockAccess, par2, par3, par4, par5);
	}


    @SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int par1, int par2) {
    	return iconBuffer[par2];
//		return super.getIcon(par1, par2);
	}

	public BlockSample() {
		super(Material.rock);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("sime:SESample");
		setCreativeTab(Util.SETab);
	}

    @Override
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
     	if (world.isRemote)
    		return;    	
    	TileEntity te = world.getTileEntity(x, y, z);
    	if((!(te instanceof TileSampleResistor))&(!(te instanceof TileSampleBattery)))
    	return;
    	Util.updateTileEntityField(te, "isWorking");
    	Util.updateTileEntityField(te, "functionalSide");  
    }
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer entityPlayer, int par6, float par7, float par8,
			float par9) {

		if (entityPlayer.isSneaking())
			return false;
		
		TileEntity te = world.getTileEntity(x, y, z);
		int meta=world.getBlockMetadata(x, y, z);
		
		if(meta==3){
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
			world.notifyBlocksOfNeighborChange(x, y, z, null);
			return true;
		}
		else if(meta==4){
			world.removeTileEntity(x, y, z);
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
			return true;
		}
		return false;
	}

	public int damageDropped(int par1) {
		return par1;
	}

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List subItems){
		for (int ix = 0; ix < subNames.length; ix++) {
			subItems.add(new ItemStack(this, 1, ix));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world,int i) {
		return null;
	}

    //@SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random var5){
    	int meta=world.getBlockMetadata(x, y, z);
    	if (meta==2){

    		TileSampleResistor te=(TileSampleResistor) world.getTileEntity(x,y,z);
    		if(te.isWorking){
    			//world.setLightValue(EnumSkyBlock.Block, x, y, z, 10);
    			double d0 = (double)((float)x + 0.5F);
    			double d1 = (double)((float)y + 1F);
    			double d2 = (double)((float)z + 0.5F);
    			double d3 = 0.2199999988079071D;
    			double d4 = 0.27000001072883606D;
    			world.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
    			world.spawnParticle("flame", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
    			world.spawnParticle("reddust", d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
    		}
    		//else
    			//world.setLightValue(EnumSkyBlock.Block, x, y, z, 0);
    	}
    }
}
