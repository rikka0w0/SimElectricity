package simelectricity.essential.coverpanel;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;
import simelectricity.essential.api.internal.ISECoverPanelRegistry;
import simelectricity.essential.client.coverpanel.GenericFacadeRender;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

public enum CoverPanelRegistry implements ISECoverPanelRegistry {
    INSTANCE;
    
	private final Map<String, ISECoverPanelFactory> factories = new HashMap<>();
	private final Map<String, Pair<ISECoverPanelFactory, Class<? extends ISECoverPanel>>> panels = new HashMap<>();
	private final List<Block> coloredBlocks = new LinkedList<>();
	
	@Override
	public void register(ISECoverPanelFactory factory, Class<? extends ISECoverPanel> panel, String name) {
		String factoryName = factory.getName();
		if (name == null) {
			panel.getSimpleName();
			if (name == null)
				name = panel.getName();	// Anonymous inner class
		}
		
		this.panels.put(factoryName + ":" + name, Pair.of(factory, panel));
        this.factories.put(factoryName, factory);
	}
	
	@Override
	public void saveToNBT(ISECoverPanel panel, CompoundTag nbt) {
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
    public ISECoverPanel fromNBT(CompoundTag nbt) {
		if (nbt == null)
			return null;
		
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

	@Override
	public ISECoverPanelRender<ISEFacadeCoverPanel> getGenericFacadeRender() {
		return GenericFacadeRender.instance;
	}

	@Override
	public void registerColoredFacadeHost(Block... blocks) {
		for (Block block: blocks)
			this.coloredBlocks.add(block);
	}
	
	public void registerAllColoredFacadeHost() {
		for (Block block: this.coloredBlocks)
			Essential.proxy.registerColoredFacadeHost(block);
	}
}
