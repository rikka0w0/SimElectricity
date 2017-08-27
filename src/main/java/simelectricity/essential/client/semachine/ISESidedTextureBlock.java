package simelectricity.essential.client.semachine;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISESidedTextureBlock {
    @SideOnly(Side.CLIENT)
    boolean hasSecondState(IBlockState state);

    @SideOnly(Side.CLIENT)
    String getModelNameFrom(IBlockState blockState);
}
