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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.Container.ContainerQuantumGenerator;
import simElectricity.Common.Blocks.TileEntity.TileAdjustableResistor;

@SideOnly(Side.CLIENT)
public class GuiAdjustableResistor extends GuiContainer {
    protected TileAdjustableResistor te;

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(0, guiLeft + 96, guiTop + 18, 20, 20, "--"));
        buttonList.add(new GuiButton(1, guiLeft + 116, guiTop + 18, 16, 20, "-"));
        buttonList.add(new GuiButton(2, guiLeft + 132, guiTop + 18, 16, 20, "+"));
        buttonList.add(new GuiButton(3, guiLeft + 148, guiTop + 18, 20, 20, "++"));

        buttonList.add(new GuiButton(4, guiLeft + 128, guiTop + 42, 40, 20, "Clear"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                if (GuiScreen.isCtrlKeyDown())
                    te.resistance -= 100;
                else
                    te.resistance -= 10;
                break;
            case 1:
                if (GuiScreen.isCtrlKeyDown())
                    te.resistance -= 0.1;
                else
                    te.resistance -= 1;
                break;
            case 2:
                if (GuiScreen.isCtrlKeyDown())
                    te.resistance += 0.1;
                else
                    te.resistance += 1;
                break;
            case 3:
                if (GuiScreen.isCtrlKeyDown())
                    te.resistance += 100;
                else
                    te.resistance += 10;
                break;
            default:
                te.powerConsumed = 0;
                Util.networkManager.updateTileEntityFieldsToServer(te, "powerConsumed");
        }

        if (te.resistance < 0.1)
            te.resistance = 0.1F;
        if (te.resistance > 10000)
            te.resistance = 10000;
        if (button.id < 4)
        	Util.networkManager.updateTileEntityFieldsToServer(te, "resistance");
    }

    public GuiAdjustableResistor(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new ContainerQuantumGenerator(inventoryPlayer, tileEntity));
        te = (TileAdjustableResistor) tileEntity;
    }

    String float2Str(float f, int dig) {
        return String.valueOf(((int) (f * dig)) / dig) + "." + String.valueOf((int) (f * dig) - ((int) (f * dig)) / dig * dig);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:AdjustableResistor.name"), 8, 6, 4210752);

        fontRendererObj.drawString(String.format("%.1f", te.resistance) + " \u03a9", 30, 24, 4210752);
        fontRendererObj.drawString(String.format("%.1f", te.power) + " W", 30, 37, 4210752);
        fontRendererObj.drawString(String.format("%.0f", te.powerConsumed) + " J", 30, 50, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_AdjustableResistor.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}