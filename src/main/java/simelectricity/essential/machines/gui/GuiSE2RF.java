package simelectricity.essential.machines.gui;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.client.gui.SEGuiContainer;

@SideOnly(Side.CLIENT)
public final class GuiSE2RF extends SEGuiContainer<ContainerSE2RF>{
	public GuiSE2RF(Container container) {
		super(container);
	}

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color

        this.fontRenderer.drawString(I18n.translateToLocal("tile.sime_essential:essential_two_port_electronics.se2rf.name"), 8, 6, 4210752);

        int ybase = 22;
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:buffered_energy"), 18, 85, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getStringWithoutUnit(this.container.bufferedEnergy) + "RF", 85, ybase + 8, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:power_input"), 18, 124, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getPowerStringWithUnit(this.container.actualInputPower), 18, 140, 4210752);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("sime_essential:textures/gui/se2rf.png"));
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}
	
    @Override
    public void initGui() {
        super.initGui();
        
    }

}
