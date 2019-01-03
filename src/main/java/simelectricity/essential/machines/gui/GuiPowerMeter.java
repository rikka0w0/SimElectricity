package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.GuiDirectionSelector;
import simelectricity.essential.utils.client.gui.SEGuiContainer;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiPowerMeter extends SEGuiContainer<ContainerPowerMeter> {
    ////////////////////////
    /// Switch
    ////////////////////////
    private static final int switchSize = 32;
    private static final int switchX = 115;
    private static final int switchY = 48;

    public GuiPowerMeter(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.fontRenderer.drawString(I18n.translateToLocal("tile.sime_essential:essential_two_port_electronics.power_meter.name"), 8, 6, 4210752);

        int ybase = 22;
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:voltage"), 10, ybase, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 10, ybase + 8, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:current"), 10, ybase + 16, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getCurrentStringWithUnit(this.container.current), 10, ybase + 24, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:power_input"), 10, ybase+32, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getPowerStringWithUnit(this.container.voltage*this.container.current), 10, ybase + 40, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:used_energy"), 10, ybase + 48, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getEnergyStringInKWh(this.container.bufferedEnergy), 10, ybase + 56, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getEnergyStringInJ(this.container.bufferedEnergy), 10, ybase + 64, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/power_meter.png"));
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        drawTexturedModalRect(this.guiLeft + switchX, this.guiTop + switchY, this.container.isOn ? 208 : 176, 0, 32, 32);

        this.directionSelector.draw(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void initGui() {
        super.initGui();
        int xbase = 18;
        int ybase = 97;

        this.directionSelector = new GuiDirectionSelector(this.guiLeft + 116, this.guiTop + 20);
        this.buttonList.add(new GuiButton(1, this.guiLeft + 10, this.guiTop + ybase, 40, 20, "Clear"));
    }

    @Override
    public void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        if (x >= this.guiLeft + switchX && y >= this.guiTop + switchY && x < this.guiLeft + switchX + switchSize && y < this.guiTop + switchY + switchSize)
            MessageContainerSync.sendButtonClickEventToSever(this.container, 0, GuiScreen.isCtrlKeyDown());
    }
}
