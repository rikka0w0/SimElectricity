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

package simElectricity.Client.Gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import simElectricity.API.Network;
import simElectricity.Common.Blocks.Container.ContainerSolarInverter;
import simElectricity.Common.Blocks.TileEntity.TileSolarInverter;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiSolarInverter extends GuiContainer {
    protected TileSolarInverter te;

    public GuiSolarInverter(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new ContainerSolarInverter(inventoryPlayer, tileEntity));
        te = (TileSolarInverter) tileEntity;
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
                    te.outputVoltage -= 100;
                else
                    te.outputVoltage -= 10;
                break;
            case 5:
                if (GuiScreen.isCtrlKeyDown())
                    te.outputVoltage -= 0.1;
                else
                    te.outputVoltage -= 1;
                break;
            case 6:
                if (GuiScreen.isCtrlKeyDown())
                    te.outputVoltage += 0.1;
                else
                    te.outputVoltage += 1;
                break;
            case 7:
                if (GuiScreen.isCtrlKeyDown())
                    te.outputVoltage += 100;
                else
                    te.outputVoltage += 10;
                break;

            default:
        }

        if (te.outputResistance < 0.001)
            te.outputResistance = 0.001F;
        if (te.outputResistance > 100)
            te.outputResistance = 100;
        if (button.id < 4)
            Network.updateTileEntityFieldsToServer(te, "outputResistance");

        if (te.outputVoltage < 200)
            te.outputVoltage = 200;
        if (te.outputVoltage > 240)
            te.outputVoltage = 240;
        if (button.id < 8 && button.id > 3)
            Network.updateTileEntityFieldsToServer(te, "outputVoltage");

    }

    @Override
    public void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        int tx = x - guiLeft, ty = y - guiTop;
        EnumFacing selectedDirection = null;

        if (tx >= 139 && tx <= 150 && ty >= 61 && ty <= 63) {
            selectedDirection = EnumFacing.NORTH;
        } else if (tx >= 139 && tx <= 150 && ty >= 76 && ty <= 78) {
            selectedDirection = EnumFacing.SOUTH;
        }
        if (tx >= 136 && tx <= 138 && ty >= 64 && ty <= 75) {
            selectedDirection = EnumFacing.WEST;
        } else if (tx >= 151 && tx <= 153 && ty >= 64 && ty <= 75) {
            selectedDirection = EnumFacing.EAST;
        }
        if (tx >= 141 && tx <= 148 && ty >= 66 && ty <= 73) {
            selectedDirection = EnumFacing.UP;
        } else if (tx >= 159 && tx <= 166 && ty >= 66 && ty <= 73) {
            selectedDirection = EnumFacing.DOWN;
        }

        if (selectedDirection == null)
            return;

        if (button == 0) {        //Left key
            if (te.getSecondarySide() == selectedDirection)
                te.outputSide = te.inputSide;

            te.inputSide = selectedDirection;
        } else if (button == 1) { //Right key
            if (te.getPrimarySide() == selectedDirection)
                te.inputSide = te.outputSide;

            te.outputSide = selectedDirection;
        }

        Network.updateTileEntityFieldsToServer(te, "inputSide", "outputSide");
        te.getWorld().markBlockForUpdate(te.getPos());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:SolarInverter.name"), 8, 6, 4210752);

        fontRendererObj.drawString("Vo: " + String.format("%.1f", te.outputVoltage) + "V", 32, 26, 4210752);

        fontRendererObj.drawString("Ro: " + String.format("%.3f", te.outputResistance) + " \u03a9", 32, 42, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_SolarInverter.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        drawFacingBar(x + 130, y + 61, te.getPrimarySide(), te.getSecondarySide());
    }

    protected void drawFacingBar(int x, int y, EnumFacing red, EnumFacing blue) {
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