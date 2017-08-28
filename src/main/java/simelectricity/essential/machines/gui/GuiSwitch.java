package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;

import org.lwjgl.opengl.GL11;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.GuiDirectionSelector;
import simelectricity.essential.utils.client.gui.SEGuiContainer;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public final class GuiSwitch extends SEGuiContainer<ContainerSwitch> {
    ////////////////////////
    /// Switch
    ////////////////////////
    private static final int switchSize = 32;
    private static final int switchX = 115;
    private static final int switchY = 48;

    public GuiSwitch(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.fontRenderer.drawString(I18n.translateToLocal("tile.sime_essential:essential_two_port_electronics.switch.name"), 8, 6, 4210752);

        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:current_trip"), 18, 85, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:resistance_internal"), 18, 124, 4210752);

        int ybase = 22;
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:current_trip"), 10, ybase, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getCurrentStringWithUnit(this.container.maxCurrent), 10, ybase + 8, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:current"), 10, ybase + 16, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getCurrentStringWithUnit(this.container.current), 10, ybase + 24, 4210752);
        this.fontRenderer.drawString("Ron = " + String.format("%.3f", this.container.resistance) + " \u03a9", 10, ybase + 32, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/switch.png"));
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        drawTexturedModalRect(this.guiLeft + GuiSwitch.switchX, this.guiTop + GuiSwitch.switchY, this.container.isOn ? 208 : 176, 0, 32, 32);

        this.directionSelector.draw(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void initGui() {
        super.initGui();

        int xbase = 18;
        int ybase = 97;

        this.buttonList.add(new GuiButton(0, this.guiLeft + xbase, this.guiTop + ybase, 30, 20, "-100"));
        this.buttonList.add(new GuiButton(1, this.guiLeft + xbase + 30, this.guiTop + ybase, 20, 20, "-10"));
        this.buttonList.add(new GuiButton(2, this.guiLeft + xbase + 50, this.guiTop + ybase, 20, 20, "-1"));
        this.buttonList.add(new GuiButton(3, this.guiLeft + xbase + 70, this.guiTop + ybase, 20, 20, "+1"));
        this.buttonList.add(new GuiButton(4, this.guiLeft + xbase + 90, this.guiTop + ybase, 20, 20, "+10"));
        this.buttonList.add(new GuiButton(5, this.guiLeft + xbase + 110, this.guiTop + ybase, 30, 20, "+100"));

        this.buttonList.add(new GuiButton(6, this.guiLeft + xbase, this.guiTop + ybase + 38, 20, 20, "-1"));
        this.buttonList.add(new GuiButton(7, this.guiLeft + xbase + 20, this.guiTop + ybase + 38, 20, 20, "-.1"));
        this.buttonList.add(new GuiButton(8, this.guiLeft + xbase + 40, this.guiTop + ybase + 38, 30, 20, "-.01"));
        this.buttonList.add(new GuiButton(9, this.guiLeft + xbase + 70, this.guiTop + ybase + 38, 30, 20, "+.01"));
        this.buttonList.add(new GuiButton(10, this.guiLeft + xbase + 100, this.guiTop + ybase + 38, 20, 20, "+.1"));
        this.buttonList.add(new GuiButton(11, this.guiLeft + xbase + 120, this.guiTop + ybase + 38, 20, 20, "+1"));

        this.directionSelector = new GuiDirectionSelector(this.guiLeft + 116, this.guiTop + 20,
                Utils.getPlayerSightHorizontal(Essential.proxy.getClientPlayer())
        );
    }

    @Override
    public void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        if (x >= this.guiLeft + GuiSwitch.switchX && y >= this.guiTop + GuiSwitch.switchY && x < this.guiLeft + GuiSwitch.switchX + GuiSwitch.switchSize && y < this.guiTop + GuiSwitch.switchY + GuiSwitch.switchSize)
            MessageContainerSync.sendButtonClickEventToSever(this.container, 12, GuiScreen.isCtrlKeyDown());
    }
}
