package simelectricity.essential.machines;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import simelectricity.essential.Essential;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEMachineBlock;
import simelectricity.essential.common.SESinglePortMachine;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.machines.tile.TileIncandescentLamp;
import simelectricity.essential.machines.tile.TileQuantumGenerator;
import simelectricity.essential.machines.tile.TileSolarPanel;
import simelectricity.essential.machines.tile.TileVoltageMeter;
import simelectricity.essential.utils.Utils;

public class BlockElectronics extends SEMachineBlock implements ITileEntityProvider, ISESubBlock{
	///////////////////////////////
	///Block Properties
	///////////////////////////////
	public BlockElectronics() {
		super("essential_electronics", new String[]{"voltage_meter", "quantum_generator", "adjustable_resistor", "incandescent_lamp", "solar_panel"});
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch (meta){
		case 0:
			return new TileVoltageMeter();
		case 1:
			return new TileQuantumGenerator();
		case 2:
			return new TileAdjustableResistor();
		case 3:
			return new TileIncandescentLamp();
		case 4:
			return new TileSolarPanel();
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
		//Voltage Meter
		iconBuffer[0][0] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
		iconBuffer[0][1] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
		iconBuffer[0][2] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
		iconBuffer[0][3] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_front");
		iconBuffer[0][4] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
		iconBuffer[0][5] = iconRegister.registerIcon("sime_essential:machines/voltage_meter_side");
		
		//Quantum Generator
		iconBuffer[1][0] = iconRegister.registerIcon("sime_essential:machines/quantum_generator");
		iconBuffer[1][1] = iconRegister.registerIcon("sime_essential:machines/quantum_generator");
		iconBuffer[1][2] = iconRegister.registerIcon("sime_essential:machines/quantum_generator");
		iconBuffer[1][3] = iconRegister.registerIcon("sime_essential:machines/quantum_generator");
		iconBuffer[1][4] = iconRegister.registerIcon("sime_essential:machines/quantum_generator");
		iconBuffer[1][5] = iconRegister.registerIcon("sime_essential:machines/quantum_generator");
		
		//Adjustable Resistor
		iconBuffer[2][0] = iconRegister.registerIcon("sime_essential:machines/adjustable_resistor_bottom");
		iconBuffer[2][1] = iconRegister.registerIcon("sime_essential:machines/adjustable_resistor_top");
		iconBuffer[2][2] = iconRegister.registerIcon("sime_essential:machines/adjustable_resistor_side");
		iconBuffer[2][3] = iconRegister.registerIcon("sime_essential:machines/adjustable_resistor_front");
		iconBuffer[2][4] = iconRegister.registerIcon("sime_essential:machines/adjustable_resistor_side");
		iconBuffer[2][5] = iconRegister.registerIcon("sime_essential:machines/adjustable_resistor_side");
		
		//Incandescent Lamp
		iconBuffer[3][0] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[3][1] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[3][2] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[3][3] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[3][4] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		iconBuffer[3][5] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp");
		
		iconBuffer2[3] = new IIcon[6];
		iconBuffer2[3][0] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp_off");
		iconBuffer2[3][1] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp_off");
		iconBuffer2[3][2] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp_off");
		iconBuffer2[3][3] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp_off");
		iconBuffer2[3][4] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp_off");
		iconBuffer2[3][5] = iconRegister.registerIcon("sime_essential:machines/incandescent_lamp_off");
		
		//Solar Panel
		iconBuffer[4][0] = iconRegister.registerIcon("sime_essential:machines/solar_panel_bottom");
		iconBuffer[4][1] = iconRegister.registerIcon("sime_essential:machines/solar_panel_top");
		iconBuffer[4][2] = iconRegister.registerIcon("sime_essential:machines/solar_panel_side");
		iconBuffer[4][3] = iconRegister.registerIcon("sime_essential:machines/solar_panel_side");
		iconBuffer[4][4] = iconRegister.registerIcon("sime_essential:machines/solar_panel_side");
		iconBuffer[4][5] = iconRegister.registerIcon("sime_essential:machines/solar_panel_side");
    }
	
	//////////////////////////////////////
	/////Item drops and Block activities
	//////////////////////////////////////
	@Override
	protected boolean isSecondState(TileEntity te){
		if (te instanceof TileIncandescentLamp && ((TileIncandescentLamp) te).lightLevel < 8)
			return true;
		return false;
	}
	
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        
        if (te instanceof TileIncandescentLamp){
        	return ((TileIncandescentLamp) te).lightLevel;
        }
        return 0;
    }
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_){
        if (player.isSneaking())
            return false;

        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 3 || meta == 4)
        	return false;	//Incandescent Lamp doesn't have an Gui!
        
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
            ForgeDirection sight = Utils.getPlayerSight(player);
            ((SESinglePortMachine) te).setFacing(sight.getOpposite());
            
            if (sight == ForgeDirection.UP && te instanceof TileSolarPanel)
            	sight = ForgeDirection.DOWN;
            
            ((SESinglePortMachine) te).setFunctionalSide(sight);
        }
    }
}
