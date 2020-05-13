package simelectricity.extension.facades;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.coverpanel.FacadePanel;

public class BCFacadePanel extends FacadePanel{
    public BCFacadePanel(CompoundNBT nbt) {
    	super(nbt);
    }

    public BCFacadePanel(boolean isHollow, BlockState blockState, ItemStack itemStack){
        super(isHollow, blockState, itemStack);
    }
}
