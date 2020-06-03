package simelectricity.essential.coverpanel;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.items.ItemPanel;

public class SECoverPanelFactory implements ISECoverPanelFactory {	
	public SECoverPanelFactory() {
		SEEAPI.coverPanelRegistry.register(this, LedPanel.class, null);
		SEEAPI.coverPanelRegistry.register(this, VoltageSensorPanel.class, null);
		SEEAPI.coverPanelRegistry.register(this, FacadePanel.FacadeNormal.class, null);
		SEEAPI.coverPanelRegistry.register(this, FacadePanel.FacadeHollow.class, null);
	}
	
	private static String getIdentifier(Class<? extends ISECoverPanel> pCls) {
		return pCls.getName();
	}

    @Override
    public ISECoverPanel from(ItemStack itemStack) {
    	Item item = itemStack.getItem();
        if (item instanceof ItemPanel) {
        	ItemPanel.ItemType type = ((ItemPanel) item).itemType;
        	if (type == ItemPanel.ItemType.facade) {
        		CompoundNBT bsNBT = itemStack.getTag();
        		BlockState blockstate = Blocks.AIR.getDefaultState();
        		if (bsNBT != null && bsNBT.contains("facade_blockstate")) {
        			bsNBT = bsNBT.getCompound("facade_blockstate");
        			blockstate = NBTUtil.readBlockState(bsNBT);
        		}
        		return new FacadePanel.FacadeNormal(blockstate, itemStack);
        	} else if (type == ItemPanel.ItemType.facade_hollow) {
        		CompoundNBT bsNBT = itemStack.getTag();
        		BlockState blockstate = Blocks.AIR.getDefaultState();
        		if (bsNBT != null && bsNBT.contains("facade_blockstate")) {
        			bsNBT = bsNBT.getCompound("facade_blockstate");
        			blockstate = NBTUtil.readBlockState(bsNBT);
        		}
        		return new FacadePanel.FacadeHollow(blockstate, itemStack);
        	}
        	return ((ItemPanel) item).itemType.constructor.get();
        }

        return null;
    }

    @Override
    public ISECoverPanel from(CompoundNBT nbt, Class<? extends ISECoverPanel> panelCls, String coverPanelName) {
		try {
			return panelCls.getConstructor(CompoundNBT.class).newInstance(nbt);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {

		}

        return null;
    }

	@Override
	public String getName() {
		return "SECoverPanelFactory";
	}
}
