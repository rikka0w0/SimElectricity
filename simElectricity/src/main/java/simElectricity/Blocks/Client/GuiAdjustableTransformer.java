package simElectricity.Blocks.Client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import simElectricity.API.Util;
import simElectricity.Blocks.ContainerQuantumGenerator;
import simElectricity.Blocks.TileAdjustableResistor;
import simElectricity.Blocks.TileAdjustableTransformer;


public class GuiAdjustableTransformer extends GuiContainer {
    protected TileAdjustableTransformer te;

    @Override
    public void initGui() {
        super.initGui();
        
  
        buttonList.add(new GuiButton(0, guiLeft + 96, guiTop + 37, 20, 20, "--"));
        buttonList.add(new GuiButton(1, guiLeft + 116, guiTop + 37, 16, 20, "-"));
        buttonList.add(new GuiButton(2, guiLeft + 132, guiTop + 37, 16, 20, "+"));
        buttonList.add(new GuiButton(3, guiLeft + 148, guiTop + 37, 20, 20, "++"));
        
        buttonList.add(new GuiButton(4, guiLeft + 96, guiTop + 18, 20, 20, "--"));
        buttonList.add(new GuiButton(5, guiLeft + 116, guiTop + 18, 16, 20, "-"));
        buttonList.add(new GuiButton(6, guiLeft + 132, guiTop + 18, 16, 20, "+"));
        buttonList.add(new GuiButton(7, guiLeft + 148, guiTop + 18, 20, 20, "++"));

    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
        case 0:
            if (GuiScreen.isCtrlKeyDown())
                te.outputResistance -= 1;
            else
                te.outputResistance -= 0.1;
            break;
        case 1:
            if (GuiScreen.isCtrlKeyDown())
                te.outputResistance -= 0.001;
            else
                te.outputResistance -= 0.01;
            break;
        case 2:
            if (GuiScreen.isCtrlKeyDown())
                te.outputResistance += 0.001;
            else
                te.outputResistance += 0.01;
            break;
        case 3:
            if (GuiScreen.isCtrlKeyDown())
                te.outputResistance += 1;
            else
                te.outputResistance += 0.1;
            break;
            
        case 4:
            if (GuiScreen.isCtrlKeyDown())
                te.ratio -= 100;
            else
                te.ratio -= 10;
            break;
        case 5:
            if (GuiScreen.isCtrlKeyDown())
                te.ratio -= 0.1;
            else
                te.ratio -= 1;
            break;
        case 6:
            if (GuiScreen.isCtrlKeyDown())
                te.ratio += 0.1;
            else
                te.ratio += 1;
            break;
        case 7:
            if (GuiScreen.isCtrlKeyDown())
                te.ratio += 100;
            else
                te.ratio += 10;
            break;        	
            
        default:
        }

        if (te.outputResistance < 0.001)
        	te.outputResistance = 0.001F;
        if (te.outputResistance > 100)
        	te.outputResistance = 100;
        if (button.id < 4)
        	Util.updateTileEntityFieldToServer(te, "outputResistance");
        
        if (te.ratio < 0.1)
        	te.ratio = 0.1F;
        if (te.ratio > 1000)
        	te.ratio = 1000;
        if (button.id < 8 && button.id > 3)
        	Util.updateTileEntityFieldToServer(te, "ratio");        
        
    }

    public void mouseClicked(int x, int y, int button){
    	super.mouseClicked(x, y, button);
    	
    	int t_x = x-guiLeft, t_y = y-guiTop;
    	System.out.print(t_x);
    	System.out.print(",");
    	System.out.print(t_y);
    	System.out.print("-");
    	System.out.println(button);    	
    }
    
    public GuiAdjustableTransformer(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new ContainerQuantumGenerator(inventoryPlayer, tileEntity));
        te = (TileAdjustableTransformer) tileEntity;
    }

    String float2Str(float f, int dig) {
        return String.valueOf(((int) (f * dig)) / dig) + "." + String.valueOf((int) (f * dig) - ((int) (f * dig)) / dig * dig);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.AdjustableTransformer.name"), 8, 6, 4210752);
        
      	fontRendererObj.drawString("1:"+String.format("%.1f", te.ratio), 32, 26, 4210752);
        
        fontRendererObj.drawString(String.format("%.3f", te.outputResistance) + " \u03a9", 32, 42, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_AdjustableTransformer.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        
        drawFacingBar(x+130, y+61,te.getPrimarySide().getOpposite(),te.getSecondarySide().getOpposite());
    }
    
    protected void drawFacingBar(int x,int y, ForgeDirection red, ForgeDirection blue){    	
    	switch(red){
    	case EAST:
    		this.drawTexturedModalRect(x+6, y+2, 176, 0, 3, 14);
    		break;
    	case WEST:
    		this.drawTexturedModalRect(x+20, y+2, 176, 0, 3, 14);
    		break;
    	case NORTH:
    		this.drawTexturedModalRect(x+8, y, 182, 0, 14, 3);
    		break;
    	case SOUTH:
    		this.drawTexturedModalRect(x+8, y+14, 182, 0, 14, 3);
    		break;
    	case UP:
    		this.drawTexturedModalRect(x+11, y+5, 182, 6, 8, 8);
    		break;
    	case DOWN:
    		this.drawTexturedModalRect(x+29, y+5, 182, 6, 8, 8);
    		break;
		default:
			break;    		
    	}
    	
    	switch(blue){
    	case EAST:
    		this.drawTexturedModalRect(x+6, y+2, 179, 0, 3, 14);
    		break;
    	case WEST:
    		this.drawTexturedModalRect(x+20, y+2, 179, 0, 3, 14);
    		break;
    	case NORTH:
    		this.drawTexturedModalRect(x+8, y, 182, 3, 14, 3);
    		break;
    	case SOUTH:
    		this.drawTexturedModalRect(x+8, y+14, 182, 3, 14, 3);
    		break;
    	case UP:
    		this.drawTexturedModalRect(x+11, y+5, 190, 6, 8, 8);
    		break;
    	case DOWN:
    		this.drawTexturedModalRect(x+29, y+5, 190, 6, 8, 8);
    		break;
		default:
			break;    		
    	}
    }
}