package simelectricity.extension.buildcraft;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import buildcraft.api.facades.IFacadeItem;
import buildcraft.transport.item.ItemPluggableFacade;
import buildcraft.transport.plug.FacadeStateManager.FacadePhasedState;

public class BCCoverFactory implements ISECoverPanelFactory{

	@Override
	public boolean acceptItemStack(ItemStack itemStack) {
		Item item = itemStack.getItem();
		
		return (item instanceof IFacadeItem);
	}

	private static boolean isHollow(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemPluggableFacade) {
			FacadePhasedState[] phasedStates = ItemPluggableFacade.getStates(itemStack).phasedStates;
			FacadePhasedState phasedState = phasedStates[0];
			return phasedState.isHollow;
		}
		return true;
	}
	
	@Override
	public ISECoverPanel fromItemStack(ItemStack itemStack) {
		Item item = itemStack.getItem();
			
		if (item instanceof IFacadeItem){
			IFacadeItem facadeItem = (IFacadeItem) item;
			IBlockState[] blockStates = facadeItem.getBlockStatesForFacade(itemStack);

			if (blockStates.length < 1)
				return null;
			
			IBlockState blockState = blockStates[0];
			boolean isHollow = isHollow(itemStack);
			
			return new BCFacadePanel(isHollow, blockState, itemStack);
		}
		
		return null;
	}

	@Override
	public boolean acceptNBT(NBTTagCompound nbt) {
		return nbt.getString("coverPanelType").equals("BCFacade");
	}

	@Override
	public ISECoverPanel fromNBT(NBTTagCompound nbt) {
		return new BCFacadePanel(nbt);
	}

	@Override
	public boolean acceptCoverPanel(ISECoverPanel coverPanel) {
		return coverPanel instanceof BCFacadePanel;
	}

	@Override
	public ItemStack getItemStack(ISECoverPanel coverPanel) {
		BCFacadePanel facadePanel = (BCFacadePanel) coverPanel;
		return facadePanel.getItemStack();
	}
}
