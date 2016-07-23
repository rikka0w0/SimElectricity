package simElectricity.API.Internal;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidUtil {
    /**
     * Find a stack of empty containers from a stack of filled containers
     *
     * @param filledContainer       a itemStack containing some filled containers
     * @return a itemStack containing corresponding empty containers
     */
	ItemStack getEmptyContainer(ItemStack filledContainer);
	
    /**
     * Attempt to drain some liquid from itemStacks with in a machine
     *
     * @param maxVolume a itemStack containing some filled containers
     * @param currentFluidStack The fluidStack within the machine, that is the amount of liquid currently stored
     * @param slots The itemStack within the machine
     * @param filledSlotID The slot used to place filled containers
     * @param emptySlotID The slot used to place empty containers
     * @return a NEW liquidStack, the result can be null
     */
	FluidStack drainContainer(int maxVolume,FluidStack currentFluidStack,ItemStack[] slots,int filledSlotID,int emptySlotID);
}
