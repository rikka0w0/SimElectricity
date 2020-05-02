package simelectricity.essential.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import rikka.librikka.IMetaProvider;
import rikka.librikka.IMetaBase;
import rikka.librikka.Utils;
import rikka.librikka.item.ItemBase;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEHVCableConnector;
import simelectricity.essential.api.ISEPoleAccessory;

import java.util.HashMap;
import java.util.Map;

public final class ItemHighVoltageCable extends ItemBase implements IMetaProvider<IMetaBase> {
    private final Map<PlayerEntity, BlockPos> lastCoordinates;

    public static enum ItemType implements IMetaBase {
    	copper(0.1),
    	aluminum(0.2);
    	
    	public final double resistivity;
    	ItemType(double resistivity) {
    		this.resistivity = resistivity;
    	}
    }
    
    public final ItemType itemType;
    private ItemHighVoltageCable(ItemType itemType) {
        super("hvcable_" + itemType.name(), (new Item.Properties())
        		.group(SEAPI.SETab));
        lastCoordinates = new HashMap<>();
        this.itemType = itemType;
    }
    
    @Override
	public IMetaBase meta() {
		return itemType;
	}
    
    public static ItemHighVoltageCable[] create() {
    	ItemHighVoltageCable[] ret = new ItemHighVoltageCable[ItemType.values().length];
    	for (ItemType info: ItemType.values())
    		ret[info.ordinal()] = new ItemHighVoltageCable(info);
    	return ret;
    }

    private static boolean numberOfConductorMatched(ISEGridNode node1, ISEGridNode node2) {
        if (node1.numOfParallelConductor() == 0 || node2.numOfParallelConductor() == 0)
            return true;
        return node1.numOfParallelConductor() == node2.numOfParallelConductor();
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
    	World world = context.getWorld();
    	BlockPos pos = context.getPos();
    	ItemStack itemStack = context.getItem();
        
        if (world.isRemote)
            return ActionResultType.SUCCESS;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        Block block = world.getBlockState(pos).getBlock();

        if (!(block instanceof ISEHVCableConnector))
            return ActionResultType.SUCCESS;

        ISEHVCableConnector connector1 = (ISEHVCableConnector) block;

        if (!this.lastCoordinates.containsKey(player))
            this.lastCoordinates.put(player, null);

        BlockPos lastCoordinate = this.lastCoordinates.get(player);

        if (lastCoordinate == null) {    //First selection
            if (canConnect(connector1, world, pos, null)) {
                lastCoordinate = new BlockPos(pos);
                Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_selected");
            } else {
            	Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_current_selection_invalid");
            }

            this.lastCoordinates.put(player, lastCoordinate);
        } else {
            Block neighbor = world.getBlockState(lastCoordinate).getBlock();

            if (neighbor instanceof ISEHVCableConnector) {
                ISEHVCableConnector connector2 = (ISEHVCableConnector) neighbor;
                ISEGridTile tile1 = connector1.getGridTile(world, pos);
                ISEGridTile tile2 = connector2.getGridTile(world, lastCoordinate);
                ISEGridNode node1 = tile1==null ? null : tile1.getGridNode();
                ISEGridNode node2 = tile2==null ? null : tile2.getGridNode();

                if (node1 == node2) {
                    Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_recursive_connection");
                } else if (!canConnect(connector1, world, pos, null)) {
                    Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_current_selection_invalid");
                } else if (!canConnect(connector2, world, lastCoordinate, null)) {
                    Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_last_selection_invalid");
                } else if (!ItemHighVoltageCable.numberOfConductorMatched(node1, node2)) {
                    Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_type_mismatch");
                } else if (!canConnect(connector1, world, pos, lastCoordinate) || !canConnect(connector2, world, lastCoordinate, pos)) {
                	Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_connection_denied");
                } else {
                	boolean flag1 = tile1 instanceof ISEPoleAccessory;
                	boolean flag2 = tile2 instanceof ISEPoleAccessory;
                	//if (flag1 && flag2) {
                		//Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_connection_denied");
                	//} else {
            			double distance = MathHelper.sqrt(node1.getPos().distanceSq(node2.getPos()));
        				double resistance = distance * this.itemType.resistivity;    //Calculate the resistance
                		boolean isCreative = player.isCreative();
                		if (!flag1 && !flag2){
                			if (distance < 5) {
                				Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_too_close");
                			} else if (distance > 200) {
                				Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_too_far");
                			} else {
                				connect(world, node1, node2, resistance, itemStack, isCreative);
                			}
                		} else {
                			connect(world, node1, node2, resistance, itemStack, isCreative);
                		}
                	//}
                }
            } else {
                Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_current_selection_invalid");
            }

            this.lastCoordinates.put(player, null);
        }

        return ActionResultType.SUCCESS;
    }
    
    private static boolean canConnect(ISEHVCableConnector connector, World world, BlockPos from, BlockPos to) {
    	ISEGridTile gridTile = connector.getGridTile(world, from);
    	if (gridTile == null)
    		return false;
    	
    	return gridTile.canConnect(to);
    }
    
    private static void connect(World world, ISEGridNode node1, ISEGridNode node2, double resistance, ItemStack itemStack, boolean isCreative) {
		if (node1 != null && node2 != null &&
				SEAPI.energyNetAgent.isNodeValid(world, node1) &&
				SEAPI.energyNetAgent.isNodeValid(world, node2)) {

			SEAPI.energyNetAgent.connectGridNode(world, node1, node2, resistance);
			
			//TODO: Consume items
		}
    }
}
