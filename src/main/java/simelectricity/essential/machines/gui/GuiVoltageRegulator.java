package simelectricity.essential.machines.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.Utils;
import simelectricity.essential.utils.client.GuiDirectionSelector;
import simelectricity.essential.utils.client.SEGuiContainer;

@SideOnly(Side.CLIENT)
public final class GuiVoltageRegulator extends SEGuiContainer<ContainerVoltageRegulator>{	
	public GuiVoltageRegulator(Container container) {
		super(container);
	}
	
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime_essential:essential_two_port_electronics.voltage_regulator.name"), 8, 6, 4210752);

        int xbase = 10;
        int ybase = 22;
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:voltage_regulated"), xbase, ybase, 4210752);
        fontRendererObj.drawString("12V", xbase, ybase+8, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:resistance_output"), xbase, ybase+16, 4210752);
        fontRendererObj.drawString("0.001\u03a9", xbase, ybase+24, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:voltage_input"), xbase, ybase+32, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getVoltageStringWithUnit(container.inputVoltage), xbase, ybase+40, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:voltage_output"), xbase, ybase+48, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getVoltageStringWithUnit(container.outputVoltage), xbase, ybase+56, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:current_output"), xbase, ybase+64, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getCurrentStringWithUnit(container.outputCurrent), xbase, ybase+72, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:duty_cycle"), xbase, ybase+80, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getStringWithoutUnit(container.dutyCycle), xbase, ybase+88, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/standard_background.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
        directionSelector.draw(container.inputSide, container.outputSide);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        
        directionSelector = new GuiDirectionSelector(guiLeft + 132, guiTop + 50,
        		Utils.getPlayerSightHorizontal(Essential.proxy.getClientPlayer())
        		);
    }
}
