package simelectricity.essential.items;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.essential.common.SEItem;
import simelectricity.essential.grid.ISEHVCableConnector;
import simelectricity.essential.utils.SEMathHelper;
import simelectricity.essential.utils.Utils;

public class ItemHighVoltageCable extends SEItem{
	private final Map<EntityPlayer, int[]> lastCoordinates;
	
	private final static double[] resistivityList = new double[]{0.1, 0.2};
	
	public ItemHighVoltageCable() {
		super("essential_hv_cable", true);
		this.lastCoordinates = new HashMap<EntityPlayer, int[]>();
	}

	@Override
	public void beforeRegister() {
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public String[] getSubItemUnlocalizedNames(){
		return new String[]{"copper", "aluminum"};
	}
	
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
        	return true;
    	
        Block block = world.getBlock(x, y, z);
        if (!(block instanceof ISEHVCableConnector))
        	return true;
        
        ISEHVCableConnector connector1 = (ISEHVCableConnector) block;
        
    	if (!lastCoordinates.containsKey(player))
            lastCoordinates.put(player, new int[] { 0, -1, 0 });

        int[] lastCoordinate = lastCoordinates.get(player);

        if (lastCoordinate[1] == -1) {	//First selection
        	if (connector1.canHVCableConnect(world, x, y, z)){
                lastCoordinate[0] = x;
                lastCoordinate[1] = y;
                lastCoordinate[2] = z;
                Utils.chat(player, "chat.sime_essential:tranmission_tower_selected");
        	}else{
        		Utils.chat(player, EnumChatFormatting.RED + "chat.sime_essential:tranmission_tower_too_many_connection");
        	}
        }else{
        	Block neighbor = world.getBlock(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]);
        	
        	if (neighbor instanceof ISEHVCableConnector){
        		ISEHVCableConnector connector2 = (ISEHVCableConnector) neighbor;
            	ISEGridNode node1 = connector1.getGridNode(world, x, y, z);
            	ISEGridNode node2 = connector2.getGridNode(world, lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]);
        		
            	if (node1 == node2){
            		Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_recursive_connection"));
            	}else if (!connector1.canHVCableConnect(world, x, y, z)){
            		Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_current_selection_invalid"));
            	}else if (!connector2.canHVCableConnect(world, lastCoordinate[0], lastCoordinate[1], lastCoordinate[2])){
            		Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_last_selection_invalid"));
            	}else{
            		double distance = SEMathHelper.distanceOf(node1.getXCoord(), node1.getZCoord(), node2.getXCoord(), node2.getZCoord());
                    if (distance < 5) {
                    	Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_too_close") + EnumChatFormatting.RESET);
                    }else if (distance > 100){
                    	Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_too_far") + EnumChatFormatting.RESET);
                    }else{
                    	double resistance = distance * resistivityList[itemStack.getItemDamage()];	//Calculate the resistance
                    	if (node1 != null && node2 != null &&
                        	SEAPI.energyNetAgent.isNodeValid(world, node1) &&
                        	SEAPI.energyNetAgent.isNodeValid(world, node2)){
                        		
                        		SEAPI.energyNetAgent.connectGridNode(world, node1, node2, resistance);
                        		Utils.chat(player, StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_connected"));
                        	}
                    }
            	}
        	}else{
        		Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_current_selection_invalid"));
        	}
        	
            lastCoordinate[0] = 0;
            lastCoordinate[1] = -1;
            lastCoordinate[2] = 0;
            lastCoordinates.put(player, lastCoordinate);
        }
    	
        return true;        	
    }
}
