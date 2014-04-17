package simElectricity.Blocks;

import java.util.Random;

import simElectricity.API.Util;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockQuantumGenerator extends BlockContainer {
	private IIcon[] iconBuffer = new IIcon[6];
	
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random var5){
    	//world.markBlockForUpdate(x,  y,  z);
    }
	
    public BlockQuantumGenerator() {
		super(Material.rock);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("sime:QuantumGenerator");
		setCreativeTab(Util.SETab);
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r){
    	iconBuffer[0] = r.registerIcon("simElectricity:IndustrialDevice_Side");
    	iconBuffer[1] = r.registerIcon("simElectricity:QuantumGenerator_Side");
    	iconBuffer[2] = r.registerIcon("simElectricity:QuantumGenerator_Front");
    	iconBuffer[3] = r.registerIcon("simElectricity:QuantumGenerator_Side");
    	iconBuffer[4] = r.registerIcon("simElectricity:QuantumGenerator_Side");
    	iconBuffer[5] = r.registerIcon("simElectricity:QuantumGenerator_Side");
    }

	
    @SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x,int y, int z, int side) {
    	int blockMeta = world.getBlockMetadata(x, y, z);
    	TileEntity te=world.getTileEntity(x, y, z);
    	
    	if(!(te instanceof TileQuantumGenerator))
    		return iconBuffer[0];
    	
    	//System.out.println(((TileQuantumGenerator)te).getFunctionalSide());
    	return iconBuffer[Util.getTextureOnSide(side, ((TileQuantumGenerator)te).getFunctionalSide())];
	}
	
    @SideOnly(Side.CLIENT)
   	@Override
   	public IIcon getIcon(int side, int meta) {
    	return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.WEST)];
   	}
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {       
        TileEntity te = world.getTileEntity(x, y, z);
        
        if (!(te instanceof TileQuantumGenerator))
        	return;
        
        ((TileQuantumGenerator)te).functionalSide=Util.getPlayerSight(player).getOpposite();
        
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
    	if (!(te instanceof TileQuantumGenerator))
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
	public TileEntity createNewTileEntity(World var1, int var2) {return new TileQuantumGenerator();}
	
	@Override
	public int damageDropped(int par1) {return par1;}
}
