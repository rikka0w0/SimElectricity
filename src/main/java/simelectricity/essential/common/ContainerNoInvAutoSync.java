package simelectricity.essential.common;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import rikka.librikka.container.ContainerNoInventory;
import rikka.librikka.container.ContainerSynchronizer;
import simelectricity.essential.Essential;
import simelectricity.essential.utils.network.MessageContainerSync;

public abstract class ContainerNoInvAutoSync<HOST> extends ContainerNoInventory<HOST> {
	public ContainerNoInvAutoSync(@Nullable Object host, int windowID) {
		this(host, Essential.MODID, windowID);
	}

	public ContainerNoInvAutoSync(@Nullable Object host, String namespace, int windowID) {
		super(host, namespace, windowID);
	}

    public ContainerNoInvAutoSync(@Nullable Object host, ContainerType containerType, int windowID) {
		super(host, containerType, windowID);
	}

	@Override
    public void detectAndSendChanges() {
        Object[] changeList = ContainerSynchronizer.detectChanges(this, ContainerNoInvAutoSync.class, host);

        if (changeList == null)
            return;
        
        Iterator<IContainerListener> iterator = getListeners().iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof ServerPlayerEntity) {
                MessageContainerSync.syncToClient((ServerPlayerEntity) crafter, changeList);
            }
        }
    }
}
