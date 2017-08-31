package rikka.librikka.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;

public interface IGuiProviderTile {
	Container getContainer(EntityPlayer player, EnumFacing side);
}
