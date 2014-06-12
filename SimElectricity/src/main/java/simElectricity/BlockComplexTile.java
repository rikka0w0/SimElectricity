package simElectricity;

import simElectricity.API.Util;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockComplexTile extends BlockContainer{

	
	public BlockComplexTile() {
		super(Material.rock);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("sime:ComplexTile");
		setCreativeTab(Util.SETab);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new ComplexTile();
	}

}
