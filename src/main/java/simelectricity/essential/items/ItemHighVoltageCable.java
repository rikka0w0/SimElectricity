package simelectricity.essential.items;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.SEItem;
import simelectricity.essential.utils.Utils;

public class ItemHighVoltageCable extends SEItem implements ISESimpleTextureItem{
	private final static String[] subNames = new String[]{"copper", "aluminum"};
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
		return subNames;
	}
	
	@Override
	public String getIconName(int damage) {
		return "hvcable_" + subNames[damage];
	}
	
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
    	
        if (itemStack.getItem() != this){
        	itemStack = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
        	if (itemStack.getItem() != this)
        		return EnumActionResult.FAIL;
        }
        
    	if (world.isRemote)
        	return EnumActionResult.SUCCESS;
    	
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        Block block = world.getBlockState(pos).getBlock();
        
        if (!(block instanceof ISEHVCableConnector))
        	return EnumActionResult.SUCCESS;
        
        ISEHVCableConnector connector1 = (ISEHVCableConnector) block;
        
    	if (!lastCoordinates.containsKey(player))
            lastCoordinates.put(player, new int[] { 0, -1, 0 });

        int[] lastCoordinate = lastCoordinates.get(player);

        if (lastCoordinate[1] == -1) {	//First selection
        	if (connector1.canHVCableConnect(world, x, y, z)){
                lastCoordinate[0] = x;
                lastCoordinate[1] = y;
                lastCoordinate[2] = z;
                Utils.chatWithLocalization(player, "chat.sime_essential:tranmission_tower_selected");
        	}else{
        		Utils.chatWithLocalization(player, "chat.sime_essential:tranmission_tower_too_many_connection");
        	}
        }else{
        	Block neighbor = world.getBlockState(new BlockPos(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2])).getBlock();
        	
        	if (neighbor instanceof ISEHVCableConnector){
        		ISEHVCableConnector connector2 = (ISEHVCableConnector) neighbor;
            	ISEGridNode node1 = (ISEGridNode) connector1.getNode(world, pos);
            	ISEGridNode node2 = (ISEGridNode) connector2.getNode(world, new BlockPos(lastCoordinate[0], lastCoordinate[1], lastCoordinate[2]));
        		
            	if (node1 == node2){
            		Utils.chatWithLocalization(player, I18n.translateToLocal("chat.sime_essential:tranmission_tower_recursive_connection"));
            	}else if (!connector1.canHVCableConnect(world, x, y, z)){
            		Utils.chatWithLocalization(player, I18n.translateToLocal("chat.sime_essential:tranmission_tower_current_selection_invalid"));
            	}else if (!connector2.canHVCableConnect(world, lastCoordinate[0], lastCoordinate[1], lastCoordinate[2])){
            		Utils.chatWithLocalization(player, I18n.translateToLocal("chat.sime_essential:tranmission_tower_last_selection_invalid"));
            	}else{
            		double distance = node1.getPos().distanceSq(node2.getPos());
                    if (distance < 5) {
                    	Utils.chatWithLocalization(player, I18n.translateToLocal("chat.sime_essential:tranmission_tower_too_close"));
                    }else if (distance > 200){
                    	Utils.chatWithLocalization(player, I18n.translateToLocal("chat.sime_essential:tranmission_tower_too_far"));
                    }else{
                    	double resistance = distance * resistivityList[itemStack.getItemDamage()];	//Calculate the resistance
                    	if (node1 != null && node2 != null &&
                        	SEAPI.energyNetAgent.isNodeValid(world, node1) &&
                        	SEAPI.energyNetAgent.isNodeValid(world, node2)){
                        		
                        		SEAPI.energyNetAgent.connectGridNode(world, node1, node2, resistance);
                        		Utils.chatWithLocalization(player, I18n.translateToLocal("chat.sime_essential:tranmission_tower_connected"));
                        	}
                    }
            	}
        	}else{
        		Utils.chatWithLocalization(player, I18n.translateToLocal("chat.sime_essential:tranmission_tower_current_selection_invalid"));
        	}
        	
            lastCoordinate[0] = 0;
            lastCoordinate[1] = -1;
            lastCoordinate[2] = 0;
            lastCoordinates.put(player, lastCoordinate);
        }
    	
        return EnumActionResult.SUCCESS;        	
    }
}
