package simElectricity.Client.Gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import simElectricity.Common.Blocks.Container.ContainerElectricFurnace;
import simElectricity.Common.Blocks.TileEntity.TileElectricFurnace;

public class GuiElectricFurnace extends GuiContainer {
    protected TileElectricFurnace tileentity;

    public GuiElectricFurnace(InventoryPlayer inventoryPlayer, TileElectricFurnace tileEntity) {
        super(new ContainerElectricFurnace(inventoryPlayer, tileEntity));
        tileentity = tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        fontRendererObj.drawString(StatCollector.translateToLocal("tile.sime:ElectricFurnace.name"), 8, 6, 4210752);

        //draws "Inventory" or your regional equivalent
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
        fontRendererObj.drawString(String.valueOf(tileentity.progress) + "%", xSize - 36, ySize - 128, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(new ResourceLocation("simElectricity:textures/gui/GUI_ElectricFurnace.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        if (tileentity.progress > 0)
            this.drawTexturedModalRect(x + 66, y + 33, 176, 0, 24 * tileentity.progress / 100, 17);
    }
}
