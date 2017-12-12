package simelectricity.essential.coverpanel;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

public class GuiVoltageSensor extends SEGuiContainer<ContainerVoltageSensor> {
    public GuiVoltageSensor(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.fontRenderer.drawString(I18n.translateToLocal("item.sime_essential:essential_item.voltagesensor.name"), 8, 6, 4210752);

        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:voltage_threshold"), 18, 124, 4210752);

        int ybase = 22;
        this.fontRenderer.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.thresholdVoltage), 20, 51, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/voltage_sensor.png"));
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        drawTexturedModalRect(this.guiLeft + 70, this.guiTop + 30, this.container.inverted ? 52 : 0, 166, 52, 33);
        drawTexturedModalRect(this.guiLeft + 130, this.guiTop + 36, this.container.emitRedStoneSignal ? 180 : 176, 0, 4, 16);
    }

    @Override
    public void initGui() {
        super.initGui();
        int xbase = 18;
        int ybase = 97;

        this.buttonList.add(new GuiButton(6, this.guiLeft + xbase, this.guiTop + ybase, 140, 20, I18n.translateToLocal("gui.sime_essential:redstone_toggle_behavior")));

        this.buttonList.add(new GuiButton(0, this.guiLeft + xbase, this.guiTop + ybase + 38, 30, 20, "-100"));
        this.buttonList.add(new GuiButton(1, this.guiLeft + xbase + 30, this.guiTop + ybase + 38, 20, 20, "-10"));
        this.buttonList.add(new GuiButton(2, this.guiLeft + xbase + 50, this.guiTop + ybase + 38, 20, 20, "-1"));
        this.buttonList.add(new GuiButton(3, this.guiLeft + xbase + 70, this.guiTop + ybase + 38, 20, 20, "+1"));
        this.buttonList.add(new GuiButton(4, this.guiLeft + xbase + 90, this.guiTop + ybase + 38, 20, 20, "+10"));
        this.buttonList.add(new GuiButton(5, this.guiLeft + xbase + 110, this.guiTop + ybase + 38, 30, 20, "+100"));
    }
}
