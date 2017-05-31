/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simElectricity.Templates.Client.Gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simElectricity.Templates.Container.ContainerAdjustableTransformer;
import simElectricity.Templates.Utils.IGuiSyncHandler;
import simElectricity.Templates.Utils.MessageGui;

@SideOnly(Side.CLIENT)
public class GuiAdjustableTransformer extends GuiContainer implements IGuiSyncHandler{
    protected TileEntity te;
    
    private ForgeDirection inputSide, outputSide;
    private double outputResistance, ratio;

	@Override
	public void onGuiEvent(byte eventID, Object[] data) {
		switch (eventID){
		case 0:
			ratio = (Double) data[0];
			outputResistance = (Double) data[1];
			inputSide = (ForgeDirection)data[2];
			outputSide = (ForgeDirection)data[3];
			break;
		}
	}
    
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
    	MessageGui.sendToServer(te, IGuiSyncHandler.EVENT_BUTTON_CLICK, GuiScreen.isCtrlKeyDown(), (byte)button.id);
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        
        int tx = x - guiLeft, ty = y - guiTop;
        ForgeDirection selectedDirection = ForgeDirection.UNKNOWN;

        if (tx >= 139 && tx <= 150 && ty >= 61 && ty <= 63) {
            selectedDirection = ForgeDirection.NORTH;
        } else if (tx >= 139 && tx <= 150 && ty >= 76 && ty <= 78) {
            selectedDirection = ForgeDirection.SOUTH;
        }
        if (tx >= 136 && tx <= 138 && ty >= 64 && ty <= 75) {
            selectedDirection = ForgeDirection.WEST;
        } else if (tx >= 151 && tx <= 153 && ty >= 64 && ty <= 75) {
            selectedDirection = ForgeDirection.EAST;
        }
        if (tx >= 141 && tx <= 148 && ty >= 66 && ty <= 73) {
            selectedDirection = ForgeDirection.UP;
        } else if (tx >= 159 && tx <= 166 && ty >= 66 && ty <= 73) {
            selectedDirection = ForgeDirection.DOWN;
        }

        if (selectedDirection == ForgeDirection.UNKNOWN)
            return;

        MessageGui.sendToServer(te, IGuiSyncHandler.EVENT_FACING_CHANGE, (byte)button, selectedDirection);
    }

    public GuiAdjustableTransformer(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new ContainerAdjustableTransformer(inventoryPlayer, tileEntity));
        te = tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:AdjustableTransformer.name"), 8, 6, 4210752);

        fontRendererObj.drawString("1:" + String.format("%.1f", ratio), 32, 26, 4210752);

        fontRendererObj.drawString(String.format("%.3f", outputResistance) + " \u03a9", 32, 42, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_AdjustableTransformer.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        drawFacingBar(x + 130, y + 61, inputSide, outputSide);
    }

    protected void drawFacingBar(int x, int y, ForgeDirection red, ForgeDirection blue) {
    	if (red == null)
    		return;
    	if (blue == null)
    		return;
    	
        switch (red) {
            case WEST:
                this.drawTexturedModalRect(x + 6, y + 2, 176, 0, 3, 14);
                break;
            case EAST:
                this.drawTexturedModalRect(x + 20, y + 2, 176, 0, 3, 14);
                break;
            case NORTH:
                this.drawTexturedModalRect(x + 8, y, 182, 0, 14, 3);
                break;
            case SOUTH:
                this.drawTexturedModalRect(x + 8, y + 14, 182, 0, 14, 3);
                break;
            case UP:
                this.drawTexturedModalRect(x + 11, y + 5, 182, 6, 8, 8);
                break;
            case DOWN:
                this.drawTexturedModalRect(x + 29, y + 5, 182, 6, 8, 8);
                break;
            default:
                break;
        }

        switch (blue) {
            case WEST:
                this.drawTexturedModalRect(x + 6, y + 2, 179, 0, 3, 14);
                break;
            case EAST:
                this.drawTexturedModalRect(x + 20, y + 2, 179, 0, 3, 14);
                break;
            case NORTH:
                this.drawTexturedModalRect(x + 8, y, 182, 3, 14, 3);
                break;
            case SOUTH:
                this.drawTexturedModalRect(x + 8, y + 14, 182, 3, 14, 3);
                break;
            case UP:
                this.drawTexturedModalRect(x + 11, y + 5, 190, 6, 8, 8);
                break;
            case DOWN:
                this.drawTexturedModalRect(x + 29, y + 5, 190, 6, 8, 8);
                break;
            default:
                break;
        }
    }
}