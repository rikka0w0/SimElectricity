package SimElectricity.Samples;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSample extends BlockContainer{
	public static final String[] subNames = {"Battery","Conductor","Resistor"};
	private Icon[][] iconBuffer;

	
	//Get TileEntities
    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
    	switch (meta){
    		case 0:
    			return new TileSampleBattery();
    		case 1:
    			return new TileSampleConductor();
    		case 2:
    			return new TileSampleResistor();
    		default:
    				return null;
    	}
    }
    
    public BlockSample(int id) {
		super(id, Material.rock);
        setHardness(2.0F);
        setResistance(5.0F);
        setUnlocalizedName("SESample");
        setCreativeTab(CreativeTabs.tabRedstone);
	}
	
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9)
    {
    
        if (entityPlayer.isSneaking())
            return false;   
        
        TileEntity te = world.getBlockTileEntity(x, y, z);     
        
        return false;
    }
    
    public int damageDropped(int par1){return par1;} 
   

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int par1, CreativeTabs tab, List subItems) {
		for (int ix = 0; ix < subNames.length; ix++) {
			subItems.add(new ItemStack(this, 1, ix));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {return null;}
	

}
