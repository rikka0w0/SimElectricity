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
public final class GuiAdjustableResistor extends SEGuiContainer<ContainerAdjustableResistor> {
	private static final ResourceLocation bgTexture =
			ResourceLocation.fromNamespaceAndPath(Essential.MODID, "textures/gui/adjustable_resistor.png");

    public GuiAdjustableResistor(ContainerAdjustableResistor screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        guiGraphics.drawString(this.font, title.getString(), 8, 6, 4210752, false);

        guiGraphics.drawString(this.font, I18n.get("gui.simelectricity.buffered_energy"), 18, 85, 4210752, false);
        guiGraphics.drawString(this.font, SEUnitHelper.getEnergyStringInJ(this.container.bufferedEnergy), 18, 98, 4210752, false);
        guiGraphics.drawString(this.font, SEUnitHelper.getEnergyStringInKWh(this.container.bufferedEnergy), 18, 107, 4210752, false);
        guiGraphics.drawString(this.font, I18n.get("gui.simelectricity.resistance_internal"), 18, 124, 4210752, false);

        int ybase = 22;
        guiGraphics.drawString(this.font, I18n.get("gui.simelectricity.voltage_input"), 85, ybase, 4210752, false);
        guiGraphics.drawString(this.font, SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 85, ybase + 8, 4210752, false);
        guiGraphics.drawString(this.font, I18n.get("gui.simelectricity.current_input"), 85, ybase + 16, 4210752, false);
        guiGraphics.drawString(this.font, SEUnitHelper.getCurrentStringWithUnit(this.container.current), 85, ybase + 24, 4210752, false);
        guiGraphics.drawString(this.font, I18n.get("gui.simelectricity.power_input"), 85, ybase + 32, 4210752, false);
        guiGraphics.drawString(this.font, SEUnitHelper.getPowerStringWithUnit(this.container.powerLevel), 85, ybase + 40, 4210752, false);

        guiGraphics.drawString(this.font, String.format("%.1f", this.container.resistance) + " \u03a9", 26, 28, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, bgTexture);
        guiGraphics.blit(bgTexture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void init() {
        super.init();
        int xbase = 18;
        int ybase = 97;

        addServerButton(0, this.leftPos + xbase, this.topPos + ybase + 38, 30, 20, "-100");
        addServerButton(1, this.leftPos + xbase + 30, this.topPos + ybase + 38, 26, 20, "-10");
        addServerButton(2, this.leftPos + xbase + 56, this.topPos + ybase + 38, 20, 20, "-1");
        addServerButton(3, this.leftPos + xbase + 76, this.topPos + ybase + 38, 20, 20, "+1");
        addServerButton(4, this.leftPos + xbase + 96, this.topPos + ybase + 38, 26, 20, "+10");
        addServerButton(5, this.leftPos + xbase + 122, this.topPos + ybase + 38, 30, 20, "+100");

        addServerButton(6, this.leftPos + xbase + 100, this.topPos + ybase, 40, 20, "Clear");
    }
}
