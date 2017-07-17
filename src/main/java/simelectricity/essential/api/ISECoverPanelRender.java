package simelectricity.essential.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public interface ISECoverPanelRender<TYPE extends ISECoverPanel> {
	@SideOnly(Side.CLIENT)
	void renderCoverPanel(	IBlockAccess world, int x, int y, int z, RenderBlocks renderer, int renderPass,
							ISEGenericCable cable, TYPE coverPanel, ForgeDirection side);
}
