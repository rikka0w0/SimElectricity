package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public final class GuiDiode extends SEGuiContainer<ContainerDiode> {
    public GuiDiode(ContainerDiode screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);

        int ybase = 22;
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.voltage_input"), 85, ybase, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getVoltageStringWithUnit(this.container.inputVoltage), 85, ybase + 8, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.voltage_output"), 85, ybase + 16, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getVoltageStringWithUnit(this.container.outputVoltage), 85, ybase + 24, 4210752);

        this.font.draw(matrixStack, I18n.get(this.container.inputVoltage > this.container.outputVoltage ?
                "gui.simelectricity.forward_biased" :
                "gui.simelectricity.reverse_biased"
        ), 85, ybase + 32, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int xMouse, int yMouse) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/diode.png");
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.directionSelector.set(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void init() {
        super.init();

        this.directionSelector = addDirectionSelector(this.width / 2 - 10, this.topPos + 100);
    }
}