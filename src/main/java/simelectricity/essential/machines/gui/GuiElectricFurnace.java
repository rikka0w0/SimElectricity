package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public class GuiElectricFurnace<T extends ContainerElectricFurnace> extends SEGuiContainer<T>{
    public GuiElectricFurnace(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.drawString(title.getFormattedText(), 8, 6, 4210752);

        //draws "Inventory" or your regional equivalent
        this.font.drawString(I18n.format("container.inventory"), 8, ySize - 96, 4210752);
        this.font.drawString(String.valueOf(container.progress) + "%", xSize - 36, ySize - 128, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/electric_furnace.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.blit(x, y, 0, 0, xSize, ySize);

        if (container.progress > 0)
            this.blit(x + 66, y + 33, 176, 0, 24 * container.progress / 100, 17);
    }
}
