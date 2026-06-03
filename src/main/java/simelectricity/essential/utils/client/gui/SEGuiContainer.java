package simelectricity.essential.utils.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import rikka.librikka.gui.GuiDirectionSelector;
import simelectricity.essential.utils.network.MessageContainerSync;

@OnlyIn(Dist.CLIENT)
public abstract class SEGuiContainer<TC extends AbstractContainerMenu> extends AbstractContainerScreen<TC> {
    protected final TC container;
    protected GuiDirectionSelector directionSelector;
    private static final ResourceLocation dsTexture = ResourceLocation.parse("sime_essential:textures/gui/direction_selector.png");

    public SEGuiContainer(TC screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
    }

    protected Button addServerButton(int id, int x, int y, int width, int height, String text) {
    	return addServerButton(id, x, y, width, height, net.minecraft.network.chat.Component.literal(text));
    }

    protected Button addServerButton(int id, int x, int y, int width, int height, Component text) {
        return this.addRenderableWidget(Button.builder(text, (button) -> {
        	MessageContainerSync.sendButtonClickEventToSever(container, id, Screen.hasControlDown());
        }).bounds(x, y, width, height).build());
    }

    protected GuiDirectionSelector addDirectionSelector(int x, int y) {
        final SEGuiContainer<TC> parent = this;
        GuiDirectionSelector directionSelector = new GuiDirectionSelector(x, y) {
        	@Override
        	protected void onClick(Direction selectedDirection, int mouseButton) {
        		MessageContainerSync.sendDirectionSelectorClickEventToSever(parent.getMenu(), selectedDirection, mouseButton);
        	}

			@Override
			protected ResourceLocation texture() {
				return dsTexture;
			}
        };
        return this.addRenderableWidget(directionSelector);
    }
}
