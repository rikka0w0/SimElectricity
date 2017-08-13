package simelectricity.essential.api.client;

import java.util.List;

import simelectricity.essential.api.coverpanel.ISECoverPanel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISECoverPanelRender<TYPE extends ISECoverPanel> {
	@SideOnly(Side.CLIENT)
	void renderCoverPanel(TYPE coverPanel, EnumFacing side, List<BakedQuad> quads);
}
