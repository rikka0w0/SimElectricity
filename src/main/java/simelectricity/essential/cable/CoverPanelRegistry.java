package simelectricity.essential.cable;

import java.util.LinkedList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import simelectricity.essential.api.ISECoverPanel;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.internal.ISECoverPanelRegistry;

public class CoverPanelRegistry implements ISECoverPanelRegistry{
	public static ISECoverPanelRegistry instance;
	private final LinkedList<ISECoverPanelFactory> factories = new LinkedList<ISECoverPanelFactory>();
	
	public CoverPanelRegistry(){
		this.instance = this;
	}
	
	@Override
	public void registerCoverPanelFactory(ISECoverPanelFactory factory){
		if (factories.contains(factory))
			return;
		
		factories.add(factory);
	}
	
	@Override
	public ISECoverPanel fromNBT(NBTTagCompound nbt){
		ISECoverPanelFactory selectedFactory = null;
		for (ISECoverPanelFactory factory: factories){
			if (factory.acceptNBT(nbt)){
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
		for (ISECoverPanelFactory factory: factories){
			if (factory.acceptItemStack(itemStack)){
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

	@Override
	public ItemStack toItemStack(ISECoverPanel coverPanel) {
		ISECoverPanelFactory selectedFactory = null;
		for (ISECoverPanelFactory factory: factories){
			if (factory.acceptCoverPanel(coverPanel)){
				if (selectedFactory == null)
					selectedFactory = factory;
				else
					throw new RuntimeException("More than one ISECoverPanelFactory accepts the ItemStack!");
			}
		}
		
		if (selectedFactory == null)
			return null;
		
		return selectedFactory.getItemStack(coverPanel);
	}
}
