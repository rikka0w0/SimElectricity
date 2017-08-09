package simelectricity.essential.client;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISESidedTextureBlock {
	@SideOnly(Side.CLIENT)
	String getModelNameFrom(IBlockState blockState);
}
