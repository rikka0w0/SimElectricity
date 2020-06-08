package simelectricity.essential.utils.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.gui.GuiDirectionSelector;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.network.MessageContainerSync;

@OnlyIn(Dist.CLIENT)
public abstract class SEGuiContainer<TC extends Container> extends ContainerScreen<TC> {
    protected final TC container;
    protected GuiDirectionSelector directionSelector;
    private static final ResourceLocation dsTexture = new ResourceLocation("sime_essential:textures/gui/direction_selector.png");

    public SEGuiContainer(TC screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    protected Button addServerButton(int id, int widthIn, int heightIn, int width, int height, String text) {
        return this.addButton(new Button(widthIn, heightIn, width, height, text, (button) -> {
        	MessageContainerSync.sendButtonClickEventToSever(container, id, Screen.hasControlDown());
        }));
    }
    
    protected GuiDirectionSelector addDirectionSelector(int x, int y) {
        final SEGuiContainer<TC> parent = this; 
        GuiDirectionSelector directionSelector = new GuiDirectionSelector(x, y) {
        	@Override
        	protected void onClick(Direction selectedDirection, int mouseButton) {
        		MessageContainerSync.sendDirectionSelectorClickEventToSever(parent.getContainer(), selectedDirection, mouseButton);
        	}

			@Override
			protected ResourceLocation texture() {
				return dsTexture;
			}

			@Override
			protected String localize(Direction direction) {
				return direction.getName();
			}
        };
        return this.addButton(directionSelector);
    }
    
    protected void bindTexture(String name) {
    	bindTexture(Essential.MODID, name);
    }
    
    protected void bindTexture(String namespace, String name) {
    	bindTexture(new ResourceLocation(namespace,name));
    }    
    
    protected void bindTexture(ResourceLocation resLoc) {
    	this.minecraft.getTextureManager().bindTexture(resLoc);
    }
}
