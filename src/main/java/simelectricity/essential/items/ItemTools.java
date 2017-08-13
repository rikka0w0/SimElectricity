package simelectricity.essential.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import simelectricity.api.ISECrowbarTarget;
import simelectricity.api.ISEWrenchable;
import simelectricity.api.ISidedFacing;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.ISENodeDelegateBlock;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.SEItem;
import simelectricity.essential.utils.SEUnitHelper;
import simelectricity.essential.utils.Utils;

public class ItemTools extends SEItem implements ISESimpleTextureItem{
	private final static String[] subNames = new String[]{"crowbar", "wrench", "glove", "multimeter"};
	
	public ItemTools() {
		super("essential_tools", true);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
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
	public String getIconName(int damage) {
		return "tool_" + subNames[damage];
	}
	
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
    	
        if (itemStack.getItem() != this){
        	itemStack = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        	if (itemStack.getItem() != this)
        		return EnumActionResult.FAIL;
        }
      
    	TileEntity te = world.getTileEntity(pos);
    	
    	switch (itemStack.getItemDamage()){
    	case 0:
    		return useCrowbar(te, player, side);
    	case 1:
    		return useWrench(te, player, side);
    	case 2:
    		return useGlove(te, player, side);
    	case 3:
    		return useMultimeter(world, pos, player, side);
    	}
    	
    	return EnumActionResult.PASS;
    }
    
    public static EnumActionResult useCrowbar(TileEntity te, EntityPlayer player, EnumFacing side){
		if (te instanceof ISECoverPanelHost) {
			ISECoverPanel coverPanel = ((ISECoverPanelHost) te).getSelectedCoverPanel(player);
			
			if (coverPanel == null)
				return EnumActionResult.FAIL;
			
			if (te.getWorld().isRemote) {
				return EnumActionResult.PASS;
			} else {
				boolean ret = ((ISECoverPanelHost) te).removeCoverPanel(coverPanel, !player.capabilities.isCreativeMode);
				return ret? EnumActionResult.PASS: EnumActionResult.FAIL;
			}
		}
		
		else if (te instanceof ISECrowbarTarget){
    		ISECrowbarTarget crowbarTarget = (ISECrowbarTarget) te;
    		
    		EnumFacing selectedDirection = side;
    		
    		if (crowbarTarget.canCrowbarBeUsed(selectedDirection)){
    			if (te.getWorld().isRemote) {
    				return EnumActionResult.PASS;
    			} else {
    				crowbarTarget.onCrowbarAction(selectedDirection, player.capabilities.isCreativeMode);        			
        			return EnumActionResult.PASS;
    			}
    				
    		}
    		
    		return EnumActionResult.FAIL;
    	}
		
		return EnumActionResult.FAIL;
    }
    
    public static EnumActionResult useWrench(TileEntity te, EntityPlayer player, EnumFacing side){
    	if (te instanceof ISEWrenchable){
    		ISEWrenchable wrenchTarget = (ISEWrenchable) te;
    		
    		if (wrenchTarget.canWrenchBeUsed(side)){
    			if (te.getWorld().isRemote) {
    				return EnumActionResult.PASS;
    			} else {
    				wrenchTarget.onWrenchAction(side, player.capabilities.isCreativeMode);
    				return EnumActionResult.PASS;
    			}
    		}
    		return EnumActionResult.FAIL;
    	}
		
		return EnumActionResult.FAIL;
    }
    
    public static EnumActionResult useGlove(TileEntity te, EntityPlayer player, EnumFacing side){
    	if (te instanceof ISidedFacing){
    		ISidedFacing target = (ISidedFacing) te;
    		
    		if (target.canSetFacing(side)) {
    			if (te.getWorld().isRemote) {
    				return EnumActionResult.PASS;
    			} else {
    				target.setFacing(side);
    				return EnumActionResult.PASS;
    			}
    		}
    		return EnumActionResult.FAIL;
    	}
		
		return EnumActionResult.FAIL;
    }
    
    public static EnumActionResult useMultimeter(World world, BlockPos pos, EntityPlayer player, EnumFacing side){
    	TileEntity te = world.getTileEntity(pos);
    	
    	if (te instanceof ISECableTile) {
            if (te.getWorld().isRemote)
            	return EnumActionResult.PASS;
            
            Utils.chat(player, "------------------");
    		ISESimulatable node = ((ISECableTile) te).getNode();
    		printVI(node, player);
        		
    		return EnumActionResult.PASS;
    	}else if (te instanceof ISETile){
            if (te.getWorld().isRemote)
            	return EnumActionResult.PASS;
    		
            Utils.chat(player, "------------------");   
            
    		ISETile tile = (ISETile)te;
    		
        	for (EnumFacing dir : EnumFacing.VALUES){
        		ISESubComponent comp = tile.getComponent(dir);
        		if (comp != null){
	        		String[] temp = comp.toString().split("[.]");
	        		Utils.chat(player, temp[temp.length-1].split("@")[0] + ": " + 
	        				SEUnitHelper.getVoltageStringWithUnit(SEAPI.energyNetAgent.getVoltage(comp)));
        		}
        	}
        	
        	return EnumActionResult.PASS;
    	}else if (te instanceof ISEGridTile){
            if (te.getWorld().isRemote)
            	return EnumActionResult.PASS;
    		
            Utils.chat(player, "------------------");  
    		ISEGridNode node = ((ISEGridTile) te).getGridNode();
    		printVI(node, player);
    		
    		return EnumActionResult.PASS;
    	}else{
    		Block block = world.getBlockState(pos).getBlock();
    		if (block instanceof ISENodeDelegateBlock){
    			if (world.isRemote)
    				return EnumActionResult.PASS;
    			
    			Utils.chat(player, "------------------"); 
    			ISESimulatable node = ((ISENodeDelegateBlock) block).getNode(world, pos);
    			printVI(node, player);
    			
    			return EnumActionResult.PASS;
    		}
    	}
    	
    	return EnumActionResult.PASS;
    }
    
    public static void printVI(ISESimulatable node, EntityPlayer player){
		Utils.chat(player, "V=" + SEUnitHelper.getVoltageStringWithUnit(
				SEAPI.energyNetAgent.getVoltage(node)));
		
    	double currentMagnitude = SEAPI.energyNetAgent.getCurrentMagnitude(node);
    	if (!Double.isNaN(currentMagnitude))
    		Utils.chat(player, "I=" + SEUnitHelper.getCurrentStringWithUnit(currentMagnitude));
    }
}
