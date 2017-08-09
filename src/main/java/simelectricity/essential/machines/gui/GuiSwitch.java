package simelectricity.essential.machines.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.Utils;
import simelectricity.essential.utils.client.GuiDirectionSelector;
import simelectricity.essential.utils.client.SEGuiContainer;
import simelectricity.essential.utils.network.MessageContainerSync;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiSwitch extends SEGuiContainer<ContainerSwitch>{
	public GuiSwitch(Container container) {
		super(container);
	}
	
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(I18n.translateToLocal("tile.sime_essential:essential_two_port_electronics.switch.name"), 8, 6, 4210752);
        
        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:current_trip"), 18, 85, 4210752);
        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:resistance_internal"), 18, 124, 4210752);
        
        int ybase = 22;
        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:current_trip"), 10, ybase, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getCurrentStringWithUnit(container.maxCurrent), 10, ybase+8, 4210752);
        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:current"), 10, ybase+16, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getCurrentStringWithUnit(container.current), 10, ybase+24, 4210752);
        fontRendererObj.drawString("Ron = " + String.format("%.3f", container.resistance) + " \u03a9", 10, ybase+32, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/switch.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
        this.drawTexturedModalRect(guiLeft+switchX, guiTop+switchY, container.isOn ? 208 : 176, 0, 32, 32);
        
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
    
    ////////////////////////
    /// Switch
    ////////////////////////
    private static int switchSize = 32;
    private static int switchX = 115;
    private static int switchY = 48;
    @Override
    public void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        
        if (x >= guiLeft+switchX && y >= guiTop+switchY && x < guiLeft+switchX+switchSize && y < guiTop+switchY+switchSize)
        	MessageContainerSync.sendButtonClickEventToSever(container, 12, GuiScreen.isCtrlKeyDown());
    }
}
