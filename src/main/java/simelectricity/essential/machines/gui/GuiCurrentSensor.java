package simelectricity.essential.machines.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@OnlyIn(Dist.CLIENT)
public class GuiCurrentSensor<T extends ContainerCurrentSensor> extends SEGuiContainer<T> {
    public GuiCurrentSensor(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.font.drawString(this.title.getFormattedText(), 8, 6, 4210752);

        this.font.drawString(I18n.format("gui.sime_essential.current_threshold"), 18, 85, 4210752);
        this.font.drawString(I18n.format("gui.sime_essential.resistance_internal"), 18, 124, 4210752);

        String conditionString = this.container.absMode ? "|I|" : "I";
        conditionString += this.container.inverted ? "<" : ">";
        conditionString += SEUnitHelper.getCurrentStringWithUnit(this.container.thresholdCurrent);
        
        
        int ybase = 22;
        this.font.drawString(I18n.format("gui.sime_essential.condition_threshold"), 10, ybase, 4210752);
        this.font.drawString(conditionString, 10, ybase + 8, 4210752);
        this.font.drawString("I=" + SEUnitHelper.getCurrentStringWithUnit(this.container.current), 10, ybase + 16, 4210752);
        this.font.drawString("Ron = " + String.format("%.3f", this.container.resistance) + " \u03a9", 10, ybase + 24, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float opacity, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture("textures/gui/current_sensor.png");
        blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        blit(this.guiLeft + 152, this.guiTop + 44, this.container.emitRedstoneSignal ? 180 : 176, 0, 4, 16);

        this.directionSelector.set(this.container.inputSide, this.container.outputSide);
    }

    @Override
    public void init() {
        super.init();

        int xbase = 18;
        int ybase = 97;

        addServerButton(0, this.guiLeft + xbase, this.guiTop + ybase, 30, 20, "-100");
        addServerButton(1, this.guiLeft + xbase + 30, this.guiTop + ybase, 20, 20, "-10");
        addServerButton(2, this.guiLeft + xbase + 50, this.guiTop + ybase, 20, 20, "-1");
        addServerButton(3, this.guiLeft + xbase + 70, this.guiTop + ybase, 20, 20, "+1");
        addServerButton(4, this.guiLeft + xbase + 90, this.guiTop + ybase, 20, 20, "+10");
        addServerButton(5, this.guiLeft + xbase + 110, this.guiTop + ybase, 30, 20, "+100");

        addServerButton(6, this.guiLeft + xbase, this.guiTop + ybase + 38, 20, 20, "-1");
        addServerButton(7, this.guiLeft + xbase + 20, this.guiTop + ybase + 38, 20, 20, "-.1");
        addServerButton(8, this.guiLeft + xbase + 40, this.guiTop + ybase + 38, 30, 20, "-.01");
        addServerButton(9, this.guiLeft + xbase + 70, this.guiTop + ybase + 38, 30, 20, "+.01");
        addServerButton(10, this.guiLeft + xbase + 100, this.guiTop + ybase + 38, 20, 20, "+.1");
        addServerButton(11, this.guiLeft + xbase + 120, this.guiTop + ybase + 38, 20, 20, "+1");

        addServerButton(12, this.guiLeft + xbase + 50, this.guiTop + ybase - 36, 90, 20, "Toggle Behavior");
        addServerButton(13, this.guiLeft + xbase, this.guiTop + ybase - 36, 50, 20, "Abs()");

        this.directionSelector = addDirectionSelector(this.guiLeft + 116, this.guiTop + 20);
    }
}
