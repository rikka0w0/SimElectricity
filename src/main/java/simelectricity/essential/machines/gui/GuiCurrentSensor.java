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
public class GuiCurrentSensor extends SEGuiContainer<ContainerCurrentSensor> {
    public GuiCurrentSensor(ContainerCurrentSensor screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);

        this.font.draw(matrixStack, I18n.get("gui.simelectricity.current_threshold"), 18, 85, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.resistance_internal"), 18, 124, 4210752);

        String conditionString = this.container.absMode ? "|I|" : "I";
        conditionString += this.container.inverted ? "<" : ">";
        conditionString += SEUnitHelper.getCurrentStringWithUnit(this.container.thresholdCurrent);
        
        
        int ybase = 22;
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.condition_threshold"), 10, ybase, 4210752);
        this.font.draw(matrixStack, conditionString, 10, ybase + 8, 4210752);
        this.font.draw(matrixStack, "I=" + SEUnitHelper.getCurrentStringWithUnit(this.container.current), 10, ybase + 16, 4210752);
        this.font.draw(matrixStack, "Ron = " + String.format("%.3f", this.container.resistance) + " \u03a9", 10, ybase + 24, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/current_sensor.png");
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        blit(matrixStack, this.leftPos + 152, this.topPos + 44, this.container.emitRedstoneSignal ? 180 : 176, 0, 4, 16);

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

        addServerButton(12, this.leftPos + xbase + 50, this.topPos + ybase - 36, 90, 20, "Toggle Behavior");
        addServerButton(13, this.leftPos + xbase, this.topPos + ybase - 36, 50, 20, "Abs()");

        this.directionSelector = addDirectionSelector(this.leftPos + 116, this.topPos + 20);
    }
}
