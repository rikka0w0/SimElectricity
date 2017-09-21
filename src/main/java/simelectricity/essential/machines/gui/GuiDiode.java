package simelectricity.essential.machines.gui;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.GuiDirectionSelector;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@SideOnly(Side.CLIENT)
public final class GuiDiode extends SEGuiContainer<ContainerDiode> {
    public GuiDiode(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.fontRenderer.drawString(I18n.translateToLocal("tile.sime_essential:essential_two_port_electronics.diode.name"), 8, 6, 4210752);

        int ybase = 22;
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:voltage_input"), 85, ybase, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.inputVoltage), 85, ybase + 8, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:voltage_output"), 85, ybase + 16, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.outputVoltage), 85, ybase + 24, 4210752);

        this.fontRenderer.drawString(I18n.translateToLocal(this.container.forwardBiased ?
                "gui.sime:forward_biased" :
                "gui.sime:reverse_biased"
        ), 85, ybase + 32, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/diode.png"));
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        this.directionSelector.draw(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.directionSelector = new GuiDirectionSelector(this.width / 2 - 10, this.guiTop + 100);
    }
}