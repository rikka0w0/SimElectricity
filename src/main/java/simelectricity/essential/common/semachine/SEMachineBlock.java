package simelectricity.essential.common.semachine;

import java.util.ArrayList;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.ISESubBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.common.SEMetaBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class SEMachineBlock extends SEMetaBlock implements ITileEntityProvider, ISESubBlock{
	protected final String[] subNames;	
	
	public SEMachineBlock(String unlocalizedName, String[] subNames) {
		super(unlocalizedName, Material.ROCK, SEItemBlock.class);
		
		this.subNames = new String[subNames.length];
		for (int i=0; i<subNames.length; i++)
			this.subNames[i] = subNames[i];
	}
	
	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
	    return new ItemStack(itemBlock, 1, this.getMetaFromState(world.getBlockState(pos)));
	}
	
	@Override
	public String[] getSubBlockUnlocalizedNames() {
		return subNames;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	protected abstract boolean isSecondState(TileEntity te);
	
	@Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
		//Need this otherwise sockets won't be rendered correctly
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
	///////////////////////////////
	///BlockStates
	///////////////////////////////
	@Override
	protected void createUnlistedProperties(ArrayList<IUnlistedProperty> properties){
		properties.add(ExtendedProperties.propertyFacing);
		properties.add(ExtendedProperties.propertIs2State);
		
		properties.add(ExtendedProperties.propertyDownSocket);
		properties.add(ExtendedProperties.propertyUpSocket);
		properties.add(ExtendedProperties.propertyNorthSocket);
		properties.add(ExtendedProperties.propertySouthSocket);
		properties.add(ExtendedProperties.propertyWestSocket);
		properties.add(ExtendedProperties.propertyEastSocket);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState retval = (IExtendedBlockState)state;
			
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof ISidedFacing){
				EnumFacing facing = ((ISidedFacing) te).getFacing();
				retval = retval.withProperty(ExtendedProperties.propertyFacing, facing);
			}
			
			retval = retval.withProperty(ExtendedProperties.propertIs2State, isSecondState(te));
			
			if (te instanceof ISESocketProvider){
				for (EnumFacing facing: EnumFacing.VALUES){
					int socketIconIndex = ((ISESocketProvider) te).getSocketIconIndex(facing);
					socketIconIndex++; //Shift the range, so 0 becomes no icon
					IUnlistedProperty<Integer> prop = ExtendedProperties.propertySockets[facing.ordinal()];
					retval = retval.withProperty(prop, socketIconIndex);
				}
			}
			
			return retval;
		}
		return state;
	}
}
