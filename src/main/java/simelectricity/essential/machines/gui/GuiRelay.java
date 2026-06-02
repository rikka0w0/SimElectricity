package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

import simelectricity.essential.Essential;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public class GuiRelay extends SEGuiContainer<ContainerRelay> {
	private static final ResourceLocation bgTexture =
			ResourceLocation.fromNamespaceAndPath(Essential.MODID, "textures/gui/switch.png");

    private static final int switchX = 115;
    private static final int switchY = 48;

    public GuiRelay(ContainerRelay screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        guiGraphics.drawString(this.font, this.title.getString(), 8, 6, 4210752, false);

        guiGraphics.drawString(this.font, I18n.get("gui.simelectricity.resistance_internal"), 18, 124, 4210752, false);

        int ybase = 22;
        guiGraphics.drawString(this.font, I18n.get("gui.simelectricity.current"), 10, ybase + 16, 4210752, false);
        guiGraphics.drawString(this.font, SEUnitHelper.getCurrentStringWithUnit(this.container.current), 10, ybase + 24, 4210752, false);
        guiGraphics.drawString(this.font, "Ron = " + String.format("%.3f", this.container.resistance) + " \u03a9", 10, ybase + 32, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, bgTexture);
        guiGraphics.blit(bgTexture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        guiGraphics.blit(bgTexture, this.leftPos + GuiRelay.switchX, this.topPos + GuiRelay.switchY, this.container.isOn ? 208 : 176, 0, 32, 32);

        this.directionSelector.set(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void init() {
        super.init();

        int xbase = 18;
        int ybase = 97;

        addServerButton(0, this.leftPos + xbase, this.topPos + ybase + 38, 20, 20, "-1");
        addServerButton(1, this.leftPos + xbase + 20, this.topPos + ybase + 38, 20, 20, "-.1");
        addServerButton(2, this.leftPos + xbase + 40, this.topPos + ybase + 38, 30, 20, "-.01");
        addServerButton(3, this.leftPos + xbase + 70, this.topPos + ybase + 38, 30, 20, "+.01");
        addServerButton(4, this.leftPos + xbase + 100, this.topPos + ybase + 38, 20, 20, "+.1");
        addServerButton(5, this.leftPos + xbase + 120, this.topPos + ybase + 38, 20, 20, "+1");

        this.directionSelector = addDirectionSelector(this.leftPos + 116, this.topPos + 20);
    }
}
