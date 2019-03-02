package simelectricity.essential.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.tile.ISEWireTile;

import java.util.List;

public interface ISEGenericWire extends ISEWireTile, ISEChunkWatchSensitiveTile{
    boolean hasBranch(EnumFacing side, EnumFacing to);
    void addBranch(EnumFacing side, EnumFacing to, ItemStack itemStack);
    void removeBranch(EnumFacing side, EnumFacing to, List<ItemStack> drops);

    @SideOnly(Side.CLIENT)
    boolean hasExtConnection(EnumFacing f1, EnumFacing f2);

    /**
     * Called by cable render (may be custom implementation) to
     * determine if the cable block has connection on the given side
     * @param side
     * @return ture if electrically connected
     */
    boolean connectedOnSide(EnumFacing side);

}
