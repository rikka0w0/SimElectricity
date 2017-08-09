package simelectricity.common;

import simelectricity.api.SEAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemSEMgrTool extends Item{
	public final static String name = "semanagementtool";
    public ItemSEMgrTool() {
        setUnlocalizedName(name);
        setRegistryName(name);
        setHasSubtypes(true);
        setMaxDamage(0);
    	setCreativeTab(SEAPI.SETab);
        maxStackSize = 1;
        GameRegistry.register(this);
    }
    
    @Override
    public String getUnlocalizedNameInefficiently(ItemStack itemStack) {
        return super.getUnlocalizedNameInefficiently(itemStack).replaceAll("item.", "item.sime:");
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        return EnumActionResult.PASS;
    }
}
