package simElectricity.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
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
import simElectricity.mod_SimElectricity;
import simElectricity.API.Util;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockElectricFurnace extends BlockContainer{
	private IIcon[] iconBuffer = new IIcon[7];
	
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random var5){
    	int meta=world.getBlockMetadata(x, y, z);

    	TileElectricFurnace te=(TileElectricFurnace) world.getTileEntity(x,y,z);
    	if(te.isWorking){
   			double d0 = (double)((float)x + 0.5F);
   			double d1 = (double)((float)y + 1F);
   			double d2 = (double)((float)z + 0.5F);
   			double d3 = 0.2199999988079071D;
   			double d4 = 0.27000001072883606D;
   			world.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
   			world.spawnParticle("flame", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
   			world.spawnParticle("reddust", d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
    	}
    }
	
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3){
    	TileEntity te = world.getTileEntity(x, y, z);
    	
    	if(player.isSneaking())
    		return false;
    	
    	if(!(te instanceof TileElectricFurnace))
    		return false;
    	
    	player.openGui(mod_SimElectricity.instance, 0, world, x, y, z);
    	return true;
    }
    
    public BlockElectricFurnace() {
		super(Material.rock);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("sime:ElectricFurnace");
		setCreativeTab(Util.SETab);
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r){
    	iconBuffer[0] = r.registerIcon("simElectricity:ElectricFurnace_Bottom");
    	iconBuffer[1] = r.registerIcon("simElectricity:ElectricFurnace_Top");
    	iconBuffer[2] = r.registerIcon("simElectricity:ElectricFurnace_Back");
    	iconBuffer[3] = r.registerIcon("simElectricity:ElectricFurnace_Front");
    	iconBuffer[4] = r.registerIcon("simElectricity:ElectricFurnace_Side");
    	iconBuffer[5] = r.registerIcon("simElectricity:ElectricFurnace_Side");
    	iconBuffer[6] = r.registerIcon("simElectricity:ElectricFurnace_Front_W");
    }

	
    @SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x,int y, int z, int side) {
    	int blockMeta = world.getBlockMetadata(x, y, z);
    	TileEntity te=world.getTileEntity(x, y, z);
    	
    	if(!(te instanceof TileElectricFurnace))
    		return iconBuffer[0];
    	
    	int iconIndex=Util.getTextureOnSide(side, ((TileElectricFurnace)te).getFunctionalSide());
    	if(((TileElectricFurnace)te).isWorking&&iconIndex==3)
    		iconIndex=6;
    	
    	return iconBuffer[iconIndex];
	}
	
    @SideOnly(Side.CLIENT)
   	@Override
   	public IIcon getIcon(int side, int meta) {
    	return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.EAST)];
   	}
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {       
        TileEntity te = world.getTileEntity(x, y, z);
        
        if (!(te instanceof TileElectricFurnace))
        	return;
        
        ((TileElectricFurnace)te).functionalSide=Util.getPlayerSight(player);
        
    	if (world.isRemote)
    		return;
    	
    	//Server side only!
        Util.updateTileEntityField(te, "functionalSide");
    }	
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
     	if (world.isRemote)
    		return;    	
    	TileEntity te = world.getTileEntity(x, y, z);
    	if (!(te instanceof TileElectricFurnace))
    		return;
    	Util.updateTileEntityField(te, "functionalSide");   	
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
    	if (world.isRemote)
    		return;    	
    	
    	//Server side only!
    	TileEntity te = world.getTileEntity(x, y, z);
    	Util.updateTileEntityField(te, "functionalSide");
    }
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {return new TileElectricFurnace();}
	
	@Override
	public int damageDropped(int par1) {return par1;}
}
