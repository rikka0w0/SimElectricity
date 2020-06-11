package simelectricity.essential.client.coverpanel;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;

@OnlyIn(Dist.CLIENT)
public class BlockColorHandler implements IBlockColor {
    public final static IBlockColor colorHandler = new BlockColorHandler();

    @Override
    public int getColor(BlockState blockState, IEnviromentBlockReader world, BlockPos pos, int tintIndex) {
        if (world != null && pos != null) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof ISECoverPanelHost) {
                ISECoverPanelHost cable = (ISECoverPanelHost)te;

                Direction side = GenericFacadeRender.getFacing(tintIndex);
                ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);

                if (coverPanel instanceof ISEFacadeCoverPanel) {
                	ISEFacadeCoverPanel bcFacade = (ISEFacadeCoverPanel) coverPanel;
                    BlockState state = bcFacade.getBlockState();
                    tintIndex = GenericFacadeRender.getTint(tintIndex);
                    return Minecraft.getInstance().getBlockColors().getColor(state, world, pos, tintIndex);
                }

                return -1;
            }
        }

        return -1;
    }
}
