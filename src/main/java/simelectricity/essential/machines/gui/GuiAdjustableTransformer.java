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
public final class GuiAdjustableTransformer extends SEGuiContainer<ContainerAdjustableTransformer> {
	private static final ResourceLocation bgTexture =
			new ResourceLocation(Essential.MODID, "textures/gui/adjustable_transformer.png");

    public GuiAdjustableTransformer(ContainerAdjustableTransformer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);

        this.font.draw(matrixStack, I18n.get("gui.simelectricity.ratio_step_up"), 18, 85, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.resistance_secondary"), 18, 124, 4210752);

        this.font.draw(matrixStack, "1:" + String.format("%.1f", this.container.ratio), 74, 22, 4210752);
        this.font.draw(matrixStack, String.format("%.3f", this.container.outputResistance) + " \u03a9", 100, 56, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getVoltageStringWithUnit(this.container.vPri), 16, 30, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getVoltageStringWithUnit(this.container.vSec), 110, 30, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, bgTexture);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        blit(matrixStack, this.leftPos + 74, this.topPos + 32, 176, 0, 28, 48);

        this.directionSelector.set(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void init() {
        super.init();


        int xbase = 18;
        int ybase = 97;

        addServerButton(0, this.leftPos + xbase, this.topPos + ybase, 20, 20, "-1");
        addServerButton(1, this.leftPos + xbase + 20, this.topPos + ybase, 20, 20, "-.1");
        addServerButton(2, this.leftPos + xbase + 40, this.topPos + ybase, 30, 20, "-.01");
        addServerButton(3, this.leftPos + xbase + 70, this.topPos + ybase, 30, 20, "+.01");
        addServerButton(4, this.leftPos + xbase + 100, this.topPos + ybase, 20, 20, "+.1");
        addServerButton(5, this.leftPos + xbase + 120, this.topPos + ybase, 20, 20, "+1");

        addServerButton(6, this.leftPos + xbase, this.topPos + ybase + 38, 20, 20, "-1");
        addServerButton(7, this.leftPos + xbase + 20, this.topPos + ybase + 38, 20, 20, "-.1");
        addServerButton(8, this.leftPos + xbase + 40, this.topPos + ybase + 38, 30, 20, "-.01");
        addServerButton(9, this.leftPos + xbase + 70, this.topPos + ybase + 38, 30, 20, "+.01");
        addServerButton(10, this.leftPos + xbase + 100, this.topPos + ybase + 38, 20, 20, "+.1");
        addServerButton(11, this.leftPos + xbase + 120, this.topPos + ybase + 38, 20, 20, "+1");

        this.directionSelector = addDirectionSelector(this.leftPos + 24, this.topPos + 52);
    }
}
