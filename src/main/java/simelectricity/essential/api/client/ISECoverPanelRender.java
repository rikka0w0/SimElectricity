package simelectricity.essential.api.client;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.util.List;

public interface ISECoverPanelRender<TYPE extends ISECoverPanel> {
    @SideOnly(Side.CLIENT)
    void renderCoverPanel(TYPE coverPanel, EnumFacing side, List<BakedQuad> quads);
}
