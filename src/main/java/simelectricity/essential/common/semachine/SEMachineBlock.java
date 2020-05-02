package simelectricity.essential.common.semachine;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.IBlockReader;
import rikka.librikka.Utils;
import rikka.librikka.block.BlockBase;
import simelectricity.api.SEAPI;

public abstract class SEMachineBlock extends BlockBase{
    public SEMachineBlock(String unlocalizedName) {
        super(unlocalizedName, 
        		Block.Properties.create(Material.IRON)
        		.sound(SoundType.METAL)
        		.hardnessAndResistance(3.0F, 10.0F)
        		, SEAPI.SETab);
    }

    public abstract boolean hasSecondState();
    @Deprecated
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
}
