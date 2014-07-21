package simElectricity.Blocks.Client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import simElectricity.Blocks.ContainerVoltageMeter;
import simElectricity.Blocks.TileVoltageMeter;

public class GuiVoltageMeter extends GuiContainer {
    protected TileVoltageMeter tileentity;
    int sqr = 0;

    public GuiVoltageMeter(InventoryPlayer inventoryPlayer, TileVoltageMeter tileEntity) {
        super(new ContainerVoltageMeter(inventoryPlayer, tileEntity));
        tileentity = tileEntity;
    }

    String float2Str(float f, int dig) {
        return String.valueOf(((int) (f * dig)) / dig) + "." + String.valueOf((int) (f * dig) - ((int) (f * dig)) / dig * dig);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.VoltageMeter.name"), 8, 6, 4210752);
        fontRendererObj.drawString("Voltage: " + tileentity.voltage + "V", 18, 22, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
        fontRendererObj.drawString("x10^" + String.valueOf(sqr), xSize - 38, ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_VoltageMeter.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        float v = tileentity.voltage;
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
}