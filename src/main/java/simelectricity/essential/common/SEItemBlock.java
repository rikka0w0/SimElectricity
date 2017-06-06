package simelectricity.essential.common;

import simelectricity.essential.Essential;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SEItemBlock extends ItemBlock{
    public SEItemBlock(Block block)
    {
        super(block);
        
        boolean hasSubBlocks = block instanceof ISESubBlock;
        
        if (!(block instanceof SEBlock))
        	throw new RuntimeException("SEItemBlock should be used with SEblock!");
        
        setHasSubtypes(hasSubBlocks);
        
		if (hasSubBlocks)
			this.setMaxDamage(0);	//The item can not be damaged
    }
	
    @Override
    public final String getUnlocalizedName(ItemStack itemstack) {
    	if (this.getHasSubtypes()){
        	SEBlock seBlock = (SEBlock)this.field_150939_a;
        	String[] subBlockUnlocalizedNames = ((ISESubBlock)seBlock).getSubBlockUnlocalizedNames();
            return super.getUnlocalizedName() + "." + subBlockUnlocalizedNames[itemstack.getItemDamage()];
    	}
    	else{
    		return super.getUnlocalizedName();
    	}
    }
    
    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    @Override
    public final int getMetadata(int damage)
    {
    	if (this.getHasSubtypes()){
    		return damage;
    	}else{
    		return 0;
    	}
    }
    
	@Override
	public final String getUnlocalizedNameInefficiently(ItemStack stack){
		String prevName = super.getUnlocalizedNameInefficiently(stack);
		return "tile." + Essential.modID + ":" + prevName.substring(5);
	}
}
