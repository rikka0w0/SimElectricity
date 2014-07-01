package simElectricity.Test;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiBatteryBox extends GuiContainer{
	protected TileBatteryBox tileentity;
	
    public GuiBatteryBox (InventoryPlayer inventoryPlayer,TileBatteryBox tileEntity) {
    	super(new ContainerBatteryBox(inventoryPlayer,tileEntity));
    	tileentity=tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {     	
     	//draw text and stuff here
       	//the parameters for drawString are: string, x, y, color
    	
       	fontRendererObj.drawString(StatCollector.translateToLocal("tile.BatteryBox.name"), 8, 6, 4210752);
       	       	
       	//draws "Inventory" or your regional equivalent
       	fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
       	fontRendererObj.drawString(String.valueOf(tileentity.progress)+"%", xSize-36, ySize - 128, 4210752);
       	fontRendererObj.drawString(String.valueOf(tileentity.energy), xSize-36, ySize - 128-10, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,int par3) {
       	//draw your Gui here, only thing you need to change is the path
       	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
       	mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_BatteryBox.png"));

       	drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);      
       	
       	drawTexturedModalRect(guiLeft+7, guiTop+44, 176, 0, 18, 18);  
       	drawTexturedModalRect(guiLeft+7, guiTop+20, 176, 0, 18, 18);
       	drawTexturedModalRect(guiLeft+36, guiTop+20, 194, 0, 32, 17);
       	if (tileentity.progress>0)
       		drawTexturedModalRect(guiLeft+36+ 4, guiTop+20, 226, 0, 24*tileentity.progress/100, 17);       	
    }
}
