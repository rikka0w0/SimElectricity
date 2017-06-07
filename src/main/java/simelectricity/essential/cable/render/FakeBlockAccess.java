package simelectricity.essential.cable.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISEGenericCable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class FakeBlockAccess implements IBlockAccess{
	private final IBlockAccess world;
	private final ForgeDirection side;

	public FakeBlockAccess(IBlockAccess world, ForgeDirection side) {
		this.world = world;
		this.side = side;
	}
	
	@Override
	public Block getBlock(int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof ISEGenericCable) {
			ISECoverPanel coverPanel = ((ISEGenericCable) tile).getCoverPanelOnSide(side);
			if (coverPanel != null)
				return coverPanel.getBlock();
		}
		return Blocks.air;
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		return null;
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int a) {
		return 0;
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof ISEGenericCable) {
			ISECoverPanel coverPanel = ((ISEGenericCable) tile).getCoverPanelOnSide(side);
			if (coverPanel != null)
				return coverPanel.getBlockMeta();
		}
		return 0;
	}

	@Override
	public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
		return 0;
	}

	@Override
	public boolean isAirBlock(int x, int y, int z) {
		return !(world.getTileEntity(x, y, z) instanceof ISEGenericCable);
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return world.getBiomeGenForCoords(x, z);
	}

	@Override
	public int getHeight() {
		return world.getHeight();
	}

	@Override
	public boolean extendedLevelsInChunkCache() {
		return world.extendedLevelsInChunkCache();
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean def) {
		return world.isSideSolid(x, y, z, side, def);
	}
}
