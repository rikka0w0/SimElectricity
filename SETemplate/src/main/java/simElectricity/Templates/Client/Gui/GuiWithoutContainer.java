package simElectricity.Templates.Client.Gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiWithoutContainer extends GuiScreen{
    /** The X size of the inventory window in pixels. */
    protected int xSize = 176;
    /** The Y size of the inventory window in pixels. */
    protected int ySize = 166;
    /** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int guiLeft;
    /** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
    protected int guiTop;
	
	@Override
    public void initGui() 
    {
		super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }
	
	@Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
	
	@Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
        {
            this.mc.thePlayer.closeScreen();
        }
    }
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float opacity)
    {
		this.drawDefaultBackground();
        this.drawGuiContainerBackgroundLayer(opacity, mouseX, mouseY);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.drawScreen(mouseX, mouseY, opacity);
        RenderHelper.enableGUIStandardItemLighting();
        
        GL11.glPushMatrix();
        GL11.glTranslatef(this.guiLeft, this.guiTop, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        short short1 = 240;
        short short2 = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int k1;

        //Forge: Force lighting to be disabled as there are some issue where lighting would
        //incorrectly be applied based on items that are in the inventory.
        GL11.glDisable(GL11.GL_LIGHTING);
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
    }
	
	@Override
    public void onGuiClosed()
    {
		
    }
	
	/**
	 * draw text and stuff here
     * the parameters for drawString are: string, x, y, color
	 * @param x
	 * @param y
	 */
    protected abstract void drawGuiContainerForegroundLayer(int mouseX, int mouseY);

    /**
     * draw your Gui here, only thing you need to change is the path
     * @param opacity
     * @param mouseX
     * @param mouseY
     */
    protected abstract void drawGuiContainerBackgroundLayer(float opacity, int mouseX, int mouseY);
}
