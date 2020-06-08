package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public final class GuiAdjustableResistor extends SEGuiContainer<ContainerAdjustableResistor> {
    public GuiAdjustableResistor(ContainerAdjustableResistor screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.drawString(title.getFormattedText(), 8, 6, 4210752);

        this.font.drawString(I18n.format("gui.simelectricity.buffered_energy"), 18, 85, 4210752);
        this.font.drawString(SEUnitHelper.getEnergyStringInJ(this.container.bufferedEnergy), 18, 98, 4210752);
        this.font.drawString(SEUnitHelper.getEnergyStringInKWh(this.container.bufferedEnergy), 18, 107, 4210752);
        this.font.drawString(I18n.format("gui.simelectricity.resistance_internal"), 18, 124, 4210752);

        int ybase = 22;
        this.font.drawString(I18n.format("gui.simelectricity.voltage_input"), 85, ybase, 4210752);
        this.font.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 85, ybase + 8, 4210752);
        this.font.drawString(I18n.format("gui.simelectricity.current_input"), 85, ybase + 16, 4210752);
        this.font.drawString(SEUnitHelper.getCurrentStringWithUnit(this.container.current), 85, ybase + 24, 4210752);
        this.font.drawString(I18n.format("gui.simelectricity.power_input"), 85, ybase + 32, 4210752);
        this.font.drawString(SEUnitHelper.getPowerStringWithUnit(this.container.powerLevel), 85, ybase + 40, 4210752);

        this.font.drawString(String.format("%.1f", this.container.resistance) + " \u03a9", 26, 28, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/adjustable_resistor.png");
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    public void init() {
        super.init();
        int xbase = 18;
        int ybase = 97;

        addServerButton(0, this.guiLeft + xbase, this.guiTop + ybase + 38, 30, 20, "-100");
        addServerButton(1, this.guiLeft + xbase + 30, this.guiTop + ybase + 38, 20, 20, "-10");
        addServerButton(2, this.guiLeft + xbase + 50, this.guiTop + ybase + 38, 20, 20, "-1");
        addServerButton(3, this.guiLeft + xbase + 70, this.guiTop + ybase + 38, 20, 20, "+1");
        addServerButton(4, this.guiLeft + xbase + 90, this.guiTop + ybase + 38, 20, 20, "+10");
        addServerButton(5, this.guiLeft + xbase + 110, this.guiTop + ybase + 38, 30, 20, "+100");

        addServerButton(6, this.guiLeft + xbase + 100, this.guiTop + ybase, 40, 20, "Clear");
    }
}
