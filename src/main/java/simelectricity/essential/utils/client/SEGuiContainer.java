package simelectricity.essential.utils.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

@SideOnly(Side.CLIENT)
public abstract class SEGuiContainer<TYPE extends Container> extends GuiContainer{
	protected final TYPE container;
	
	public SEGuiContainer(Container container) {
		super(container);
		this.container = (TYPE) container;
	}	
}
