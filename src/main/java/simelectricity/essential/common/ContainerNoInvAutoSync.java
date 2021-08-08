package simelectricity.essential.common;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ContainerListener;
import rikka.librikka.container.ContainerNoInventory;
import rikka.librikka.container.ContainerSynchronizer;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.network.MessageContainerSync;

public abstract class ContainerNoInvAutoSync<HOST> extends ContainerNoInventory<HOST> {
	public ContainerNoInvAutoSync(@Nullable HOST host, int windowID) {
		this(host, Essential.MODID, windowID);
	}

	public ContainerNoInvAutoSync(@Nullable HOST host, String namespace, int windowID) {
		super(host, namespace, windowID);
	}

    public ContainerNoInvAutoSync(@Nullable HOST host, MenuType<?> containerType, int windowID) {
		super(host, containerType, windowID);
	}

	@Override
    public void broadcastChanges() {
        Object[] changeList = ContainerSynchronizer.detectChanges(this, ContainerNoInvAutoSync.class, host);

        if (changeList == null)
            return;
        
        Iterator<ContainerListener> iterator = getListeners().iterator();
        while (iterator.hasNext()) {
            ContainerListener crafter = iterator.next();

            if (crafter instanceof ServerPlayer) {
                MessageContainerSync.syncToClient((ServerPlayer) crafter, changeList);
            }
        }
    }
}
