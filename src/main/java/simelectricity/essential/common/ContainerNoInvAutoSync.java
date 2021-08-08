package simelectricity.essential.common;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.player.Player;
import rikka.librikka.container.ContainerNoInventory;
import rikka.librikka.container.ContainerSynchronizer;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.network.MessageContainerSync;

public abstract class ContainerNoInvAutoSync<HOST> extends ContainerNoInventory<HOST> {
	public ContainerNoInvAutoSync(@Nullable HOST host, int windowID, Player player) {
		this(host, Essential.MODID, windowID, player);
	}

	public ContainerNoInvAutoSync(@Nullable HOST host, String namespace, int windowID, Player player) {
		super(host, namespace, windowID);
        this.player = player;
	}

    public ContainerNoInvAutoSync(@Nullable HOST host, MenuType<?> containerType, int windowID, Player player) {
		super(host, containerType, windowID);
        this.player = player;
	}

    protected final Player player;

	@Override
    public void broadcastChanges() {
        Object[] changeList = ContainerSynchronizer.detectChanges(this, ContainerNoInvAutoSync.class, host);

        if (changeList == null)
            return;

        MessageContainerSync.syncToClient((ServerPlayer) this.player, changeList);
    }
}
