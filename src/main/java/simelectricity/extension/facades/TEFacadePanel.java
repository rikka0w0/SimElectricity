package simelectricity.extension.facades;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.coverpanel.FacadePanel;

public class TEFacadePanel extends FacadePanel{
    public TEFacadePanel(CompoundNBT nbt) {
    	super(nbt);
    }

    public TEFacadePanel(BlockState blockState, ItemStack itemStack){
        super(true, blockState, itemStack);
    }
}
