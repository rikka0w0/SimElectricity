package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import simelectricity.essential.Essential;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;
import simelectricity.essential.utils.network.MessageContainerSync;

@OnlyIn(Dist.CLIENT)
public class GuiPowerMeter extends SEGuiContainer<ContainerPowerMeter> {
	private static final ResourceLocation bgTexture =
			new ResourceLocation(Essential.MODID, "textures/gui/power_meter.png");
    ////////////////////////
    /// Switch
    ////////////////////////
    private static final int switchSize = 32;
    private static final int switchX = 115;
    private static final int switchY = 48;

    public GuiPowerMeter(ContainerPowerMeter screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);

        int ybase = 22;
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.voltage"), 10, ybase, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 10, ybase + 8, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.current"), 10, ybase + 16, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getCurrentStringWithUnit(this.container.current), 10, ybase + 24, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.power_input"), 10, ybase+32, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getPowerStringWithUnit(this.container.voltage*this.container.current), 10, ybase + 40, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.used_energy"), 10, ybase + 48, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getEnergyStringInKWh(this.container.bufferedEnergy), 10, ybase + 56, 4210752);
        this.font.draw(matrixStack, SEUnitHelper.getEnergyStringInJ(this.container.bufferedEnergy), 10, ybase + 64, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int xMouse, int yMouse) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, bgTexture);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        blit(matrixStack, this.leftPos + switchX, this.topPos + switchY, this.container.isOn ? 208 : 176, 0, 32, 32);

        this.directionSelector.set(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void init() {
        super.init();
//        int xbase = 18;
        int ybase = 97;

        this.directionSelector = addDirectionSelector(this.leftPos + 116, this.topPos + 20);
        addServerButton(1, this.leftPos + 10, this.topPos + ybase, 40, 20, "Clear");
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean ret = super.mouseClicked(x, y, button);

        if (x >= this.leftPos + GuiPowerMeter.switchX && y >= this.topPos + GuiPowerMeter.switchY && x < this.leftPos + GuiPowerMeter.switchX + GuiPowerMeter.switchSize && y < this.topPos + GuiPowerMeter.switchY + GuiPowerMeter.switchSize)
            MessageContainerSync.sendButtonClickEventToSever(this.container, 0, Screen.hasControlDown());

        return ret;
    }
}
