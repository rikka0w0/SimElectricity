package simElectricity.Samples;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSample extends BlockContainer {
	public static final String[] subNames = { "Battery", "Conductor",
			"Resistor" };
	private Icon[] iconBuffer = new Icon[3];

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
		default:
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#registerIcons(net.minecraft.client.renderer.texture.IconRegister)
	 */
    @SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister) {
    	iconBuffer[0] = par1IconRegister.registerIcon("simElectricity:Block_SampleBattery");
    	iconBuffer[1] = par1IconRegister.registerIcon("simElectricity:Block_SampleConductor");
    	iconBuffer[2] = par1IconRegister.registerIcon("simElectricity:Block_SampleResistor");
//		super.registerIcons(par1IconRegister);
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#getBlockTexture(net.minecraft.world.IBlockAccess, int, int, int, int)
	 */
    @SideOnly(Side.CLIENT)
	@Override
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2,
			int par3, int par4, int par5) {
    	int blockMeta = par1iBlockAccess.getBlockMetadata(par2, par3, par4);
    	return iconBuffer[blockMeta];
//		return super.getBlockTexture(par1iBlockAccess, par2, par3, par4, par5);
	}

	/* (non-Javadoc)
	 * @see net.minecraft.block.Block#getIcon(int, int)
	 */
    @SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int par1, int par2) {
    	return iconBuffer[par2];
//		return super.getIcon(par1, par2);
	}

	public BlockSample(int id) {
		super(id, Material.rock);
		setHardness(2.0F);
		setResistance(5.0F);
		setUnlocalizedName("SESample");
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer entityPlayer, int par6, float par7, float par8,
			float par9) {

		if (entityPlayer.isSneaking())
			return false;

		TileEntity te = world.getBlockTileEntity(x, y, z);

		return false;
	}

	public int damageDropped(int par1) {
		return par1;
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int par1, CreativeTabs tab, List subItems) {
		for (int ix = 0; ix < subNames.length; ix++) {
			subItems.add(new ItemStack(this, 1, ix));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}

}
