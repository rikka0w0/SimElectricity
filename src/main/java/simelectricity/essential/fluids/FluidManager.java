package simelectricity.essential.fluids;

import simelectricity.essential.ItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidManager {
	public static void registerFluids(){
		FluidContainerRegistry.registerFluidContainer(registerFluid("vita_tea"), new ItemStack(ItemRegistry.itemVitaTea,1,0));		
	}
	
	public static Fluid registerFluid(String fluidName){
		Fluid fluid = new Fluid(fluidName);
		FluidRegistry.registerFluid(fluid);
		SEBlockFluid blockFluid = new SEBlockFluid(fluid, fluidName);
		return fluid;
	}
}
