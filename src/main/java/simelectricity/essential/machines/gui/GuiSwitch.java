package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;
import simelectricity.essential.utils.network.MessageContainerSync;

@OnlyIn(Dist.CLIENT)
public final class GuiSwitch extends SEGuiContainer<ContainerSwitch> {
    ////////////////////////
    /// Switch
    ////////////////////////
    private static final int switchSize = 32;
    private static final int switchX = 115;
    private static final int switchY = 48;

    public GuiSwitch(ContainerSwitch screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.drawString(matrixStack, this.title.getString(), 8, 6, 4210752);

        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.current_trip"), 18, 85, 4210752);
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.resistance_internal"), 18, 124, 4210752);

        int ybase = 22;
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.current_trip"), 10, ybase, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getCurrentStringWithUnit(this.container.maxCurrent), 10, ybase + 8, 4210752);
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.current"), 10, ybase + 16, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getCurrentStringWithUnit(this.container.current), 10, ybase + 24, 4210752);
        this.font.drawString(matrixStack, "Ron = " + String.format("%.3f", this.container.resistance) + " \u03a9", 10, ybase + 32, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/switch.png");
        blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        blit(matrixStack, this.guiLeft + GuiSwitch.switchX, this.guiTop + GuiSwitch.switchY, this.container.isOn ? 208 : 176, 0, 32, 32);

        this.directionSelector.set(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void init() {
        super.init();

        int xbase = 18;
        int ybase = 97;

        addServerButton(0, this.guiLeft + xbase, this.guiTop + ybase, 30, 20, "-100");
        addServerButton(1, this.guiLeft + xbase + 30, this.guiTop + ybase, 20, 20, "-10");
        addServerButton(2, this.guiLeft + xbase + 50, this.guiTop + ybase, 20, 20, "-1");
        addServerButton(3, this.guiLeft + xbase + 70, this.guiTop + ybase, 20, 20, "+1");
        addServerButton(4, this.guiLeft + xbase + 90, this.guiTop + ybase, 20, 20, "+10");
        addServerButton(5, this.guiLeft + xbase + 110, this.guiTop + ybase, 30, 20, "+100");

        addServerButton(6, this.guiLeft + xbase, this.guiTop + ybase + 38, 20, 20, "-1");
        addServerButton(7, this.guiLeft + xbase + 20, this.guiTop + ybase + 38, 20, 20, "-.1");
        addServerButton(8, this.guiLeft + xbase + 40, this.guiTop + ybase + 38, 30, 20, "-.01");
        addServerButton(9, this.guiLeft + xbase + 70, this.guiTop + ybase + 38, 30, 20, "+.01");
        addServerButton(10, this.guiLeft + xbase + 100, this.guiTop + ybase + 38, 20, 20, "+.1");
        addServerButton(11, this.guiLeft + xbase + 120, this.guiTop + ybase + 38, 20, 20, "+1");

        this.directionSelector = addDirectionSelector(this.guiLeft + 116, this.guiTop + 20);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean ret = super.mouseClicked(x, y, button);

        if (x >= this.guiLeft + GuiSwitch.switchX && y >= this.guiTop + GuiSwitch.switchY && x < this.guiLeft + GuiSwitch.switchX + GuiSwitch.switchSize && y < this.guiTop + GuiSwitch.switchY + GuiSwitch.switchSize)
            MessageContainerSync.sendButtonClickEventToSever(this.container, 12, Screen.hasControlDown());
        
        return ret;
    }
}
