package simelectricity.essential.common;

import java.util.ArrayList;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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
	
	///////////////////////////////
	///BlockStates
	///////////////////////////////
	@Override
	protected void createUnlistedProperties(ArrayList<IUnlistedProperty> properties){
		properties.add(ExtendedProperties.propertyFacing);
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
			
			return retval;
		}
		return state;
	}
}
