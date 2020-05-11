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
public final class GuiSE2RF<T extends ContainerSE2RF> extends SEGuiContainer<T>{
    public GuiSE2RF(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.drawString(title.getFormattedText(), 8, 6, 4210752);

        this.font.drawString(I18n.format("gui.simelectricity.voltage_input"), 8, 22, 4210752);
        this.font.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 8, 30, 4210752);
        this.font.drawString(I18n.format("gui.simelectricity.power_input"), 8, 38, 4210752);
        this.font.drawString(SEUnitHelper.getPowerStringWithUnit(this.container.actualInputPower), 8, 46, 4210752);
        this.font.drawString(I18n.format("gui.simelectricity.buffered_energy"), 8, 54, 4210752);
        this.font.drawString(SEUnitHelper.getEnergyStringInJ(this.container.bufferedEnergy), 8, 62, 4210752);
        this.font.drawString(I18n.format("gui.sime_essential.rf_demand"), 8, 70, 4210752);
        this.font.drawString(this.container.rfDemandRateDisplay + "RF", 8, 78, 4210752);
        this.font.drawString(I18n.format("gui.sime_essential.rf_power"), 8, 86, 4210752);
        this.font.drawString(this.container.rfOutputRateDisplay + "RF", 8, 94, 4210752);

        this.font.drawString(I18n.format("gui.simelectricity.power_rated") + ": " +
                SEUnitHelper.getPowerStringWithUnit(this.container.ratedOutputPower), 18, 124, 4210752);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/se2rf.png");
        blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
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
    }

}
