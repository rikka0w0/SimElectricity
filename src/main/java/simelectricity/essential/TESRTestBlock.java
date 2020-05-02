package simelectricity.essential;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import rikka.librikka.block.BlockBase;
import rikka.librikka.tileentity.TileEntityBase;
import simelectricity.api.SEAPI;

public class TESRTestBlock extends BlockBase{
	public TESRTestBlock() {
		super("tesrtest", Properties.create(Material.ROCK).hardnessAndResistance(1), SEAPI.SETab);
	}
	
	@Override
	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	// render using a BakedModel (mbe01_block_simple.json -->
	// mbe01_block_simple_model.json)
	// not strictly required because the default (super method) is MODEL.
	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.INVISIBLE;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new Tile();
	}
	
	public static class Tile extends TileEntityBase {
		public Tile() {
			super(Essential.MODID);
		}
	}
}
