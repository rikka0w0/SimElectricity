package simelectricity.essential.machines.gui;

import org.lwjgl.opengl.GL11;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.SEGuiContainer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiAdjustableResistor extends SEGuiContainer<ContainerAdjustableResistor>{	
    public GuiAdjustableResistor(Container container) {
		super(container);
	}

	@Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(I18n.translateToLocal("tile.sime_essential:essential_electronics.adjustable_resistor.name"), 8, 6, 4210752);

        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:buffered_energy"), 18, 85, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getEnergyStringInJ(container.bufferedEnergy), 18, 98, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getEnergyStringInKWh(container.bufferedEnergy), 18, 107, 4210752);
        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:resistance_internal"), 18, 124, 4210752);
        
        int ybase = 22;
        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:voltage_input"), 85, ybase, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getVoltageStringWithUnit(container.voltage), 85, ybase+8, 4210752);
        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:current_input"), 85, ybase+16, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getCurrentStringWithUnit(container.current), 85, ybase+24, 4210752);
        fontRendererObj.drawString(I18n.translateToLocal("gui.sime:power_input"), 85, ybase+32, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getPowerStringWithUnit(container.powerLevel), 85, ybase+40, 4210752);
        
        fontRendererObj.drawString(String.format("%.1f", container.resistance) + " \u03a9", 26, 28, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/adjustable_resistor.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        int xbase = 18;
        int ybase = 97;
        
        buttonList.add(new GuiButton(0, guiLeft + xbase, 		guiTop + ybase + 38, 30, 20, "-100"));
        buttonList.add(new GuiButton(1, guiLeft + xbase + 30, 	guiTop + ybase + 38, 20, 20, "-10"));
        buttonList.add(new GuiButton(2, guiLeft + xbase + 50, 	guiTop + ybase + 38, 20, 20, "-1"));
        buttonList.add(new GuiButton(3, guiLeft + xbase + 70, 	guiTop + ybase + 38, 20, 20, "+1"));
        buttonList.add(new GuiButton(4, guiLeft + xbase + 90, 	guiTop + ybase + 38, 20, 20, "+10"));
        buttonList.add(new GuiButton(5, guiLeft + xbase + 110, 	guiTop + ybase + 38, 30, 20, "+100"));
        
        buttonList.add(new GuiButton(6, guiLeft + xbase + 100, 	guiTop + ybase, 40, 20, "Clear"));
    }
}
