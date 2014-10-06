package simElectricity.Client.Gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import simElectricity.Common.Blocks.Container.ContainerIceMachine;
import simElectricity.Common.Blocks.TileEntity.TileIceMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class GuiIceMachine extends GuiContainer{
	TileIceMachine te;
	
	public static void drawLiquidBar(int x,int y,int width,int height,int fluidID,int percentage){
		Fluid fluid=FluidRegistry.getFluid(fluidID);
		if (fluid==null)
			return;
		
		IIcon icon=fluid.getIcon();
		
		if (icon==null)
			return;

		//Bind SpriteNumber=0,texture "/terrain.png"
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		texturemanager.bindTexture(texturemanager.getResourceLocation(0)); 
		
		
		double u = icon.getInterpolatedU(3.0D);
		double u2 = icon.getInterpolatedU(13.0D);
		double v = icon.getInterpolatedV(1.0D);
		double v2 = icon.getInterpolatedV(15.0D);

		int z=height*percentage/100;

		GL11.glEnable(3553);
		GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);

		GL11.glBegin(7);
		GL11.glTexCoord2d(u, v);
		GL11.glVertex2i(x, y+height-z);

		GL11.glTexCoord2d(u, v2);
		GL11.glVertex2i(x, y + height);

		GL11.glTexCoord2d(u2, v2);
		GL11.glVertex2i(x + width, y + height);

		GL11.glTexCoord2d(u2, v);
		GL11.glVertex2i(x + width, y+height-z);
		GL11.glEnd();
	}
	
    public GuiIceMachine(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new ContainerIceMachine(inventoryPlayer, tileEntity));
        this.te = (TileIceMachine) tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

    	this.drawLiquidBar(35, 18, 16, 47, te.fluidID, te.amountP/10);
    	
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_IceMachine.png"));
        this.drawTexturedModalRect(35, 18, 176, 0, 16, 47);
        
        if (te.isPowered == 1)
        	this.drawTexturedModalRect(152, 63, 176, 52, 14, 14);
        
        this.drawTexturedModalRect(64, 24, 176, 66, 47 * te.progress / 100, 35);
        
    	int x=par1-((width-xSize)/2),y=par2-((height-ySize)/2);
    	if (x>=35&y>=18&x<=50&y<=64){
    		List<String> l = new ArrayList<String>();
    		if (te.fluidID>0){
    			l.add(String.valueOf(te.amountP/10)+"."+String.valueOf(te.amountP-(te.amountP/10)*10)+"%");
    			l.add("About "+String.valueOf(te.amountP*te.maxCapacity/1000)+"mB");
    			func_146283_a(l, x, y);   
            }             
    	}
        
    	fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:IceMachine.name"), 8, 6, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
        //fontRendererObj.drawString(String.valueOf(tileentity.progress) + "%", xSize - 36, ySize - 128, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_IceMachine.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        //if (tileentity.progress > 0)
            //this.drawTexturedModalRect(x + 66, y + 33, 176, 0, 24 * tileentity.progress / 100, 17);
    }
}
