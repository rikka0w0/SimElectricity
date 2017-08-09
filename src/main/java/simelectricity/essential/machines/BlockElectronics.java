package simelectricity.essential.machines;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import simelectricity.essential.Essential;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEMachineBlock;
import simelectricity.essential.common.SESinglePortMachine;
import simelectricity.essential.machines.render.ISESidedTextureBlock;
import simelectricity.essential.machines.tile.TileAdjustableResistor;
import simelectricity.essential.machines.tile.TileIncandescentLamp;
import simelectricity.essential.machines.tile.TileQuantumGenerator;
import simelectricity.essential.machines.tile.TileSolarPanel;
import simelectricity.essential.machines.tile.TileVoltageMeter;
import simelectricity.essential.utils.Utils;

public class BlockElectronics extends SEMachineBlock implements ITileEntityProvider, ISESubBlock, ISESidedTextureBlock{
	public static String subNames[] = new String[]{"voltage_meter", "quantum_generator", "adjustable_resistor", "incandescent_lamp", "solar_panel"};
	///////////////////////////////
	///Block Properties
	///////////////////////////////
	public BlockElectronics() {
		super("essential_electronics", subNames);
	}
	
	@Override
	protected int getNumOfSubTypes() {
		return subNames.length;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getModelNameFrom(IBlockState blockState) {
		int meta = blockState.getValue(this.propertyMeta);
		return "electronics_"+subNames[meta];
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
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        
        if (te instanceof TileIncandescentLamp){
        	return ((TileIncandescentLamp) te).lightLevel;
        }
        return 0;
    }
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if (player.isSneaking())
            return false;

        int meta = state.getBlock().getMetaFromState(state);
        if (meta == 3 || meta == 4)
        	return false;	//Incandescent Lamp doesn't have an Gui!
        
        //When openGui() is call on the server side, Forge seems automatically send a packet to client side
        //in order to notify the client to set up the container and show the Gui.
        if (!world.isRemote)
        	player.openGui(Essential.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
	}
	
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        
        if (te instanceof SESinglePortMachine){
            EnumFacing sight = Utils.getPlayerSight(placer);
            ((SESinglePortMachine) te).setFacing(sight.getOpposite());
            
            if (sight == EnumFacing.UP && te instanceof TileSolarPanel)
            	sight = EnumFacing.DOWN;
            
            ((SESinglePortMachine) te).SetFunctionalSide(sight);
        }
    }
}
