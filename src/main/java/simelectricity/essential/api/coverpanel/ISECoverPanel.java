package simelectricity.essential.api.coverpanel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import simelectricity.essential.api.client.ISECoverPanelRender;

public interface ISECoverPanel {
    float thickness = 0.05F;    //Constant

    /**
     * @return false: prevent other cable/machine from connecting to the side with this cover panel
     */
    boolean isHollow();

    /**
     * Save the cover panel to a NBT object, this should not be called!
     *
     * @param nbt
     */
    void toNBT(CompoundTag nbt);

    @OnlyIn(Dist.CLIENT)
    <T extends ISECoverPanel> ISECoverPanelRender<T> getCoverPanelRender();

    /**
     * Called when the cover panel is loaded from NBT data or placed by a player using itemStack
     *
     * @param hostTileEntity
     * @param side
     */
    void setHost(BlockEntity hostTileEntity, Direction side);

    /**
     * @return a copy or new instance of the item to be dropped into the world
     */
    ItemStack getDroppedItemStack();
}
