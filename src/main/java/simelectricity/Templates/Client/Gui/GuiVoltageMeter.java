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

package simelectricity.Templates.Client.Gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import simelectricity.Templates.Container.ContainerVoltageMeter;
import simelectricity.Templates.Utils.IGuiSyncHandler;

@SideOnly(Side.CLIENT)
public class GuiVoltageMeter extends GuiContainer implements IGuiSyncHandler{
	private double voltage;
	
    protected TileEntity tileentity;
    int sqr = 0;

    public GuiVoltageMeter(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        super(new ContainerVoltageMeter(inventoryPlayer, tileEntity));
        tileentity = tileEntity;
    }

    String float2Str(float f, int dig) {
        return String.valueOf(((int) (f * dig)) / dig) + "." + String.valueOf((int) (f * dig) - ((int) (f * dig)) / dig * dig);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:VoltageMeter.name"), 8, 6, 4210752);
        fontRendererObj.drawString("Voltage: " + voltage + "V", 18, 22, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
        fontRendererObj.drawString("x10^" + String.valueOf(sqr), xSize - 38, ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_VoltageMeter.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        float v = (float) voltage;
        if (v == 0)
            sqr = 0;
        else
            sqr = -1;
        while (v > 1) {
            v /= 10;
            sqr++;
        }
        v *= 10;

        //Display v on the meter... Hard coding......
        int center = (int) (v * 25 + 2);
        int nx = 0;
        if (center - 68 < 0)
            nx = 68 - center;

        int mx = 0;
        if (center + 68 - 255 > 0)
            mx = center + 68 - 255;

        this.drawTexturedModalRect(x + 20 + nx, y + 36, center - 68 + nx, 166, 135 - nx - mx, 24);

        //Draw the pointer
        this.drawTexturedModalRect(x + 88, y + 56, 0, 190, 1, 4);
    }

	@Override
	public void onGuiEvent(byte eventID, Object[] data) {
		voltage = (Double)data[0];
	}
}