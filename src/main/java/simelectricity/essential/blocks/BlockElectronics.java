package simelectricity.essential.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.essential.Essential;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.common.SESinglePortMachine;
import simelectricity.essential.utils.Utils;

public class BlockElectronics  extends SEBlock implements ITileEntityProvider, ISESubBlock{
	private static final String[] subNames = new String[]{"voltage_meter"};
	
	//[meta][side]
	private final IIcon[][] iconBuffer;
	
	///////////////////////////////
	///Block Properties
	///////////////////////////////
	public BlockElectronics() {
		super("essential_electronics", Material.rock, SEItemBlock.class);
		iconBuffer = new IIcon[subNames.length][6];
	}

	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return subNames;
	}

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch (meta){
		case 0:
			return new TileVoltageMeter();
		}
		return null;
	}
	
	
	////////////////////////////////////
	/// Rendering
	////////////////////////////////////
	@Deprecated	//Removed in 1.8 and above
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
		//Voltage meter
		iconBuffer[0][0] = iconRegister.registerIcon("sime_essential:voltage_meter_side");
		iconBuffer[0][1] = iconRegister.registerIcon("sime_essential:voltage_meter_side");
		iconBuffer[0][2] = iconRegister.registerIcon("sime_essential:voltage_meter_front");
		iconBuffer[0][3] = iconRegister.registerIcon("sime_essential:voltage_meter_side");
		iconBuffer[0][4] = iconRegister.registerIcon("sime_essential:voltage_meter_side");
		iconBuffer[0][5] = iconRegister.registerIcon("sime_essential:voltage_meter_side");
    }
	
	
	@Deprecated	//Removed in 1.8 and above
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
    	TileEntity te = world.getTileEntity(x, y, z);
    	int meta = world.getBlockMetadata(x, y, z);
    	
    	if (te instanceof ISidedFacing){
    		int facing = ((ISidedFacing) te).getFacing().ordinal();
    		return iconBuffer[meta][Utils.sideAndFacingToSpriteOffset[side][facing]];
    	}else{
    		return iconBuffer[meta][2];
    	}
    }
    		
    
	@Deprecated	//Removed in 1.8 and above
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
		return iconBuffer[meta][side];
	}
	
	//////////////////////////////////////
	/////Item drops and Block activities
	//////////////////////////////////////    
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_){
        if (player.isSneaking())
            return false;

        player.openGui(Essential.instance, 0, world, x, y, z);
        return true;
	}
	
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, player, itemStack);
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(x, y, z);
        
        if (te instanceof SESinglePortMachine){
            ForgeDirection sight = Utils.getPlayerSight(player, false);
            ((SESinglePortMachine) te).setFacing(sight);
            ((SESinglePortMachine) te).setFunctionalSide(sight);
        }
    }
}
