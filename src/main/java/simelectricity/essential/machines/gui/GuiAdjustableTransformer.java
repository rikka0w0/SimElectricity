package simelectricity.essential.machines.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;

import org.lwjgl.opengl.GL11;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.GuiDirectionSelector;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@SideOnly(Side.CLIENT)
public final class GuiAdjustableTransformer extends SEGuiContainer<ContainerAdjustableTransformer> {
    public GuiAdjustableTransformer(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.fontRenderer.drawString(I18n.translateToLocal("tile.sime_essential:essential_two_port_electronics.adjustable_transformer.name"), 8, 6, 4210752);

        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:ratio_step_up"), 18, 85, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:resistance_secondary"), 18, 124, 4210752);

        this.fontRenderer.drawString("1:" + String.format("%.1f", this.container.ratio), 74, 22, 4210752);
        this.fontRenderer.drawString(String.format("%.3f", this.container.outputResistance) + " \u03a9", 100, 56, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.vPri), 16, 30, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.vSec), 110, 30, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/adjustable_transformer.png"));
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        drawTexturedModalRect(this.guiLeft + 74, this.guiTop + 32, 176, 0, 28, 48);

        this.directionSelector.draw(this.container.inputSide, this.container.outputSide);
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

        this.buttonList.add(new GuiButton(6, this.guiLeft + xbase, this.guiTop + ybase + 38, 20, 20, "-1"));
        this.buttonList.add(new GuiButton(7, this.guiLeft + xbase + 20, this.guiTop + ybase + 38, 20, 20, "-.1"));
        this.buttonList.add(new GuiButton(8, this.guiLeft + xbase + 40, this.guiTop + ybase + 38, 30, 20, "-.01"));
        this.buttonList.add(new GuiButton(9, this.guiLeft + xbase + 70, this.guiTop + ybase + 38, 30, 20, "+.01"));
        this.buttonList.add(new GuiButton(10, this.guiLeft + xbase + 100, this.guiTop + ybase + 38, 20, 20, "+.1"));
        this.buttonList.add(new GuiButton(11, this.guiLeft + xbase + 120, this.guiTop + ybase + 38, 20, 20, "+1"));

        this.directionSelector = new GuiDirectionSelector(this.guiLeft + 24, this.guiTop + 52,
                Utils.getPlayerSightHorizontal(Essential.proxy.getClientPlayer())
        );
    }
}
