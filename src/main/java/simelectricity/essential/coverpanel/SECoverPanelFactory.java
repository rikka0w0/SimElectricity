package simelectricity.essential.coverpanel;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.items.ItemMisc;

public class SECoverPanelFactory implements ISECoverPanelFactory {	
	public SECoverPanelFactory() {
		SEEAPI.coverPanelRegistry.register(this, LedPanel.class);
		SEEAPI.coverPanelRegistry.register(this, VoltageSensorPanel.class);
		SEEAPI.coverPanelRegistry.register(this, FacadePanel.FacadeNormal.class);
		SEEAPI.coverPanelRegistry.register(this, FacadePanel.FacadeHollow.class);
	}
	
	private static String getIdentifier(Class<? extends ISECoverPanel> pCls) {
		return pCls.getName();
	}

    @Override
    public ISECoverPanel from(ItemStack itemStack) {
    	Item item = itemStack.getItem();
        if (item instanceof ItemMisc) {
        	return ((ItemMisc) item).itemType.constructor.get();
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
