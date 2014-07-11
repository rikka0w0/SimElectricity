package simElectricity.Items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.ISidedFacing;
import simElectricity.API.Util;

public class Item_Glove extends Item {
    public Item_Glove() {
        super();
        maxStackSize = 1;
        setHasSubtypes(true);
        setUnlocalizedName("Item_Glove");
        setMaxDamage(256);
        setCreativeTab(Util.SETab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r) {
        itemIcon = r.registerIcon("simElectricity:Item_Glove");
    }

    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
        if ((world.getTileEntity(x, y, z) instanceof ISidedFacing) & (!world.isRemote)) {
            ISidedFacing te = (ISidedFacing) world.getTileEntity(x, y, z);
            ForgeDirection newFacing = Util.getPlayerSight(player).getOpposite();

            if (te.canSetFacing(newFacing)) {
                te.setFacing(newFacing);
                Util.updateTileEntityFacing((TileEntity) te);
            }

            return true;
        } else {
            return false;
        }
    }
}
