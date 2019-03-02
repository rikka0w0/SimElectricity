package simelectricity.essential.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import simelectricity.api.tile.ISEWireTile;

import java.util.List;

public interface ISEGenericWire extends ISEWireTile, ISEChunkWatchSensitiveTile{
    boolean hasBranch(EnumFacing side, EnumFacing to);
    void addBranch(EnumFacing side, EnumFacing to, ItemStack itemStack);
    void removeBranch(EnumFacing side, EnumFacing to, List<ItemStack> drops);
}
