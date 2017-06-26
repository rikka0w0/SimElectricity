package simelectricity.essential.utils.client;

import simelectricity.essential.utils.network.MessageContainerSync;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public abstract class SEGuiContainer<TYPE extends Container> extends GuiContainer{
	protected final TYPE container;
	protected GuiDirectionSelector directionSelector;
	
	public SEGuiContainer(Container container) {
		super(container);
		this.container = (TYPE) container;
	}
	
    @Override
    public void actionPerformed(GuiButton button) {
    	MessageContainerSync.sendButtonClickEventToSever(container, button.id, GuiScreen.isCtrlKeyDown());
    }
    
    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        
        if (directionSelector == null)
        	return;
        
        ForgeDirection selectedDirection = directionSelector.onMouseClick(x, y);

        if (selectedDirection == ForgeDirection.UNKNOWN)
            return;
        
        MessageContainerSync.sendDirectionSelectorClickEventToSever(container, selectedDirection, button);
    }
}
