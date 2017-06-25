package simelectricity.essential.machines.gui;

import org.lwjgl.opengl.GL11;

import simelectricity.essential.Essential;
import simelectricity.essential.utils.Utils;
import simelectricity.essential.utils.client.GuiDirectionSelector;
import simelectricity.essential.utils.client.SEGuiContainer;
import simelectricity.essential.utils.network.MessageContainerSync;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public final class GuiAdjustableTransformer extends SEGuiContainer<ContainerAdjustableTransformer>{	
	public GuiAdjustableTransformer(Container container) {
		super(container);
	}

	private GuiDirectionSelector directionSelector;

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime_essential:essential_two_port_electronics.adjustable_transformer.name"), 8, 6, 4210752);

        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:ratio_step_up"), 18, 85, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.sime:resistance_secondary"), 18, 124, 4210752);
        
        fontRendererObj.drawString("1:" + String.format("%.1f", container.ratio), 32, 32, 4210752);
        fontRendererObj.drawString(String.format("%.3f", container.outputResistance) + " \u03a9", 32, 47, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/adjustable_transformer.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
        ForgeDirection inputSide = container.inputSide, outputSide = container.outputSide;
        if (inputSide == null || outputSide == null)
        	return;
        
        directionSelector.draw(inputSide, outputSide);
    }
	
    @Override
    public void initGui() {
        super.initGui();


        int xbase = 18;
        int ybase = 97;
        
        buttonList.add(new GuiButton(0, guiLeft + xbase, 		guiTop + ybase, 20, 20, "-1"));
        buttonList.add(new GuiButton(1, guiLeft + xbase + 20, 	guiTop + ybase, 20, 20, "-.1"));
        buttonList.add(new GuiButton(2, guiLeft + xbase + 40, 	guiTop + ybase, 30, 20, "-.01"));
        buttonList.add(new GuiButton(3, guiLeft + xbase + 70, 	guiTop + ybase, 30, 20, "+.01"));
        buttonList.add(new GuiButton(4, guiLeft + xbase + 100, 	guiTop + ybase, 20, 20, "+.1"));
        buttonList.add(new GuiButton(5, guiLeft + xbase + 120, 	guiTop + ybase, 20, 20, "+1"));
        
        buttonList.add(new GuiButton(6, guiLeft + xbase, 		guiTop + ybase + 38, 20, 20, "-1"));
        buttonList.add(new GuiButton(7, guiLeft + xbase + 20, 	guiTop + ybase + 38, 20, 20, "-.1"));
        buttonList.add(new GuiButton(8, guiLeft + xbase + 40, 	guiTop + ybase + 38, 30, 20, "-.01"));
        buttonList.add(new GuiButton(9, guiLeft + xbase + 70, 	guiTop + ybase + 38, 30, 20, "+.01"));
        buttonList.add(new GuiButton(10, guiLeft + xbase + 100, 	guiTop + ybase + 38, 20, 20, "+.1"));
        buttonList.add(new GuiButton(11, guiLeft + xbase + 120, 	guiTop + ybase + 38, 20, 20, "+1"));

        directionSelector = new GuiDirectionSelector(guiLeft + 132, guiTop + 50,
        		Utils.getPlayerSightHorizontal(Essential.proxy.getClientPlayer())
        		);
    }
	
    @Override
    public void actionPerformed(GuiButton button) {
    	MessageContainerSync.sendButtonClickEventToSever(container, button.id, GuiScreen.isCtrlKeyDown());
    }
    
    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        
        ForgeDirection selectedDirection = directionSelector.onMouseClick(x, y);

        if (selectedDirection == ForgeDirection.UNKNOWN)
            return;
        
        MessageContainerSync.sendDirectionSelectorClickEventToSever(container, selectedDirection, button);
    }
}
