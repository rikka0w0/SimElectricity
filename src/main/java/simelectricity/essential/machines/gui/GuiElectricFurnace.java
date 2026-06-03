package simelectricity.essential.machines.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

import simelectricity.essential.Essential;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public class GuiElectricFurnace extends SEGuiContainer<ContainerElectricFurnace>{
	private static final ResourceLocation bgTexture =
			ResourceLocation.fromNamespaceAndPath(Essential.MODID, "textures/gui/electric_furnace.png");
    public GuiElectricFurnace(ContainerElectricFurnace screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        guiGraphics.drawString(this.font, title.getString(), 8, 6, 4210752, false);

        //draws "Inventory" or your regional equivalent
        guiGraphics.drawString(this.font, I18n.get("container.inventory"), 8, imageHeight - 96, 4210752, false);
        guiGraphics.drawString(this.font, String.valueOf(container.progress) + "%", imageWidth - 36, imageHeight - 128, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int xMouse, int yMouse) {
        //draw your Gui here, only thing you need to change is the path
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, bgTexture);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(bgTexture, x, y, 0, 0, imageWidth, imageHeight);

        if (container.progress > 0)
            guiGraphics.blit(bgTexture, x + 66, y + 33, 176, 0, 24 * container.progress / 100, 17);
    }
}
