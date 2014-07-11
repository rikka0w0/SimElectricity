package simElectricity.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;
import simElectricity.mod_SimElectricity;

import java.util.Random;

public class BlockSimpleGenerator  extends BlockContainer {
	private IIcon[] iconBuffer = new IIcon[7];
	
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random var5){
    	TileSimpleGenerator te=(TileSimpleGenerator) world.getTileEntity(x,y,z);
    	if(te.isWorking){
   			double d0 = (x);
   			double d1 = (y);
   			double d2 = (z);
   			double d3 = 0.2199999988079071D;
   			double d4 = 0.27000001072883606D;
   			world.spawnParticle("smoke", d0 + d4 + 0.25F, d1 + d3 + 1F, d2 + 0.5F, 0.0D, 0.0D, 0.0D);
   			world.spawnParticle("smoke", d0 + d4 + 0.15F, d1 + d3 + 1F, d2 + 0.5F, 0.0D, 0.0D, 0.0D);
   			world.spawnParticle("smoke", d0 + d4 + 0.4F, d1 + d3 + 1F, d2 + 0.6F, 0.0D, 0.0D, 0.0D);
   			switch(te.getFacing()){
   			case WEST:
   				d0-=0.4F;
   				d1+=0.1F;
   				d2+=0.5F;
   				break;
   			case SOUTH:
   				d0+=0.25F;
   				d1+=0.1F;
   				d2+=1.1F;
   				break;
   			case NORTH:
   				d0+=0.25F;
   				d1+=0.1F;
   				d2-=0.1F;
   				break;
   			case EAST:
   				d0+=0.8F;
   				d1+=0.1F;
   				d2+=0.5F;
   				break;   		
			default:
				break;
   			}
   			
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
    	
    	if(!(te instanceof TileSimpleGenerator))
    		return false;
    	
    	player.openGui(mod_SimElectricity.instance, 0, world, x, y, z);
    	return true;
    }
    
    public BlockSimpleGenerator() {
		super(Material.rock);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("SimpleGenerator");
		setCreativeTab(Util.SETab);
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r){
    	iconBuffer[0] = r.registerIcon("simElectricity:SimpleGenerator_Bottom");
    	iconBuffer[1] = r.registerIcon("simElectricity:SimpleGenerator_Top");
    	iconBuffer[2] = r.registerIcon("simElectricity:SimpleGenerator_Front");
    	iconBuffer[3] = r.registerIcon("simElectricity:SimpleGenerator_Side");
    	iconBuffer[4] = r.registerIcon("simElectricity:SimpleGenerator_Side");
    	iconBuffer[5] = r.registerIcon("simElectricity:SimpleGenerator_Side");
    	iconBuffer[6] = r.registerIcon("simElectricity:SimpleGenerator_Front_W");
    }

	
    @SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x,int y, int z, int side) {
    	int blockMeta = world.getBlockMetadata(x, y, z);
    	TileEntity te=world.getTileEntity(x, y, z);
    	
    	if(!(te instanceof ISidedFacing))
    		return iconBuffer[0];
    	
    	int iconIndex=Util.getTextureOnSide(side, ((ISidedFacing)te).getFacing());
    	if(((TileSimpleGenerator)te).isWorking&&iconIndex==2)
    		iconIndex=6;
    	
    	return iconBuffer[iconIndex];
	}
	
    @SideOnly(Side.CLIENT)
   	@Override
   	public IIcon getIcon(int side, int meta) {
    	return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.WEST)];
   	}
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {       
        TileEntity te = world.getTileEntity(x, y, z);
        
        if (!(te instanceof IEnergyTile))
        	return;
        
        ((ISidedFacing)te).setFacing(Util.getPlayerSight(player).getOpposite());
        ((IEnergyTile)te).setFunctionalSide(Util.getPlayerSight(player));
    }	
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
     	if (world.isRemote)
    		return;    	
    	TileEntity te = world.getTileEntity(x, y, z);
    	if (!(te instanceof IEnergyTile))
    		return;

    	Util.updateTileEntityFacing(te);
    	Util.updateTileEntityFunctionalSide(te); 	
		Util.updateTileEntityField(te, "isWorking");
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
    	if (world.isRemote)
    		return;    	
    	
    	//Server side only!
    	TileEntity te = world.getTileEntity(x, y, z);
    	Util.updateTileEntityFacing(te);
    }
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {return new TileSimpleGenerator();}
	
	@Override
	public int damageDropped(int par1) {return par1;}
}
