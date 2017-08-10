package simelectricity.essential.machines.gui;

import org.lwjgl.opengl.GL11;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

@SideOnly(Side.CLIENT)
public final class GuiVoltageMeter extends SEGuiContainer<ContainerVoltageMeter>{	
	public GuiVoltageMeter(Container container) {
		super(container);
	}

	private int sqr;
	
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(I18n.translateToLocal("tile.sime_essential:essential_electronics.voltage_meter.name"), 8, 6, 4210752);
        fontRendererObj.drawString("Voltage: " + SEUnitHelper.getVoltageStringWithUnit(container.voltage), 18, 22, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(I18n.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
        fontRendererObj.drawString("x10^" + String.valueOf(sqr), xSize - 38, ySize - 96, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/voltage_meter.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        
        float v = (float) container.voltage;
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

        this.drawTexturedModalRect(x + 20 + nx, y + 36, center - 68 + nx, 166, 135 - nx - mx, 24);

        //Draw the pointer
        this.drawTexturedModalRect(x + 88, y + 56, 0, 190, 1, 4);
    }

}
