package simelectricity.essential.api.internal;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public interface ISECoverPanelRegistry {
    void register(ISECoverPanelFactory factory, Class<? extends ISECoverPanel> panelCls);
    
    void register(ISECoverPanelFactory factory, Class<? extends ISECoverPanel> panelCls, String name);
    
    ISECoverPanel fromItemStack(ItemStack itemStack);

    ISECoverPanel fromNBT(CompoundNBT nbt);
    
    void saveToNBT(ISECoverPanel panel, CompoundNBT nbt);
}
