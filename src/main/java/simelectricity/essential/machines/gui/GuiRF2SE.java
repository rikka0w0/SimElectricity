package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@SideOnly(Side.CLIENT)
public final class GuiRF2SE extends SEGuiContainer<ContainerRF2SE> {
    public GuiRF2SE(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.fontRenderer.drawString(I18n.translateToLocal("tile.sime_essential:essential_electronics.transformer_rf2se.name"), 8, 6, 4210752);

        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:voltage_output"), 8, 22, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 8, 30, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:power_output"), 8, 38, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getPowerStringWithUnit(this.container.actualOutputPower), 8, 46, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:buffered_energy"), 8, 54, 4210752);
        this.fontRenderer.drawString(this.container.bufferedEnergy + "RF", 8, 62, 4210752);
//        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime_essential:rf_demand"), 8, 70, 4210752);
//        this.fontRenderer.drawString(this.container.rfDemandRateDisplay + "RF", 8, 78, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime_essential:rf_power"), 8, 86, 4210752);
        this.fontRenderer.drawString(this.container.rfInputRateDisplay + "RF", 8, 94, 4210752);
//
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:power_rated") + ": " +
                SEUnitHelper.getPowerStringWithUnit(this.container.ratedOutputPower), 18, 124, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/se2rf.png"));
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void initGui() {
        super.initGui();
        int xbase = 18;
        int ybase = 97;

        this.buttonList.add(new GuiButton(0, this.guiLeft + xbase, this.guiTop + ybase + 38, 30, 20, "-100"));
        this.buttonList.add(new GuiButton(1, this.guiLeft + xbase + 30, this.guiTop + ybase + 38, 20, 20, "-10"));
        this.buttonList.add(new GuiButton(2, this.guiLeft + xbase + 50, this.guiTop + ybase + 38, 20, 20, "-1"));
        this.buttonList.add(new GuiButton(3, this.guiLeft + xbase + 70, this.guiTop + ybase + 38, 20, 20, "+1"));
        this.buttonList.add(new GuiButton(4, this.guiLeft + xbase + 90, this.guiTop + ybase + 38, 20, 20, "+10"));
        this.buttonList.add(new GuiButton(5, this.guiLeft + xbase + 110, this.guiTop + ybase + 38, 30, 20, "+100"));
    }
}
