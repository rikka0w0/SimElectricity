package simElectricity.Common.Items.ItemBlocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import simElectricity.API.Common.Items.ItemBlockSE;
import simElectricity.Common.Blocks.BlockWire;

import java.util.List;

public class ItemBlockWire extends ItemBlockSE {
    public ItemBlockWire(Block block) {
        super(block);
        setHasSubtypes(true);
        setUnlocalizedName("ItemBlock_SEWire");
    }

    @Override
    public int getMetadata(int damageValue) {
        return damageValue;
    }


    public String getUnlocalizedName(ItemStack itemstack) {
        return getUnlocalizedName() + "." + BlockWire.subNames[itemstack.getItemDamage()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean p_77624_4_) {
        list.add(StatCollector.translateToLocal("sime.Resistance") + String.valueOf(BlockWire.resistanceList[itemStack.getItemDamage()]) + "\u03a9");
    }
}