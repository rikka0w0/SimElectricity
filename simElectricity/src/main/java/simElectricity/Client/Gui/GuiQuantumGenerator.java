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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.Container.ContainerQuantumGenerator;
import simElectricity.Common.Blocks.TileEntity.TileQuantumGenerator;


public class GuiQuantumGenerator extends GuiContainer {
    protected TileQuantumGenerator tileentity;

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(4, guiLeft + 96, guiTop + 18, 20, 20, "--"));
        buttonList.add(new GuiButton(5, guiLeft + 116, guiTop + 18, 16, 20, "-"));
        buttonList.add(new GuiButton(6, guiLeft + 132, guiTop + 18, 16, 20, "+"));
        buttonList.add(new GuiButton(7, guiLeft + 148, guiTop + 18, 20, 20, "++"));

        buttonList.add(new GuiButton(0, guiLeft + 96, guiTop + 40, 20, 20, "--"));
        buttonList.add(new GuiButton(1, guiLeft + 116, guiTop + 40, 16, 20, "-"));
        buttonList.add(new GuiButton(2, guiLeft + 132, guiTop + 40, 16, 20, "+"));
        buttonList.add(new GuiButton(3, guiLeft + 148, guiTop + 40, 20, 20, "++"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id < 4) {
            switch (button.id) {
                case 0:
                    if (GuiScreen.isCtrlKeyDown())
                        tileentity.outputVoltage -= 100;
                    else
                        tileentity.outputVoltage -= 10;
                    break;
                case 1:
                    if (GuiScreen.isCtrlKeyDown())
                        tileentity.outputVoltage -= 0.1;
                    else
                        tileentity.outputVoltage -= 1;
                    break;
                case 2:
                    if (GuiScreen.isCtrlKeyDown())
                        tileentity.outputVoltage += 0.1;
                    else
                        tileentity.outputVoltage += 1;
                    break;
                case 3:
                    if (GuiScreen.isCtrlKeyDown())
                        tileentity.outputVoltage += 100;
                    else
                        tileentity.outputVoltage += 10;
                    break;
                default:
            }

            if (tileentity.outputVoltage < 0)
                tileentity.outputVoltage = 0.1F;
            if (tileentity.outputVoltage > 10000)
                tileentity.outputVoltage = 10000;
            Util.updateTileEntityFieldToServer(tileentity, "outputVoltage");
        } else if (button.id <= 8) {
            switch (button.id) {
                case 4:
                    if (GuiScreen.isCtrlKeyDown())
                        tileentity.outputResistance -= 1;
                    else
                        tileentity.outputResistance -= 0.1;
                    break;
                case 5:
                    if (GuiScreen.isCtrlKeyDown())
                        tileentity.outputResistance -= 0.001;
                    else
                        tileentity.outputResistance -= 0.01;
                    break;
                case 6:
                    if (GuiScreen.isCtrlKeyDown())
                        tileentity.outputResistance += 0.001;
                    else
                        tileentity.outputResistance += 0.01;
                    break;
                case 7:
                    if (GuiScreen.isCtrlKeyDown())
                        tileentity.outputResistance += 1;
                    else
                        tileentity.outputResistance += 0.1;
                    break;
                default:
            }

            if (tileentity.outputResistance < 0)
                tileentity.outputResistance = 0.001F;
            if (tileentity.outputResistance > 100)
                tileentity.outputResistance = 100;
            Util.updateTileEntityFieldToServer(tileentity, "outputResistance");
        }
    }

    public GuiQuantumGenerator(InventoryPlayer inventoryPlayer, TileQuantumGenerator tileEntity) {
        super(new ContainerQuantumGenerator(inventoryPlayer, tileEntity));
        tileentity = tileEntity;
    }

    String float2Str(float f, int dig) {
        return String.valueOf(((int) (f * dig)) / dig) + "." + String.valueOf((int) (f * dig) - ((int) (f * dig)) / dig * dig);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:QuantumGenerator.name"), 8, 6, 4210752);

        fontRendererObj.drawString(String.format("%.1f", tileentity.outputVoltage) + " V", 30, 46, 4210752);
        fontRendererObj.drawString(String.format("%.3f", tileentity.outputResistance) + " \u03a9", 30, 24, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
        fontRendererObj.drawString(tileentity.getFunctionalSide().toString(), xSize - 38, ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_QuantumGenerator.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}