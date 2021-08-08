package simelectricity.extension.facades;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import simelectricity.essential.coverpanel.FacadePanel;

public class TEFacadePanel extends FacadePanel{
    public TEFacadePanel(CompoundTag nbt) {
    	super(nbt);
    }

    public TEFacadePanel(BlockState blockState, ItemStack itemStack){
        super(true, blockState, itemStack);
    }
}
