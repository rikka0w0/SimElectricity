package simelectricity.essential.api.coverpanel;

import simelectricity.essential.api.client.ISECoverPanelRender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISECoverPanel {
	public static final float thickness = 0.05F;	//Constant
	
	/**
	 * @return false: prevent other cable/machine from connecting to the side with this cover panel
	 */
	boolean isHollow();
	
	/**
	 * Save the cover panel to a NBT object
	 * @param nbt
	 */
	void toNBT(NBTTagCompound nbt);
	
	@SideOnly(Side.CLIENT)
	ISECoverPanelRender getCoverPanelRender();
	
	
	/**
	 * Called when the cover panel is loaded from NBT data or placed by a player using itemStack
	 * @param hostTileEntity
	 * @param side
	 */
	void setHost(TileEntity hostTileEntity, EnumFacing side);
	
	/**
	 * @return a copy or new instance of the item to be dropped into the world
	 */
	ItemStack getDroppedItemStack();
}
