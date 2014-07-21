package simElectricity.Common.Items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.Items.ItemSE;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.Util;

public class ItemWrench extends ItemSE {
    public ItemWrench() {
        super();
        maxStackSize = 1;
        setHasSubtypes(true);
        setUnlocalizedName("Wrench");
        setMaxDamage(256);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r) {
        itemIcon = r.registerIcon("simElectricity:Item_Wrench");
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if ((world.getTileEntity(x, y, z) instanceof IEnergyTile) & (!world.isRemote)) {
            IEnergyTile te = (IEnergyTile) world.getTileEntity(x, y, z);
            ForgeDirection newFacing = Util.getPlayerSight(player).getOpposite();

            if (te.canSetFunctionalSide(newFacing)) {
                te.setFunctionalSide(newFacing);
                Energy.postTileRejoinEvent((TileEntity) te);
                Util.updateTileEntityFunctionalSide((TileEntity) te);
                world.notifyBlocksOfNeighborChange(x, y, z, null);
                itemStack.damageItem(1, player);
            }

            return true;
        } else {
            return false;
        }
    }
}
