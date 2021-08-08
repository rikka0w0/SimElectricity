package simelectricity.essential.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.data.ModelProperty;
import simelectricity.api.tile.ISEWireTile;

import java.util.List;

public interface ISEGenericWire extends ISEWireTile, ISEChunkWatchSensitiveTile{
	public static ModelProperty<ISEGenericWire> prop = new ModelProperty<>();
	
    // Server-Only functions
    void addBranch(Direction side, Direction to, ItemStack itemStack, double resistance);
    void removeBranch(Direction side, Direction to, List<ItemStack> drops);
    /**
     * Drop the wire on the given side as item, note that the number of item maybe more than 1!
     */
    ItemStack getItemDrop(Direction side);

    // Common functions
    default boolean hasBranch(Direction side, Direction to) {
        return getWireParam(side).hasBranchOnSide(to);
    }
    boolean canAddBranch(Direction side, Direction to, ItemStack itemStack);

    // The following functions are more likely to be called on client-side for rendering purpose

    /**
     * @return true if the wire has a exterior connection on the given side
     */
    boolean hasExtConnection(Direction f1, Direction f2);

    /**
     * Called by cable render (may be custom implementation) to
     * determine if the cable block has connection on the given side
     * @param side
     * @return ture if electrically connected
     */
    boolean connectedOnSide(Direction side);
}
