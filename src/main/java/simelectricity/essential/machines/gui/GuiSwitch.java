package simelectricity.essential.machines.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.Utils;
import simelectricity.essential.utils.client.GuiDirectionSelector;
import simelectricity.essential.utils.client.SEGuiContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiSwitch extends SEGuiContainer<ContainerSwitch>{
	public GuiSwitch(Container container) {
		super(container);
	}
	
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime_essential:essential_two_port_electronics.switch.name"), 8, 6, 4210752);
        
        int ybase = 22;
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:current_trip"), 10, ybase, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getCurrentStringWithUnit(container.maxCurrent), 10, ybase+8, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:current"), 10, ybase+16, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getCurrentStringWithUnit(container.current), 10, ybase+24, 4210752);
        fontRendererObj.drawString("Ron = " + String.format("%.3f", container.resistance) + " \u03a9", 10, ybase+32, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/diode.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
        directionSelector.draw(container.inputSide, container.outputSide);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        
        directionSelector = new GuiDirectionSelector(guiLeft + 132, guiTop + 30,
        		Utils.getPlayerSightHorizontal(Essential.proxy.getClientPlayer())
        		);
    }
}
