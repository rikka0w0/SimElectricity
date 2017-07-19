package simelectricity.essential.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.SEItem;

public class ItemMisc extends SEItem{
	private final static String[] subNames = new String[]{"ledpanel", "voltagesensor"};
	private final IIcon[] iconCache;
	
	public ItemMisc() {
		super("essential_item", true);
		
		this.iconCache = new IIcon[this.getUnlocalizedName().length()];
	}

	@Override
	public void beforeRegister() {
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public String[] getSubItemUnlocalizedNames(){
		return subNames;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister r)	{
		for (int i=0; i<subNames.length; i++)
			iconCache[i] = r.registerIcon("sime_essential:item_"+subNames[i]);
    }
	
    /**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int dmg)
    {
        return iconCache[dmg];
    }
}
