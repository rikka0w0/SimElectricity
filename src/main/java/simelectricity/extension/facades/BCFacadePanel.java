package simelectricity.extension.facades;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import simelectricity.essential.coverpanel.FacadePanel;

public class BCFacadePanel extends FacadePanel{
    public BCFacadePanel(CompoundTag nbt) {
    	super(nbt);
    }

    public BCFacadePanel(boolean isHollow, BlockState blockState, ItemStack itemStack){
        super(isHollow, blockState, itemStack);
    }
}
