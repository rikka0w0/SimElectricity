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
        
    	if (!lastCoordinates.containsKey(player))
            lastCoordinates.put(player, new int[] { 0, -1, 0 });

        int[] lastCoordinate = lastCoordinates.get(player);

        if (lastCoordinate[1] == -1) {	//First selection
            lastCoordinate[0] = x;
            lastCoordinate[1] = y;
            lastCoordinate[2] = z;

            Utils.chat(player, "chat.sime_essential:tranmission_tower_selected");
        }else{
        	if (lastCoordinate[0] == x && lastCoordinate[1] == y && lastCoordinate[2] == z){
        		Utils.chat(player, StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_recursive_connection"));
        	}else{
        		Block neighbor = world.getBlock(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]);
        		if (neighbor instanceof ISEHVCableConnector){
        			double distance = SEMathHelper.distanceOf(x, z, lastCoordinate[0], lastCoordinate[2]);
                    if (distance < 5) {
                    	Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_too_close") + EnumChatFormatting.RESET);
                    }else if (distance > 100){
                    	Utils.chat(player, EnumChatFormatting.RED + StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_too_far") + EnumChatFormatting.RESET);
                    }else{
                    	double resistance = distance * resistivityList[itemStack.getItemDamage()];	//Calculate the resistance
                    	ISEGridNode node1 = ((ISEHVCableConnector) block).getGridNode(world, x, y, z);
                    	ISEGridNode node2 = ((ISEHVCableConnector) neighbor).getGridNode(world, lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]);

                    	if (node1 != null && node2 != null &&
                    		SEAPI.energyNetAgent.isNodeValid(world, node1) &&
                    		SEAPI.energyNetAgent.isNodeValid(world, node2)){
                    		
                    		SEAPI.energyNetAgent.connectGridNode(world, node1, node2, resistance);
                    		Utils.chat(player, StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_connected"));
                    	}else{
                    		Utils.chat(player, StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_last_selection_invalid"));
						}

                        lastCoordinate[0] = 0;
                        lastCoordinate[1] = -1;
                        lastCoordinate[2] = 0;
                        lastCoordinates.put(player, lastCoordinate);
                        
                        
                    }
        		}else{
                    lastCoordinate[0] = 0;
                    lastCoordinate[1] = -1;
                    lastCoordinate[2] = 0;
                    lastCoordinates.put(player, lastCoordinate);
                    
                    Utils.chat(player, StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_last_selection_invalid"));
        		}
        	}
        }
    	
        return true;        	
    }
}
