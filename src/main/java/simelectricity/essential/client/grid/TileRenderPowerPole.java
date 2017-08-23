package simelectricity.essential.client.grid;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.grid.PowerPoleRenderHelper.ConnectionInfo;
import simelectricity.essential.client.grid.TransmissionLineGLRender.ITextureProvider;
import simelectricity.essential.utils.math.Vec3f;

@SideOnly(Side.CLIENT)
public class TileRenderPowerPole extends TileEntitySpecialRenderer implements ITextureProvider{	
	@Override
    public boolean isGlobalRenderer(TileEntity te) {return true;}
    
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
		PowerPoleRenderHelper helper = ((ISEPowerPole) tileEntity).getRenderHelper();
		
		if (helper == null)
			return;
		
        GL11.glPushMatrix();
        GL11.glTranslated(x-helper.pos.getX(), y-helper.pos.getY(), z-helper.pos.getZ());
		for (ConnectionInfo[] connections: helper.connectionInfo) {
			for (ConnectionInfo info: connections) {
				TransmissionLineGLRender.renderParabolicCable(info.fixedFrom, info.fixedTo, true, 0.075F, info.tension, this, 1);
			}
		}
		
		for (Pair<Vec3f, Vec3f> wire: helper.extraWires) {
			TransmissionLineGLRender.renderParabolicCable(wire.getLeft(), wire.getRight(), false, 0.075F, 1.5F, this, 1);
		}
		GL11.glPopMatrix();
	}
	
	@Override
	public void bindTexture(int index, int side) {
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("sime_essential", "textures/render/transmission/hv_cable.png"));
	}
}
