package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;

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

    public GuiSwitch(ContainerSwitch screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);

        this.font.draw(matrixStack, I18n.get("gui.simelectricity.current_trip"), 18, 85, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.resistance_internal"), 18, 124, 4210752);

        int ybase = 22;
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.current_trip"), 10, ybase, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getCurrentStringWithUnit(this.container.maxCurrent), 10, ybase + 8, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.current"), 10, ybase + 16, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getCurrentStringWithUnit(this.container.current), 10, ybase + 24, 4210752);
        this.font.draw(matrixStack, "Ron = " + String.format("%.3f", this.container.resistance) + " \u03a9", 10, ybase + 32, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/switch.png");
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        blit(matrixStack, this.leftPos + GuiSwitch.switchX, this.topPos + GuiSwitch.switchY, this.container.isOn ? 208 : 176, 0, 32, 32);

        this.directionSelector.set(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void init() {
        super.init();

        int xbase = 18;
        int ybase = 97;

        addServerButton(0, this.leftPos + xbase, this.topPos + ybase, 30, 20, "-100");
        addServerButton(1, this.leftPos + xbase + 30, this.topPos + ybase, 20, 20, "-10");
        addServerButton(2, this.leftPos + xbase + 50, this.topPos + ybase, 20, 20, "-1");
        addServerButton(3, this.leftPos + xbase + 70, this.topPos + ybase, 20, 20, "+1");
        addServerButton(4, this.leftPos + xbase + 90, this.topPos + ybase, 20, 20, "+10");
        addServerButton(5, this.leftPos + xbase + 110, this.topPos + ybase, 30, 20, "+100");

        addServerButton(6, this.leftPos + xbase, this.topPos + ybase + 38, 20, 20, "-1");
        addServerButton(7, this.leftPos + xbase + 20, this.topPos + ybase + 38, 20, 20, "-.1");
        addServerButton(8, this.leftPos + xbase + 40, this.topPos + ybase + 38, 30, 20, "-.01");
        addServerButton(9, this.leftPos + xbase + 70, this.topPos + ybase + 38, 30, 20, "+.01");
        addServerButton(10, this.leftPos + xbase + 100, this.topPos + ybase + 38, 20, 20, "+.1");
        addServerButton(11, this.leftPos + xbase + 120, this.topPos + ybase + 38, 20, 20, "+1");

        this.directionSelector = addDirectionSelector(this.leftPos + 116, this.topPos + 20);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean ret = super.mouseClicked(x, y, button);

        if (x >= this.leftPos + GuiSwitch.switchX && y >= this.topPos + GuiSwitch.switchY && x < this.leftPos + GuiSwitch.switchX + GuiSwitch.switchSize && y < this.topPos + GuiSwitch.switchY + GuiSwitch.switchSize)
            MessageContainerSync.sendButtonClickEventToSever(this.container, 12, Screen.hasControlDown());
        
        return ret;
    }
}
