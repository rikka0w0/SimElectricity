package simelectricity.essential.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public interface ISECoverPanelFactory {
    boolean acceptItemStack(ItemStack itemStack);

    ISECoverPanel fromItemStack(ItemStack itemStack);

    boolean acceptNBT(CompoundNBT nbt);

    ISECoverPanel fromNBT(CompoundNBT nbt);
}
