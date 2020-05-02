package simelectricity.essential.coverpanel;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.internal.ISECoverPanelRegistry;

import java.util.LinkedList;

public enum CoverPanelRegistry implements ISECoverPanelRegistry {
    INSTANCE;
    
	private final LinkedList<ISECoverPanelFactory> factories = new LinkedList<ISECoverPanelFactory>();

    @Override
    public void registerCoverPanelFactory(ISECoverPanelFactory factory) {
        if (this.factories.contains(factory))
            return;

        this.factories.add(factory);
    }

    @Override
    public ISECoverPanel fromNBT(CompoundNBT nbt) {
        ISECoverPanelFactory selectedFactory = null;
        for (ISECoverPanelFactory factory : this.factories) {
            if (factory.acceptNBT(nbt)) {
                if (selectedFactory == null)
                    selectedFactory = factory;
                else
                    throw new RuntimeException("More than one ISECoverPanelFactory accepts the NBT tag!");
            }
        }

        if (selectedFactory == null)
            return null;

        return selectedFactory.fromNBT(nbt);
    }

    @Override
    public ISECoverPanel fromItemStack(ItemStack itemStack) {
        ISECoverPanelFactory selectedFactory = null;
        for (ISECoverPanelFactory factory : this.factories) {
            if (factory.acceptItemStack(itemStack)) {
                if (selectedFactory == null)
                    selectedFactory = factory;
                else
                    throw new RuntimeException("More than one ISECoverPanelFactory accepts the ItemStack!");
            }
        }

        if (selectedFactory == null)
            return null;

        return selectedFactory.fromItemStack(itemStack);
    }
}
