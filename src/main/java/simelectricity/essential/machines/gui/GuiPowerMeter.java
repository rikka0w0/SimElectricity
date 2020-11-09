package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;
import simelectricity.essential.utils.network.MessageContainerSync;

@OnlyIn(Dist.CLIENT)
public class GuiPowerMeter extends SEGuiContainer<ContainerPowerMeter> {
    ////////////////////////
    /// Switch
    ////////////////////////
    private static final int switchSize = 32;
    private static final int switchX = 115;
    private static final int switchY = 48;

    public GuiPowerMeter(ContainerPowerMeter screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.drawString(matrixStack, this.title.getString(), 8, 6, 4210752);

        int ybase = 22;
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.voltage"), 10, ybase, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 10, ybase + 8, 4210752);
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.current"), 10, ybase + 16, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getCurrentStringWithUnit(this.container.current), 10, ybase + 24, 4210752);
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.power_input"), 10, ybase+32, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getPowerStringWithUnit(this.container.voltage*this.container.current), 10, ybase + 40, 4210752);
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.used_energy"), 10, ybase + 48, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getEnergyStringInKWh(this.container.bufferedEnergy), 10, ybase + 56, 4210752);
        this.font.drawString(matrixStack, SEUnitHelper.getEnergyStringInJ(this.container.bufferedEnergy), 10, ybase + 64, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int xMouse, int yMouse) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/power_meter.png");
        blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        blit(matrixStack, this.guiLeft + switchX, this.guiTop + switchY, this.container.isOn ? 208 : 176, 0, 32, 32);

        this.directionSelector.set(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void init() {
        super.init();
//        int xbase = 18;
        int ybase = 97;

        this.directionSelector = addDirectionSelector(this.guiLeft + 116, this.guiTop + 20);
        addServerButton(1, this.guiLeft + 10, this.guiTop + ybase, 40, 20, "Clear");
    }
    
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean ret = super.mouseClicked(x, y, button);

        if (x >= this.guiLeft + GuiPowerMeter.switchX && y >= this.guiTop + GuiPowerMeter.switchY && x < this.guiLeft + GuiPowerMeter.switchX + GuiPowerMeter.switchSize && y < this.guiTop + GuiPowerMeter.switchY + GuiPowerMeter.switchSize)
            MessageContainerSync.sendButtonClickEventToSever(this.container, 12, Screen.hasControlDown());
        
        return ret;
    }
}
