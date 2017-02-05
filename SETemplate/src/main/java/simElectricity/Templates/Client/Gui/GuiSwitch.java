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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import simElectricity.API.SEAPI;
import simElectricity.Templates.Container.ContainerSwitch;
import simElectricity.Templates.TileEntity.TileSwitch;

@SideOnly(Side.CLIENT)
public class GuiSwitch extends GuiContainer {
    protected TileSwitch te;

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
                    te.resistance -= 1;
                else
                    te.resistance -= 0.1;
                break;
            case 1:
                if (GuiScreen.isCtrlKeyDown())
                    te.resistance -= 0.001;
                else
                    te.resistance -= 0.01;
                break;
            case 2:
                if (GuiScreen.isCtrlKeyDown())
                    te.resistance += 0.001;
                else
                    te.resistance += 0.01;
                break;
            case 3:
                if (GuiScreen.isCtrlKeyDown())
                    te.resistance += 1;
                else
                    te.resistance += 0.1;
                break;

            case 4:
                if (GuiScreen.isCtrlKeyDown())
                    te.maxCurrent -= 100;
                else
                    te.maxCurrent -= 10;
                break;
            case 5:
                if (GuiScreen.isCtrlKeyDown())
                    te.maxCurrent -= 0.1;
                else
                    te.maxCurrent -= 1;
                break;
            case 6:
                if (GuiScreen.isCtrlKeyDown())
                    te.maxCurrent += 0.1;
                else
                    te.maxCurrent += 1;
                break;
            case 7:
                if (GuiScreen.isCtrlKeyDown())
                    te.maxCurrent += 100;
                else
                    te.maxCurrent += 10;
                break;

            default:
        }

        if (te.resistance < 0.001)
            te.resistance = 0.001F;
        if (te.resistance > 100)
            te.resistance = 100;
        if (button.id < 4)
        	SEAPI.networkManager.updateTileEntityFieldsToServer(te, "resistance");

        if (te.maxCurrent < 0.1)
            te.maxCurrent = 0.1F;
        if (te.maxCurrent > 1000)
            te.maxCurrent = 1000;
        if (button.id < 8 && button.id > 3)
        	SEAPI.networkManager.updateTileEntityFieldsToServer(te, "maxCurrent");

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

        if (selectedDirection == ForgeDirection.UNKNOWN) {
            if (tx > 80 && tx < 110 && ty > 66 && ty < 72) {
                te.isOn = !te.isOn;
                SEAPI.networkManager.updateTileEntityFieldsToServer(te, "isOn");
            }
            return;
        }


        if (button == 0) {        //Left key
            if (te.outputSide == selectedDirection)
                te.outputSide = te.inputSide;

            te.inputSide = selectedDirection;
        } else if (button == 1) { //Right key
            if (te.inputSide == selectedDirection)
                te.inputSide = te.outputSide;

            te.outputSide = selectedDirection;
        }

        SEAPI.networkManager.updateTileEntityFieldsToServer(te, "inputSide", "outputSide");
        te.getWorldObj().markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
    }

    public GuiSwitch(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new ContainerSwitch(inventoryPlayer, tileEntity));
        te = (TileSwitch) tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        if (te.isOn)
            fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:Switch.name") + "              (I=" + String.format("%.3f", te.current) + " A)", 8, 6, 4210752);
        else
            fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:Switch.name"), 8, 6, 4210752);


        fontRendererObj.drawString("Imax = " + String.format("%.1f", te.maxCurrent) + " A", 8, 26, 4210752);

        fontRendererObj.drawString("Ron = " + String.format("%.3f", te.resistance) + " \u03a9", 8, 42, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_Switch.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        if (te.isOn) {
            drawTexturedModalRect(x + 91, y + 70, 3, 0, 9, 1);
        }

        drawFacingBar(x + 130, y + 61, te.inputSide, te.outputSide);
    }

    protected void drawFacingBar(int x, int y, ForgeDirection red, ForgeDirection blue) {
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