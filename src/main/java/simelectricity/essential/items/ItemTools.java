package simelectricity.essential.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.api.ISECrowbarTarget;
import simelectricity.api.ISEWrenchable;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.SEItem;

public class ItemTools extends SEItem {
	private final static String[] subNames = new String[]{"crowbar", "wrench", "glove"};
	private final IIcon[] iconCache;
	
	public ItemTools() {
		super("essential_tools", true);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		
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
			iconCache[i] = r.registerIcon("sime_essential:tool_"+subNames[i]);
    }
	
    /**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int dmg)
    {
        return iconCache[dmg];
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    	ForgeDirection direction = ForgeDirection.getOrientation(side);
    	TileEntity te = world.getTileEntity(x, y, z);
    	
    	switch (itemStack.getItemDamage()){
    	case 0:
    		return useCrowbar(te, player, direction);
    	case 1:
    		return useWrench(te, player, direction);
    	case 2:
    		return useGlove(te, player, direction);
    	}
    	
    	return false;
    }
    
    public static boolean useCrowbar(TileEntity te, EntityPlayer player, ForgeDirection side){
    	if (te instanceof ISECrowbarTarget){
    		ISECrowbarTarget crowbarTarget = (ISECrowbarTarget) te;
    		
    		ForgeDirection selectedDirection = crowbarTarget.getSelectedSide(player, side);
    		if (crowbarTarget.canCrowbarBeUsed(selectedDirection)){
    			if (!te.getWorldObj().isRemote)
    				crowbarTarget.onCrowbarAction(selectedDirection, player.capabilities.isCreativeMode);
    			
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public static boolean useWrench(TileEntity te, EntityPlayer player, ForgeDirection side){
    	if (te instanceof ISEWrenchable){
    		ISEWrenchable wrenchTarget = (ISEWrenchable) te;
    		
    		if (wrenchTarget.canWrenchBeUsed(side)){
    			if (!te.getWorldObj().isRemote)
    				wrenchTarget.onWrenchAction(side, player.capabilities.isCreativeMode);
    			
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private static boolean useGlove(TileEntity te, EntityPlayer player, ForgeDirection side){
    	if (te instanceof ISidedFacing){
    		ISidedFacing target = (ISidedFacing) te;
    		
    		if (target.canSetFacing(side)) {
    			if (!te.getWorldObj().isRemote)
    				target.setFacing(side);
    			
    			return true;
    		}
    	}
    	return false;
    }
}
