package simelectricity.essential.common;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import simelectricity.essential.Essential;

public abstract class SEItem extends Item{	
	/**
	 * 
	 * @param name Naming rules: lower case English letters and numbers only, words are separated by '_', e.g. "cooked_beef"
	 * @param hasSubItems
	 */
    public SEItem(String name, boolean hasSubItems) {
		this.setUnlocalizedName(name);	//UnlocalizedName = "item." + name
		//this.setRegistryName(name);
		this.setHasSubtypes(hasSubItems);
		
		if (hasSubItems)
			this.setMaxDamage(0);	//The item can not be damaged
		
		this.beforeRegister();
		
		GameRegistry.registerItem(this, name);
		
		//GameRegistry.register(this);
		//new SEItemRenderRegistery(this);
    }
    
    @Override
    public final String getUnlocalizedName(ItemStack itemstack) {
    	if (this.getHasSubtypes()){
            return super.getUnlocalizedName() + "." + getSubItemUnlocalizedNames()[itemstack.getItemDamage()];
    	}
    	else{
    		return super.getUnlocalizedName();
    	}
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    //public final void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    public final void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
    {
    	if (this.getHasSubtypes()){
            for (int ix = 0; ix < getSubItemUnlocalizedNames().length; ix++) 
                subItems.add(new ItemStack(this, 1, ix));
    	}else{
    		subItems.add(new ItemStack(itemIn));
    	}
    }
    
	@Override
	public final String getUnlocalizedNameInefficiently(ItemStack stack){
		String prevName = super.getUnlocalizedNameInefficiently(stack);
		return "item." + Essential.modID + ":" + prevName.substring(5);
	}
	
    public abstract void beforeRegister();
    
    /**
     * Only use for subItems
     * 
     * @return an array of unlocalized names
     */
    public String[] getSubItemUnlocalizedNames(){
    	return null;
    }
}
