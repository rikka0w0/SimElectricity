package simelectricity.essential.common;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import rikka.librikka.container.ContainerNoInventory;
import rikka.librikka.container.ContainerSynchronizer;
import simelectricity.essential.utils.network.MessageContainerSync;

public abstract class ContainerNoInvAutoSync<HOST> extends ContainerNoInventory<HOST> {
    public ContainerNoInvAutoSync(Object host) {
		super(host);
	}

	@Override
    public void detectAndSendChanges() {
        Object[] changeList = ContainerSynchronizer.detectChanges(this, ContainerNoInvAutoSync.class, host);

        if (changeList == null)
            return;

        Iterator<IContainerListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            IContainerListener crafter = iterator.next();

            if (crafter instanceof EntityPlayerMP) {
                MessageContainerSync.syncToClient((EntityPlayerMP) crafter, changeList);
            }
        }
    }
}
