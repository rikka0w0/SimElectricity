package simElectricity.Blocks;


import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockWire extends ItemBlock {
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
    	list.add("Resistance: "+String.valueOf(BlockWire.resistanceList[itemStack.getItemDamage()])+"\u03a9");
    }
}