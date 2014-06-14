package simElectricity.Test;

import java.util.Random;

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
import simElectricity.API.Energy;
import simElectricity.API.Util;
import simElectricity.API.Common.TileComplexMachine;
import simElectricity.API.EnergyTile.ICircuitComponent;
import simElectricity.Test.TileBatteryBox.CircuitComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTransformer extends BlockContainer{
	public static class TileTransformer extends TileComplexMachine{
		public CircuitComponent sink,source; 
		public ForgeDirection sinkDirection,sourceDirection;
		
		public static float ratio=10F;
		public float lastOutputResistance=Float.MAX_VALUE;
		
		@Override 
		public void onLoad(){
			sink = new CircuitComponent();
			source = new CircuitComponent();
			
			sink.resistance=Float.MAX_VALUE;
			source.resistance=1;
			sink.voltage=0;
		}
		
		@Override
		public void updateEntity() {
			super.updateEntity();
		
			if(worldObj.isRemote)
				return;
			
			
			sink.resistance=lastOutputResistance;
			sink.resistance/=ratio*ratio;
			if(Energy.getCurrent(source,worldObj)<=0){
				lastOutputResistance=Float.MAX_VALUE;
			}else{
				lastOutputResistance=Energy.getVoltage(source, worldObj)/Energy.getCurrent(source,worldObj);
			}
			source.voltage=Energy.getVoltage(sink, worldObj)*ratio;
			Energy.postTileChangeEvent(this);
			
			
		}
		
		@Override
		public ICircuitComponent getCircuitComponent(ForgeDirection side) {
			if(side==sinkDirection)
				return sink;
			else if(side==sourceDirection)
				return source;
			else
				return null;
		}

		@Override
		public boolean canConnectOnSide(ForgeDirection side) {
			if (side==sinkDirection||side==sourceDirection)
				return true;
			else 
				return false;
		}
		
		@Override
		public int getInventorySize() {return 2;}
		
	}
	private IIcon[] iconBuffer = new IIcon[6];
	
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {       
        TileEntity te = world.getTileEntity(x, y, z);

        
        ((TileTransformer)te).sinkDirection=Util.getPlayerSight(player).getOpposite();
        ((TileTransformer)te).sourceDirection=Util.getPlayerSight(player);
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_) {
     	if (world.isRemote)
    		return;    	
    	TileEntity te = world.getTileEntity(x, y, z);

    	Util.updateTileEntityField(te, "sinkDirection");
    	Util.updateTileEntityField(te, "sourceDirection");
    }
    
    public BlockTransformer() {
		super(Material.rock);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("sime:Transformer");
		setCreativeTab(Util.SETab);
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r){
    	iconBuffer[0] = r.registerIcon("simElectricity:AdjustableResistor_Bottom");
    	iconBuffer[1] = r.registerIcon("simElectricity:AdjustableResistor_Top");
    	iconBuffer[2] = r.registerIcon("simElectricity:AdjustableResistor_Front");
    	iconBuffer[3] = r.registerIcon("simElectricity:AdjustableResistor_Side");
    	iconBuffer[4] = r.registerIcon("simElectricity:AdjustableResistor_Side");
    	iconBuffer[5] = r.registerIcon("simElectricity:AdjustableResistor_Side");
    }

	
    @SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x,int y, int z, int side) {
    	int blockMeta = world.getBlockMetadata(x, y, z);
    	TileEntity te=world.getTileEntity(x, y, z);

    	
    	return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.NORTH)];
	}
	
    @SideOnly(Side.CLIENT)
   	@Override
   	public IIcon getIcon(int side, int meta) {
    	return iconBuffer[Util.getTextureOnSide(side, ForgeDirection.WEST)];
   	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {return new TileTransformer();}
	
	@Override
	public int damageDropped(int par1) {return par1;}
}
