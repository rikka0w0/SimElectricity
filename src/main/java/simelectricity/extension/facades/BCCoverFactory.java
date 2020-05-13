package simelectricity.extension.facades;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.coverpanel.ISECoverPanel;

import java.lang.reflect.Method;

public class BCCoverFactory implements ISECoverPanelFactory{
	public final Class clsIFacadeItem;
	public final Method mtdgetFacade;
	public final Object objFacadeItem;
	public final Method mtdgetPhasedStates;
	public final Method mtdgetState;
	public final Method mtdisHollow;
	public final Method mtdgetActiveColor;
	public final Method mtdisTransparent;
	public final Method mtdgetBlockState;
	public final Method mtdgetRequiredStack;
	public BCCoverFactory() {
		Class clsIFacadeItem = null;
		Method mtdgetFacade = null;
		Object objFacadeItem = null;
		Method mtdgetPhasedStates = null;
		Method mtdgetState = null;
		Method mtdisHollow = null;
		Method mtdgetActiveColor = null;
		Method mtdisTransparent = null;
		Method mtdgetBlockState = null;
		Method mtdgetRequiredStack = null;

		try {
			clsIFacadeItem = Class.forName("buildcraft.api.facades.IFacadeItem");

			Class clsFacadeAPI = Class.forName("buildcraft.api.facades.FacadeAPI");
			objFacadeItem = clsFacadeAPI.getField("facadeItem").get(null);
			mtdgetFacade = clsIFacadeItem.getMethod("getFacade", ItemStack.class);

			Class clsIFacade = Class.forName("buildcraft.api.facades.IFacade");
			mtdisHollow = clsIFacade.getMethod("isHollow");
			mtdgetPhasedStates = clsIFacade.getMethod("getPhasedStates");

			Class clsIFacadePhasedState = Class.forName("buildcraft.api.facades.IFacadePhasedState");
			mtdgetState = clsIFacadePhasedState.getMethod("getState");
			mtdgetActiveColor = clsIFacadePhasedState.getMethod("getActiveColor");

			Class clsIFacadeState = Class.forName("buildcraft.api.facades.IFacadeState");
			mtdisTransparent = clsIFacadeState.getMethod("isTransparent");
			mtdgetBlockState = clsIFacadeState.getMethod("getBlockState");
			mtdgetRequiredStack = clsIFacadeState.getMethod("getRequiredStack");
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.clsIFacadeItem = clsIFacadeItem;
		this.mtdgetFacade = mtdgetFacade;
		this.objFacadeItem = objFacadeItem;
		this.mtdgetPhasedStates = mtdgetPhasedStates;
		this.mtdgetState = mtdgetState;
		this.mtdisHollow = mtdisHollow;
		this.mtdgetActiveColor = mtdgetActiveColor;
		this.mtdisTransparent = mtdisTransparent;
		this.mtdgetBlockState = mtdgetBlockState;
		this.mtdgetRequiredStack = mtdgetRequiredStack;
	}

	@Override
	public ISECoverPanel from(ItemStack itemStack) {
		if (!clsIFacadeItem.isAssignableFrom(itemStack.getItem().getClass()))
			return null;
		
		try {
			Object facade = mtdgetFacade.invoke(objFacadeItem, itemStack);
			if (facade != null) {
				Object facadePhasedState = ((Object[])mtdgetPhasedStates.invoke(facade))[0];
				Object facadeState = mtdgetState.invoke(facadePhasedState);
				
				boolean isHollow = (boolean) mtdisHollow.invoke(facade);
				BlockState blockState = (BlockState) mtdgetBlockState.invoke(facadeState);
				ItemStack reqStack = (ItemStack) mtdgetRequiredStack.invoke(facadeState);
				
				return new BCFacadePanel(isHollow, blockState, itemStack);
			}
		}catch(Exception e) {
			return null;
		}
		return null;
	}

	@Override
	public ISECoverPanel from(CompoundNBT nbt, Class<? extends ISECoverPanel> panelCls, String coverPanelName) {
		return new BCFacadePanel(nbt);
	}

	@Override
	public String getName() {
		return "BCCoverFactory";
	}
}
