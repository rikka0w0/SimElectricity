package simelectricity.essential.machines;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import simelectricity.essential.Essential;
import simelectricity.essential.common.SEMachineBlock;
import simelectricity.essential.common.SETwoPortMachine;
import simelectricity.essential.machines.tile.TileAdjustableTransformer;
import simelectricity.essential.machines.tile.TileDiode;
import simelectricity.essential.machines.tile.TileSwitch;
import simelectricity.essential.machines.tile.TileVoltageRegulator;
import simelectricity.essential.utils.Utils;

public class BlockTwoPortElectronics extends SEMachineBlock{
	///////////////////////////////
	///Block Properties
	///////////////////////////////
	public BlockTwoPortElectronics() {
		super("essential_two_port_electronics", new String[]{"adjustable_transformer","voltage_regulator","diode","switch"});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch (meta){
		case 0:
			return new TileAdjustableTransformer();
		case 1:
			return new TileVoltageRegulator();
		case 2:
			return new TileDiode();
		case 3:
			return new TileSwitch();
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
		//Adjustable Transformer
		iconBuffer[0][0] = iconRegister.registerIcon("sime_essential:machines/adjustable_transformer");
		iconBuffer[0][1] = iconRegister.registerIcon("sime_essential:machines/adjustable_transformer");
		iconBuffer[0][2] = iconRegister.registerIcon("sime_essential:machines/adjustable_transformer");
		iconBuffer[0][3] = iconRegister.registerIcon("sime_essential:machines/adjustable_transformer");
		iconBuffer[0][4] = iconRegister.registerIcon("sime_essential:machines/adjustable_transformer");
		iconBuffer[0][5] = iconRegister.registerIcon("sime_essential:machines/adjustable_transformer");
		
		//Voltage Regulator
		iconBuffer[1][0] = iconRegister.registerIcon("sime_essential:machines/voltage_regulator");
		iconBuffer[1][1] = iconRegister.registerIcon("sime_essential:machines/voltage_regulator");
		iconBuffer[1][2] = iconRegister.registerIcon("sime_essential:machines/voltage_regulator");
		iconBuffer[1][3] = iconRegister.registerIcon("sime_essential:machines/voltage_regulator");
		iconBuffer[1][4] = iconRegister.registerIcon("sime_essential:machines/voltage_regulator");
		iconBuffer[1][5] = iconRegister.registerIcon("sime_essential:machines/voltage_regulator");
		
		//Diode
		iconBuffer[2][0] = iconRegister.registerIcon("sime_essential:machines/diode");
		iconBuffer[2][1] = iconRegister.registerIcon("sime_essential:machines/diode");
		iconBuffer[2][2] = iconRegister.registerIcon("sime_essential:machines/diode");
		iconBuffer[2][3] = iconRegister.registerIcon("sime_essential:machines/diode");
		iconBuffer[2][4] = iconRegister.registerIcon("sime_essential:machines/diode");
		iconBuffer[2][5] = iconRegister.registerIcon("sime_essential:machines/diode");
		
		//Switch
		iconBuffer[3][0] = iconRegister.registerIcon("sime_essential:machines/switch_side");
		iconBuffer[3][1] = iconRegister.registerIcon("sime_essential:machines/switch_side");
		iconBuffer[3][2] = iconRegister.registerIcon("sime_essential:machines/switch_side");
		iconBuffer[3][3] = iconRegister.registerIcon("sime_essential:machines/switch_front");
		iconBuffer[3][4] = iconRegister.registerIcon("sime_essential:machines/switch_side");
		iconBuffer[3][5] = iconRegister.registerIcon("sime_essential:machines/switch_side");
		
		iconBuffer2[3] = new IIcon[6];
		iconBuffer2[3][0] = iconRegister.registerIcon("sime_essential:machines/switch_side");
		iconBuffer2[3][1] = iconRegister.registerIcon("sime_essential:machines/switch_side");
		iconBuffer2[3][2] = iconRegister.registerIcon("sime_essential:machines/switch_side");
		iconBuffer2[3][3] = iconRegister.registerIcon("sime_essential:machines/switch_front_off");
		iconBuffer2[3][4] = iconRegister.registerIcon("sime_essential:machines/switch_side");
		iconBuffer2[3][5] = iconRegister.registerIcon("sime_essential:machines/switch_side");		
	}
	
	//////////////////////////////////////
	/////Item drops and Block activities
	//////////////////////////////////////
	@Override
	protected boolean isSecondState(TileEntity te){
		if (te instanceof TileSwitch && !((TileSwitch) te).isOn)
			return true;
		return false;
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_){
        if (player.isSneaking())
            return false;
        
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileSwitch){
        	TileSwitch tileSwitch = (TileSwitch) te;
        	if (tileSwitch.getFacing() == ForgeDirection.getOrientation(side)){
                if (!world.isRemote)
                	tileSwitch.setSwitchStatus(!tileSwitch.isOn);
        		return true;
        	}
        }
        
        player.openGui(Essential.instance, 0, world, x, y, z);
        return true;
	}
	
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        super.onBlockPlacedBy(world, x, y, z, player, itemStack);
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(x, y, z);
        
        if (te instanceof SETwoPortMachine){
            ForgeDirection sight = Utils.getPlayerSight(player);
            ((SETwoPortMachine) te).setFacing(sight.getOpposite());
            ((SETwoPortMachine) te).setFunctionalSide(sight.getOpposite(), sight);
        }
    }
}
