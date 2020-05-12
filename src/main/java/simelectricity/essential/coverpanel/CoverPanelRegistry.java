package simelectricity.essential.coverpanel;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.internal.ISECoverPanelRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

public enum CoverPanelRegistry implements ISECoverPanelRegistry {
    INSTANCE;
    
	private final Map<String, ISECoverPanelFactory> factories = new HashMap<>();
	private final Map<String, Pair<ISECoverPanelFactory, Class<? extends ISECoverPanel>>> panels = new HashMap<>();

	@Override
	public void register(ISECoverPanelFactory factory, Class<? extends ISECoverPanel> panel) {
		String name = panel.getSimpleName();
		if (name == null)
			name = panel.getName();	// Anonymous inner class
		
		register(factory, panel, name);
	}
	
	@Override
	public void register(ISECoverPanelFactory factory, Class<? extends ISECoverPanel> panel, String name) {
		String factoryName = factory.getName();
		
		this.panels.put(factoryName + ":" + name, Pair.of(factory, panel));
        this.factories.put(factoryName, factory);
	}
	
	@Override
	public void saveToNBT(ISECoverPanel panel, CompoundNBT nbt) {
		for (Entry<String, Pair<ISECoverPanelFactory, Class<? extends ISECoverPanel>>> entry: panels.entrySet()) {
			Pair<ISECoverPanelFactory, Class<? extends ISECoverPanel>> pair = entry.getValue();
			if (pair.getRight() == panel.getClass()) {
				nbt.putString("factory_name", pair.getLeft().getName());
				nbt.putString("coverpanel_name", entry.getKey());
				panel.toNBT(nbt);
				return;
			}
		}
		throw new RuntimeException("CoverPanel is not registered!");
	}
	
	@Override
    public ISECoverPanel fromNBT(CompoundNBT nbt) {
    	String factoryName = nbt.getString("factory_name");
    	ISECoverPanelFactory factory = factories.get(factoryName);
    	if (factory == null)
    		return null;
    	
        String coverPanelName = nbt.getString("coverpanel_name");
        Pair<ISECoverPanelFactory, Class<? extends ISECoverPanel>> pair = panels.get(coverPanelName);
        return factory.from(nbt, pair.getRight(), coverPanelName);
    }

    @Override
    public ISECoverPanel fromItemStack(ItemStack itemStack) {
        ISECoverPanelFactory selectedFactory = null;
        ISECoverPanel result = null;
        for (ISECoverPanelFactory factory : this.factories.values()) {
        	result = factory.from(itemStack);
            if (result != null) {
                if (selectedFactory == null)
                    selectedFactory = factory;
                else
                    throw new RuntimeException("More than one ISECoverPanelFactory accepts the ItemStack!");
            }
        }

        if (selectedFactory == null)
            return null;

        return result;
    }
}
