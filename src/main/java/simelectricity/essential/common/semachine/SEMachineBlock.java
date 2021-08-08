package simelectricity.essential.common.semachine;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import rikka.librikka.Utils;
import rikka.librikka.block.BlockBase;
import simelectricity.api.SEAPI;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.common.CoverPanelUtils;

import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class SEMachineBlock extends BlockBase implements EntityBlock {
    public SEMachineBlock(String unlocalizedName) {
        super(unlocalizedName,
        		BlockBehaviour.Properties.of(Material.METAL)
        		.sound(SoundType.METAL)
        		.strength(3.0F, 10.0F)
        		, SEAPI.SETab);
    }

    public abstract boolean hasSecondState();

    public abstract boolean useObjModel();

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);

		if (hasSecondState())
			builder.add(BlockStateProperties.POWERED);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		return state.setValue(BlockStateProperties.FACING, rot.rotate(facing));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction sight = Utils.getPlayerSight(context.getPlayer());
		BlockState bs = this.defaultBlockState();
		bs = bs.setValue(BlockStateProperties.FACING, sight.getOpposite());

		if (this.getStateDefinition().getProperties().contains(BlockStateProperties.POWERED))
			bs = bs.setValue(BlockStateProperties.POWERED, false);

		return bs;
	}

    ///////////////////////////////
    /// CoverPanelHandler
    ///////////////////////////////
    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof ISECoverPanelHost))
			return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);

    	if (CoverPanelUtils.removeCoverPanel((ISECoverPanelHost)te, player))
			return false;

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
}
