package simelectricity.essential.api.client;

import java.util.List;

import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISECoverPanelRender<TYPE extends ISECoverPanel> {
	@SideOnly(Side.CLIENT)
	void renderCoverPanel(	IBlockAccess world, int x, int y, int z, List<BakedQuad> renderer, int renderPass,
							ISEGenericCable cable, TYPE coverPanel, EnumFacing side);
}
