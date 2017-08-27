package simelectricity.essential.utils.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.utils.network.MessageContainerSync;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public abstract class SEGuiContainer<TYPE extends Container> extends GuiContainer {
    protected final TYPE container;
    protected GuiDirectionSelector directionSelector;

    public SEGuiContainer(Container container) {
        super(container);
        this.container = (TYPE) container;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        MessageContainerSync.sendButtonClickEventToSever(this.container, button.id, GuiScreen.isCtrlKeyDown());
    }

    @Override
    public void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        if (this.directionSelector == null)
            return;

        EnumFacing selectedDirection = this.directionSelector.onMouseClick(x, y);

        if (selectedDirection == null)
            return;

        MessageContainerSync.sendDirectionSelectorClickEventToSever(this.container, selectedDirection, button);
    }
}
