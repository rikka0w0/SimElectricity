package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public final class GuiQuantumGenerator extends SEGuiContainer<ContainerQuantumGenerator> {
    public GuiQuantumGenerator(ContainerQuantumGenerator screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.drawString(matrixStack, title.getString(), 8, 6, 4210752);

        this.font.drawString(matrixStack, String.format("%.0f", this.container.internalVoltage) + " V", 30, 46, 4210752);
        this.font.drawString(matrixStack, String.format("%.3f", this.container.resistance) + " \u03a9", 30, 24, 4210752);

        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.resistance_internal"), 18, 85, 4210752);
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.voltage_internal"), 18, 124, 4210752);
        //gui.sime_essential:redstone_behavior_inverted
        int ybase = 22;
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.voltage_output"), 85, ybase, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 85, ybase + 8, 4210752);
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.current_output"), 85, ybase + 16, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getCurrentStringWithUnit(this.container.current), 85, ybase + 24, 4210752);
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.power_output"), 85, ybase + 32, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getPowerStringWithUnit(this.container.voltage * this.container.current), 85, ybase + 40, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/quantum_generator.png");
        blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void init() {
        super.init();
        int xbase = 18;
        int ybase = 97;

        addServerButton(0, this.guiLeft + xbase, this.guiTop + ybase, 20, 20, "-1");
        addServerButton(1, this.guiLeft + xbase + 20, this.guiTop + ybase, 20, 20, "-.1");
        addServerButton(2, this.guiLeft + xbase + 40, this.guiTop + ybase, 30, 20, "-.01");
        addServerButton(3, this.guiLeft + xbase + 70, this.guiTop + ybase, 30, 20, "+.01");
        addServerButton(4, this.guiLeft + xbase + 100, this.guiTop + ybase, 20, 20, "+.1");
        addServerButton(5, this.guiLeft + xbase + 120, this.guiTop + ybase, 20, 20, "+1");

        addServerButton(6, this.guiLeft + xbase, this.guiTop + ybase + 38, 30, 20, "-100");
        addServerButton(7, this.guiLeft + xbase + 30, this.guiTop + ybase + 38, 20, 20, "-10");
        addServerButton(8, this.guiLeft + xbase + 50, this.guiTop + ybase + 38, 20, 20, "-1");
        addServerButton(9, this.guiLeft + xbase + 70, this.guiTop + ybase + 38, 20, 20, "+1");
        addServerButton(10, this.guiLeft + xbase + 90, this.guiTop + ybase + 38, 20, 20, "+10");
        addServerButton(11, this.guiLeft + xbase + 110, this.guiTop + ybase + 38, 30, 20, "+100");
    }
}
