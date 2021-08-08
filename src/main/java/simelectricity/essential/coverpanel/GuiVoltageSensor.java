package simelectricity.essential.coverpanel;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

public class GuiVoltageSensor extends SEGuiContainer<ContainerVoltageSensor> {
    public GuiVoltageSensor(ContainerVoltageSensor screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

    	this.font.draw(matrixStack, title.getString(), 8, 6, 4210752);

    	this.font.draw(matrixStack, I18n.get("gui.simelectricity.voltage_threshold"), 18, 124, 4210752);

        this.font.draw(matrixStack, SEUnitHelper.getVoltageStringWithUnit(this.container.thresholdVoltage), 20, 51, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bindForSetup(new ResourceLocation("sime_essential:textures/gui/voltage_sensor.png"));
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        blit(matrixStack, this.leftPos + 70, this.topPos + 30, this.container.inverted ? 52 : 0, 166, 52, 33);
        blit(matrixStack, this.leftPos + 130, this.topPos + 36, this.container.emitRedStoneSignal ? 180 : 176, 0, 4, 16);
    }

    @Override
    public void init() {
        super.init();
        int xbase = 18;
        int ybase = 97;

        addServerButton(6, this.leftPos + xbase, this.topPos + ybase, 140, 20, I18n.get("gui.sime_essential.redstone_toggle_behavior"));

        addServerButton(0, this.leftPos + xbase, this.topPos + ybase + 38, 30, 20, "-100");
        addServerButton(1, this.leftPos + xbase + 30, this.topPos + ybase + 38, 20, 20, "-10");
        addServerButton(2, this.leftPos + xbase + 50, this.topPos + ybase + 38, 20, 20, "-1");
        addServerButton(3, this.leftPos + xbase + 70, this.topPos + ybase + 38, 20, 20, "+1");
        addServerButton(4, this.leftPos + xbase + 90, this.topPos + ybase + 38, 20, 20, "+10");
        addServerButton(5, this.leftPos + xbase + 110, this.topPos + ybase + 38, 30, 20, "+100");
    }
}
