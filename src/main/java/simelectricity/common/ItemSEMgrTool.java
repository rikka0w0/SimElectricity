package simelectricity.common;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSEMgrTool extends Item{
	public final static String name = "semanagementtool";
    public ItemSEMgrTool() {
        setUnlocalizedName(name);
        setHasSubtypes(true);
        setMaxDamage(0);
    	setCreativeTab(SEAPI.SETab);
        maxStackSize = 1;
        GameRegistry.registerItem(this, name);
    }
    
    @Override
    public String getUnlocalizedNameInefficiently(ItemStack itemStack) {
        return super.getUnlocalizedNameInefficiently(itemStack).replaceAll("item.", "item.sime:");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r) {
        itemIcon = r.registerIcon("simelectricity:semanagementtool");
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    	return false;
    }
}
