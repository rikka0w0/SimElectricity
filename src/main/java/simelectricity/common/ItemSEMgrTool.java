package simelectricity.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import simelectricity.api.SEAPI;

public class ItemSEMgrTool extends Item {
    public static final String name = "semanagementtool";

    public ItemSEMgrTool() {
        this.setUnlocalizedName(ItemSEMgrTool.name);
        this.setRegistryName(ItemSEMgrTool.name);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(SEAPI.SETab);
        this.maxStackSize = 1;
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack itemStack) {
        return super.getUnlocalizedNameInefficiently(itemStack).replaceAll("item.", "item.sime:");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }
}
