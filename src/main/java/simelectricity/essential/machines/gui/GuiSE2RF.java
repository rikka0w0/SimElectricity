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

        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:voltage_input"), 8, 22, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getVoltageStringWithUnit(this.container.voltage), 8, 30, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:power_input"), 8, 38, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getPowerStringWithUnit(this.container.actualInputPower), 8, 46, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("gui.sime:buffered_energy"), 8, 54, 4210752);
        this.fontRenderer.drawString(SEUnitHelper.getEnergyStringInJ(this.container.bufferedEnergy), 8, 62, 4210752);

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
