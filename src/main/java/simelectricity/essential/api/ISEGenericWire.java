package simelectricity.essential.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import simelectricity.api.tile.ISEWireTile;

import java.util.List;

public interface ISEGenericWire extends ISEWireTile, ISEChunkWatchSensitiveTile{
    // Server-Only functions
    void addBranch(EnumFacing side, EnumFacing to, ItemStack itemStack, double resistance);
    void removeBranch(EnumFacing side, EnumFacing to, List<ItemStack> drops);
    /**
     * Drop the wire on the given side as item, note that the number of item maybe more than 1!
     */
    ItemStack getItemDrop(EnumFacing side);

    // Common functions
    default boolean hasBranch(EnumFacing side, EnumFacing to) {
        return getWireParam(side).hasBranchOnSide(to);
    }
    boolean canAddBranch(EnumFacing side, EnumFacing to, ItemStack itemStack);

    // The following functions are more likely to be called on client-side for rendering purpose

    /**
     * @return true if the wire has a exterior connection on the given side
     */
    boolean hasExtConnection(EnumFacing f1, EnumFacing f2);

    /**
     * Called by cable render (may be custom implementation) to
     * determine if the cable block has connection on the given side
     * @param side
     * @return ture if electrically connected
     */
    boolean connectedOnSide(EnumFacing side);

    int getWireType(EnumFacing side);
}
