package simelectricity.essential.items;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import simelectricity.api.SEAPI;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.common.SEItem;
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

        if (lastCoordinate[1] == -1) {        	
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
                    	double resistance = distance * resistivityList[itemStack.getItemDamage()];
                    	int[] coord1 = ((ISEHVCableConnector) block).getGridNodeCoord(world, x, y, z);
                    	int[] coord2 = ((ISEHVCableConnector) neighbor).getGridNodeCoord(world, lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]);
                    	TileEntity te = world.getTileEntity(coord1[0], coord1[1], coord1[2]);
                    	te = world.getTileEntity(coord2[0], coord2[1], coord2[2]);
                    	SEAPI.energyNetAgent.connectGridNode(world, coord1[0], coord1[1], coord1[2], coord2[0], coord2[1], coord2[2], resistance);

                        lastCoordinate[0] = 0;
                        lastCoordinate[1] = -1;
                        lastCoordinate[2] = 0;
                        lastCoordinates.put(player, lastCoordinate);
                        
                        Utils.chat(player, StatCollector.translateToLocal("chat.sime_essential:tranmission_tower_connected"));
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
