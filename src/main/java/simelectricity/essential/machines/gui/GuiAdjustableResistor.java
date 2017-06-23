package simelectricity.essential.machines.gui;

import org.lwjgl.opengl.GL11;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.network.MessageContainerSync;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdjustableResistor extends GuiContainer{	
	public final ContainerAdjustableResistor container;
	
	public GuiAdjustableResistor(TileEntity te){
		super(new ContainerAdjustableResistor(te));
		this.container = (ContainerAdjustableResistor) super.inventorySlots;
	}
	
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime_essential:essential_electronics.adjustable_resistor.name"), 8, 6, 4210752);

        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:buffered_energy"), 18, 85, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getEnergyStringInJ(container.bufferedEnergy), 18, 98, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getEnergyStringInKWh(container.bufferedEnergy), 18, 107, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:resistance_internal"), 18, 124, 4210752);
        
        int ybase = 22;
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:voltage_input"), 85, ybase, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getVoltageStringWithUnit(container.voltage), 85, ybase+8, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:current_input"), 85, ybase+16, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getCurrentStringWithUnit(container.current), 85, ybase+24, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:power_input"), 85, ybase+32, 4210752);
        fontRendererObj.drawString(SEUnitHelper.getPowerStringWithUnit(container.powerLevel), 85, ybase+40, 4210752);
        
        fontRendererObj.drawString(String.format("%.1f", container.resistance) + " \u03a9", 26, 28, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/adjustable_resistor.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
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
    
    @Override
    public void actionPerformed(GuiButton button) {
    	MessageContainerSync.sendButtonClickEventToSever(container, button.id, GuiScreen.isCtrlKeyDown());
    }
}
