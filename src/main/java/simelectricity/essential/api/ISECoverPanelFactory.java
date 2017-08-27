package simelectricity.essential.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

public interface ISECoverPanelFactory {
    boolean acceptItemStack(ItemStack itemStack);

    ISECoverPanel fromItemStack(ItemStack itemStack);

    boolean acceptNBT(NBTTagCompound nbt);

    ISECoverPanel fromNBT(NBTTagCompound nbt);
}
