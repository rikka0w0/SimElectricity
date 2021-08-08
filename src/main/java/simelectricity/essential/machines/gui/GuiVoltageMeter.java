package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;

import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public final class GuiVoltageMeter extends SEGuiContainer<ContainerVoltageMeter> {
    private int sqr;

    public GuiVoltageMeter(ContainerVoltageMeter screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);
        this.font.draw(matrixStack, I18n.get("gui.simelectricity.voltage") + ": " + 
        		SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 18, 22, 4210752);
        this.font.draw(matrixStack, "x10^" + String.valueOf(this.sqr), this.imageWidth - 38, this.imageHeight - 96, 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int xMouse, int yMouse) {
        //draw your Gui here, only thing you need to change is the path
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/voltage_meter.png");
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);


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
