package simelectricity.essential.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class ContainerNoInventory<TYPE extends TileEntity> extends Container {
    protected TYPE tileEntity;

    public ContainerNoInventory(TileEntity tileEntity) {
        this.tileEntity = (TYPE) tileEntity;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public abstract void detectAndSendChanges();
}
