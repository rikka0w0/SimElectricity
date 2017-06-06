package simelectricity.Templates.Client.Gui;

import org.lwjgl.opengl.GL11;

import simelectricity.Templates.Utils.IGuiSyncHandler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiVoltageMeter2 extends GuiWithoutContainer implements IGuiSyncHandler{
    int sqr = 0;
    private double voltage;
	
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:VoltageMeter.name"), 8, 6, 4210752);
        fontRendererObj.drawString("Voltage: " + voltage + "V", 18, 22, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
        fontRendererObj.drawString("x10^" + String.valueOf(sqr), xSize - 38, ySize - 96, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float opacity, int mouseX, int mouseY){
    	//draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_VoltageMeter.png"));

        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

        float v = (float) voltage;
        if (v == 0)
            sqr = 0;
        else
            sqr = -1;
        while (v > 1) {
            v /= 10;
            sqr++;
        }
        v *= 10;

        //Display v on the meter... Hard coding......
        int center = (int) (v * 25 + 2);
        int nx = 0;
        if (center - 68 < 0)
            nx = 68 - center;

        int mx = 0;
        if (center + 68 - 255 > 0)
            mx = center + 68 - 255;

        this.drawTexturedModalRect(this.guiLeft + 20 + nx, this.guiTop + 36, center - 68 + nx, 166, 135 - nx - mx, 24);

        //Draw the pointer
        this.drawTexturedModalRect(this.guiLeft + 88, this.guiTop + 56, 0, 190, 1, 4);
    }

	@Override
	public void onGuiEvent(byte eventID, Object[] data) {
		// TODO Auto-generated method stub
		
	}
}
