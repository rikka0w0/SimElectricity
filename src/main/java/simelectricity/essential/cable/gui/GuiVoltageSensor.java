package simelectricity.essential.cable.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

public class GuiVoltageSensor extends SEGuiContainer<ContainerVoltageSensor>{
	public GuiVoltageSensor(Container container) {
		super(container);
	}

	@Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(I18n.translateToLocal("item.sime_essential:essential_item.voltagesensor.name"), 8, 6, 4210752);
        
        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:voltage_threshold"), 18, 124, 4210752);
        
        int ybase = 22;
        fontRendererObj.drawString(SEUnitHelper.getVoltageStringWithUnit(container.thresholdVoltage), 20, 51, 4210752);
	}
	
    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/voltage_sensor.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
       
        this.drawTexturedModalRect(guiLeft+70, guiTop+30, container.inverted ? 52 : 0, 166, 52, 33);
        this.drawTexturedModalRect(guiLeft+130, guiTop+36, container.emitRedstoneSignal ? 180 : 176, 0, 4, 16);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        int xbase = 18;
        int ybase = 97;
        
        buttonList.add(new GuiButton(6, guiLeft + xbase, 		guiTop + ybase, 140, 20, I18n.translateToLocal("gui.sime_essential:redstone_toggle_behavior")));
        
        buttonList.add(new GuiButton(0, guiLeft + xbase, 		guiTop + ybase + 38, 30, 20, "-100"));
        buttonList.add(new GuiButton(1, guiLeft + xbase + 30, 	guiTop + ybase + 38, 20, 20, "-10"));
        buttonList.add(new GuiButton(2, guiLeft + xbase + 50, 	guiTop + ybase + 38, 20, 20, "-1"));
        buttonList.add(new GuiButton(3, guiLeft + xbase + 70, 	guiTop + ybase + 38, 20, 20, "+1"));
        buttonList.add(new GuiButton(4, guiLeft + xbase + 90, 	guiTop + ybase + 38, 20, 20, "+10"));
        buttonList.add(new GuiButton(5, guiLeft + xbase + 110, 	guiTop + ybase + 38, 30, 20, "+100"));
    }
}
