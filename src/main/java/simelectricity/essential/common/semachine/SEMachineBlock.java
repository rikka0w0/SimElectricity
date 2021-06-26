package simelectricity.essential.common.semachine;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import rikka.librikka.Utils;
import rikka.librikka.block.BlockBase;
import simelectricity.api.SEAPI;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.common.CoverPanelUtils;

public abstract class SEMachineBlock extends BlockBase {
    public SEMachineBlock(String unlocalizedName) {
        super(unlocalizedName, 
        		Block.Properties.create(Material.IRON)
        		.sound(SoundType.METAL)
        		.hardnessAndResistance(3.0F, 10.0F)
        		, SEAPI.SETab);
    }

    public abstract boolean hasSecondState();

    public abstract boolean useObjModel();

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);
		
		if (hasSecondState())
			builder.add(BlockStateProperties.POWERED);
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		Direction facing = state.get(BlockStateProperties.FACING);
		return state.with(BlockStateProperties.FACING, rot.rotate(facing));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction sight = Utils.getPlayerSight(context.getPlayer());
		BlockState bs = this.getDefaultState();
		bs = bs.with(BlockStateProperties.FACING, sight.getOpposite());
		
		if (this.getStateContainer().getProperties().contains(BlockStateProperties.POWERED))
			bs = bs.with(BlockStateProperties.POWERED, false);
		
		return bs;
	}
    
    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(BlockState state) {return true;}
	
	@Override
	public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    ///////////////////////////////
    /// CoverPanelHandler
    ///////////////////////////////
    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof ISECoverPanelHost))
			return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    	
    	if (CoverPanelUtils.removeCoverPanel((ISECoverPanelHost)te, player))
			return false;
		
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
}
