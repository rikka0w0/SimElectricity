package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public final class GuiVoltageMeter extends SEGuiContainer<ContainerVoltageMeter> {
    private int sqr;

    public GuiVoltageMeter(ContainerVoltageMeter screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.drawString(matrixStack, this.title.getString(), 8, 6, 4210752);
        this.font.drawString(matrixStack, I18n.format("gui.simelectricity.voltage") + ": " + 
        		SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 18, 22, 4210752);
        this.font.drawString(matrixStack, "x10^" + String.valueOf(this.sqr), this.xSize - 38, this.ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int xMouse, int yMouse) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/voltage_meter.png");
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        blit(matrixStack, x, y, 0, 0, this.xSize, this.ySize);


        float v = (float) this.container.voltage;
        if (v == 0)
            this.sqr = 0;
        else
            this.sqr = -1;
        while (v > 1) {
            v /= 10;
            this.sqr++;
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

        blit(matrixStack, x + 20 + nx, y + 36, center - 68 + nx, 166, 135 - nx - mx, 24);

        //Draw the pointer
        blit(matrixStack, x + 88, y + 56, 0, 190, 1, 4);
    }

}
