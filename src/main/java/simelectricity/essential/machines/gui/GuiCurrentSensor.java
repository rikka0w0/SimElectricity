package simelectricity.essential.machines.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.Utils;
import simelectricity.essential.utils.client.GuiDirectionSelector;
import simelectricity.essential.utils.client.SEGuiContainer;

public class GuiCurrentSensor extends SEGuiContainer<ContainerCurrentSensor>{
	public GuiCurrentSensor(Container container) {
		super(container);
	}

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime_essential:essential_two_port_electronics.current_sensor.name"), 8, 6, 4210752);
        
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:current_threshold"), 18, 85, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:resistance_internal"), 18, 124, 4210752);
        
        int ybase = 22;
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:current_threshold"), 10, ybase, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getCurrentStringWithUnit(container.thresholdCurrent), 10, ybase+8, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:current"), 10, ybase+16, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getCurrentStringWithUnit(container.current), 10, ybase+24, 4210752);
        fontRendererObj.drawString("Ron = " + String.format("%.3f", container.resistance) + " \u03a9", 10, ybase+32, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/current_sensor.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
        this.drawTexturedModalRect(guiLeft+130, guiTop+36, container.emitRedstoneSignal ? 180 : 176, 0, 4, 16);
        
        directionSelector.draw(container.inputSide, container.outputSide);
    }
	
    @Override
    public void initGui() {
        super.initGui();
        
        int xbase = 18;
        int ybase = 97;
        
        buttonList.add(new GuiButton(0, guiLeft + xbase, 		guiTop + ybase, 30, 20, "-100"));
        buttonList.add(new GuiButton(1, guiLeft + xbase + 30, 	guiTop + ybase, 20, 20, "-10"));
        buttonList.add(new GuiButton(2, guiLeft + xbase + 50, 	guiTop + ybase, 20, 20, "-1"));
        buttonList.add(new GuiButton(3, guiLeft + xbase + 70, 	guiTop + ybase, 20, 20, "+1"));
        buttonList.add(new GuiButton(4, guiLeft + xbase + 90, 	guiTop + ybase, 20, 20, "+10"));
        buttonList.add(new GuiButton(5, guiLeft + xbase + 110, 	guiTop + ybase, 30, 20, "+100"));
        
        buttonList.add(new GuiButton(6, guiLeft + xbase, 		guiTop + ybase + 38, 20, 20, "-1"));
        buttonList.add(new GuiButton(7, guiLeft + xbase + 20, 	guiTop + ybase + 38, 20, 20, "-.1"));
        buttonList.add(new GuiButton(8, guiLeft + xbase + 40, 	guiTop + ybase + 38, 30, 20, "-.01"));
        buttonList.add(new GuiButton(9, guiLeft + xbase + 70, 	guiTop + ybase + 38, 30, 20, "+.01"));
        buttonList.add(new GuiButton(10, guiLeft + xbase + 100, 	guiTop + ybase + 38, 20, 20, "+.1"));
        buttonList.add(new GuiButton(11, guiLeft + xbase + 120, 	guiTop + ybase + 38, 20, 20, "+1"));
        
        directionSelector = new GuiDirectionSelector(guiLeft + 116, guiTop + 20,
        		Utils.getPlayerSightHorizontal(Essential.proxy.getClientPlayer())
        		);
    }
}
