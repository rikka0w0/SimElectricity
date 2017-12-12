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
public final class GuiQuantumGenerator extends SEGuiContainer<ContainerQuantumGenerator> {
    public GuiQuantumGenerator(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.fontRenderer.drawString(I18n.translateToLocal("tile.sime_essential:essential_electronics.quantum_generator.name"), 8, 6, 4210752);

        this.fontRenderer.drawString(String.format("%.0f", this.container.internalVoltage) + " V", 30, 46, 4210752);
        this.fontRenderer.drawString(String.format("%.3f", this.container.resistance) + " \u03a9", 30, 24, 4210752);

        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:resistance_internal"), 18, 85, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:voltage_internal"), 18, 124, 4210752);
        //gui.sime_essential:redstone_behavior_inverted
        int ybase = 22;
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:voltage_output"), 85, ybase, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 85, ybase + 8, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:current_output"), 85, ybase + 16, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getCurrentStringWithUnit(this.container.current), 85, ybase + 24, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:power_output"), 85, ybase + 32, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getPowerStringWithUnit(this.container.voltage * this.container.current), 85, ybase + 40, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/quantum_generator.png"));
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void initGui() {
        super.initGui();
        int xbase = 18;
        int ybase = 97;

        this.buttonList.add(new GuiButton(0, this.guiLeft + xbase, this.guiTop + ybase, 20, 20, "-1"));
        this.buttonList.add(new GuiButton(1, this.guiLeft + xbase + 20, this.guiTop + ybase, 20, 20, "-.1"));
        this.buttonList.add(new GuiButton(2, this.guiLeft + xbase + 40, this.guiTop + ybase, 30, 20, "-.01"));
        this.buttonList.add(new GuiButton(3, this.guiLeft + xbase + 70, this.guiTop + ybase, 30, 20, "+.01"));
        this.buttonList.add(new GuiButton(4, this.guiLeft + xbase + 100, this.guiTop + ybase, 20, 20, "+.1"));
        this.buttonList.add(new GuiButton(5, this.guiLeft + xbase + 120, this.guiTop + ybase, 20, 20, "+1"));

        this.buttonList.add(new GuiButton(6, this.guiLeft + xbase, this.guiTop + ybase + 38, 30, 20, "-100"));
        this.buttonList.add(new GuiButton(7, this.guiLeft + xbase + 30, this.guiTop + ybase + 38, 20, 20, "-10"));
        this.buttonList.add(new GuiButton(8, this.guiLeft + xbase + 50, this.guiTop + ybase + 38, 20, 20, "-1"));
        this.buttonList.add(new GuiButton(9, this.guiLeft + xbase + 70, this.guiTop + ybase + 38, 20, 20, "+1"));
        this.buttonList.add(new GuiButton(10, this.guiLeft + xbase + 90, this.guiTop + ybase + 38, 20, 20, "+10"));
        this.buttonList.add(new GuiButton(11, this.guiLeft + xbase + 110, this.guiTop + ybase + 38, 30, 20, "+100"));
    }
}
