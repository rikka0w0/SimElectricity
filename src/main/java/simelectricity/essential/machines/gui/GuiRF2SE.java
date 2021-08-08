package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import simelectricity.essential.Essential;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public final class GuiRF2SE extends SEGuiContainer<ContainerRF2SE> {
	private static final ResourceLocation bgTexture =
			new ResourceLocation(Essential.MODID, "textures/gui/se2rf.png");

    public GuiRF2SE(ContainerRF2SE screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.draw(matrixStack, title.getString(), 8, 6, 4210752);

        this.font.draw(matrixStack, I18n.get("gui.simelectricity.voltage_output"), 8, 22, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 8, 30, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.power_output"), 8, 38, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getPowerStringWithUnit(this.container.actualOutputPower), 8, 46, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.buffered_energy"), 8, 54, 4210752);
        this.font.draw(matrixStack, this.container.bufferedEnergy + "RF", 8, 62, 4210752);
//        this.font.drawString(matrixStack, I18n.format("gui.sime_essential:rf_demand"), 8, 70, 4210752);
//        this.font.drawString(matrixStack, this.container.rfDemandRateDisplay + "RF", 8, 78, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.sime_essential.rf_power"), 8, 86, 4210752);
        this.font.draw(matrixStack, this.container.rfInputRateDisplay + "RF", 8, 94, 4210752);
//
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.power_rated") + ": " +
                SEUnitHelper.getPowerStringWithUnit(this.container.ratedOutputPower), 18, 124, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, bgTexture);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void init() {
        super.init();
        int xbase = 18;
        int ybase = 97;

        addServerButton(0, this.leftPos + xbase, this.topPos + ybase + 38, 30, 20, "-100");
        addServerButton(1, this.leftPos + xbase + 30, this.topPos + ybase + 38, 20, 20, "-10");
        addServerButton(2, this.leftPos + xbase + 50, this.topPos + ybase + 38, 20, 20, "-1");
        addServerButton(3, this.leftPos + xbase + 70, this.topPos + ybase + 38, 20, 20, "+1");
        addServerButton(4, this.leftPos + xbase + 90, this.topPos + ybase + 38, 20, 20, "+10");
        addServerButton(5, this.leftPos + xbase + 110, this.topPos + ybase + 38, 30, 20, "+100");
    }
}
