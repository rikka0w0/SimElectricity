package simelectricity.essential.client.coverpanel;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;

@OnlyIn(Dist.CLIENT)
public class BlockColorHandler implements BlockColor {
    public final static BlockColor colorHandler = new BlockColorHandler();

    @Override
    public int getColor(BlockState blockState, BlockAndTintGetter world, BlockPos pos, int tintIndex) {
        if (world != null && pos != null) {
            BlockEntity te = world.getBlockEntity(pos);
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
