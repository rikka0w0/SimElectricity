package simelectricity.essential.api.coverpanel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public interface ISEGuiCoverPanel extends ISECoverPanel {
	Container getContainer(EntityPlayer player, TileEntity te);
}
