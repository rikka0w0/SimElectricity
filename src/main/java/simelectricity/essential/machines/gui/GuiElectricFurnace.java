package simelectricity.essential.machines.gui;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;
import simelectricity.essential.utils.client.gui.SEGuiContainer;


public class GuiElectricFurnace extends SEGuiContainer<ContainerElectricFurnace>{
    public  GuiElectricFurnace(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.fontRenderer.drawString(I18n.translateToLocal("tile.sime_essential:essential_electronics.electric_furnace.name"), 8, 6, 4210752);

        //draws "Inventory" or your regional equivalent
        this.fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, ySize - 96, 4210752);
        this.fontRenderer.drawString(String.valueOf(container.progress) + "%", xSize - 36, ySize - 128, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/electric_furnace.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        if (container.progress > 0)
            this.drawTexturedModalRect(x + 66, y + 33, 176, 0, 24 * container.progress / 100, 17);
    }
}
