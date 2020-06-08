package simelectricity.essential.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rikka.librikka.IMetaProvider;
import rikka.librikka.IMetaBase;
import rikka.librikka.Utils;
import rikka.librikka.block.BlockUtils;
import rikka.librikka.item.ItemBase;
import simelectricity.api.ISECrowbarTarget;
import simelectricity.api.ISESidedFacing;
import simelectricity.api.ISEWrenchable;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISECable;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.ISENodeDelegateBlock;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.utils.SEUnitHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ItemTools extends ItemBase implements IMetaProvider<IMetaBase> {
	private final ItemType itemType;
    private static Map<PlayerEntity, BlockPos> lastCoordinates_cablecutter_hv = new HashMap<>();

    public static enum ItemType implements IMetaBase{
    	crowbar(ItemTools::useCrowbar),
    	wrench(ItemTools::useWrench),
    	glove(ItemTools::useGlove),
    	multimeter(ItemTools::useMultimeter),
    	cablecutter_hv(ItemTools::useHVCableCutter);
    	
    	private final Function<ItemUseContext, ActionResultType> handler;
    	ItemType(Function<ItemUseContext, ActionResultType> handler) {
    		this.handler = handler;
    	}
    }
    
    private ItemTools(ItemType itemType) {
        super("tool_" + itemType.name(), (new Item.Properties())
        		.maxStackSize(1)
        		.group(SEAPI.SETab));
        this.itemType = itemType;
    }

    @Override
	public IMetaBase meta() {
		return itemType;
	}
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
    	return itemType.handler.apply(context);
    }
    
    public static ItemTools[] create() {
    	ItemTools[] ret = new ItemTools[ItemType.values().length];
    	for (ItemType itemType: ItemType.values())
    		ret[itemType.ordinal()] = new ItemTools(itemType);
    	return ret;
    }
    
    public static ActionResultType useCrowbar(ItemUseContext context) {
    	TileEntity te = context.getWorld().getTileEntity(context.getPos());
    	PlayerEntity player = context.getPlayer();
    	Direction side = context.getFace();
    	
    	
        if (te instanceof ISECoverPanelHost) {
            Direction coverPanelSide = ((ISECoverPanelHost) te).getSelectedCoverPanel(player);

            if (coverPanelSide == null)
                return ActionResultType.FAIL;

            ISECoverPanelHost host = (ISECoverPanelHost) te;
            if (host.removeCoverPanel(coverPanelSide, true)) {
            	ISECoverPanel coverPanel = ((ISECoverPanelHost) te).getCoverPanelOnSide(side);
            	
            	if (!te.getWorld().isRemote())
                	host.removeCoverPanel(coverPanelSide, false);
            	
                if (!player.isCreative()) {
                	Utils.dropItemIntoWorld(te.getWorld(), te.getPos(), coverPanel.getDroppedItemStack());
                }
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.FAIL;
        } else if (te instanceof ISECrowbarTarget) {
            ISECrowbarTarget crowbarTarget = (ISECrowbarTarget) te;

            Direction selectedDirection = side;

            if (crowbarTarget.canCrowbarBeUsed(selectedDirection)) {
                if (te.getWorld().isRemote) {
                    return ActionResultType.PASS;
                } else {
                    crowbarTarget.onCrowbarAction(selectedDirection, player.isCreative());
                    return ActionResultType.PASS;
                }

            }

            return ActionResultType.FAIL;
        }

        return ActionResultType.FAIL;
    }

    public static ActionResultType useWrench(ItemUseContext context) {
    	TileEntity te = context.getWorld().getTileEntity(context.getPos());
    	PlayerEntity player = context.getPlayer();
    	Direction side = context.getFace();
    	
        if (te instanceof ISEWrenchable) {
            ISEWrenchable wrenchTarget = (ISEWrenchable) te;

            if (wrenchTarget.canWrenchBeUsed(side)) {
                if (te.getWorld().isRemote) {
                    return ActionResultType.PASS;
                } else {
                    wrenchTarget.onWrenchAction(side, player.isCreative());
                    return ActionResultType.PASS;
                }
            }
            return ActionResultType.FAIL;
        }

        return ActionResultType.FAIL;
    }

    public static ActionResultType useGlove(ItemUseContext context) {
    	TileEntity te = context.getWorld().getTileEntity(context.getPos());
    	Direction side = context.getFace();
    	
        if (te instanceof ISESidedFacing) {
            ISESidedFacing target = (ISESidedFacing) te;

            if (target.canSetFacing(side)) {
                if (te.getWorld().isRemote) {
                    return ActionResultType.PASS;
                } else {
                    target.setFacing(side);
                    return ActionResultType.PASS;
                }
            }
            return ActionResultType.FAIL;
        }

        return ActionResultType.FAIL;
    }

    public static ActionResultType useMultimeter(ItemUseContext context) {
    	World world = context.getWorld();
    	BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        
        if (world.isRemote)
            return ActionResultType.PASS;
        
        TileEntity te = world.getTileEntity(pos);
        Block block = world.getBlockState(pos).getBlock();
        
        ISESimulatable delegatedNode = null;
        if (block instanceof ISENodeDelegateBlock) {
        	delegatedNode = ((ISENodeDelegateBlock<?>) block).getNode(world, pos);
        }
        
        if (!(	te instanceof ISECableTile ||
        		te instanceof ISETile ||
        		te instanceof ISETile) && delegatedNode == null)
            return ActionResultType.PASS;
        
        Utils.chat(player, "------------------");
        player.sendMessage(BlockUtils.getDisplayName(world, pos));
        
        if (block instanceof ISENodeDelegateBlock) {
        	delegatedNode = ((ISENodeDelegateBlock<?>) block).getNode(world, pos);
            if (delegatedNode != null) {
            	ItemTools.printVI(delegatedNode, player);
            }
        }
                
        if (te instanceof ISECableTile) {
            ISESimulatable node = ((ISECableTile) te).getNode();
            if (node != delegatedNode)
            	ItemTools.printVI(node, player);
        }
        
        if (te instanceof ISETile) {
            ISETile tile = (ISETile) te;

            for (Direction dir : Direction.values()) {
                ISESubComponent<?> comp = tile.getComponent(dir);
                if (comp != null && comp != delegatedNode) {
                    double voltage = comp.getVoltage();
                    String[] temp = comp.toString().split("[.]");
                    String msg = temp[temp.length - 1].split("@")[0] + ": " +
                            SEUnitHelper.getVoltageStringWithUnit(voltage);
                    if (comp instanceof ISEVoltageSource) {
                    	double r = ((ISEVoltageSource) comp).getResistance();
                    	double vint = ((ISEVoltageSource) comp).getOutputVoltage();
                    	double power = voltage*(voltage-vint)/r;
                    	msg += ", " + SEUnitHelper.getPowerStringWithUnit(power);
                    }
                    Utils.chat(player, msg);
                }
            }

        } 
        
        if (te instanceof ISEGridTile) {
            ISEGridNode node = ((ISEGridTile) te).getGridNode();
            if (node != delegatedNode)
            	ItemTools.printVI(node, player);
        }
        
        return ActionResultType.PASS;
    }

    public static void printVI(ISESimulatable node, PlayerEntity player) {
    	String s = "WTF";
    	
    	if (node instanceof ISECable) {
    		s = "Cable";
    	} else if (node instanceof ISEGridNode) {
    		int type = ((ISEGridNode) node).getType();
    		if (type == 0)
    			s = "Pole";
    		else if (type == 1)
    			s = "TransformerPrimary";
    		else if (type == 2)
    			s = "TransformerSecondary";
    	}
    	
    	s += ", " + SEUnitHelper.getVoltageStringWithUnit(node.getVoltage());
    	
        double currentMagnitude = node.getCurrentMagnitude();
        if (!Double.isNaN(currentMagnitude))
        	s += ", I=" + SEUnitHelper.getCurrentStringWithUnit(currentMagnitude);
        
        Utils.chat(player, s);
    }

    private static ActionResultType useHVCableCutter(ItemUseContext context) {
    	World world = context.getWorld();
    	BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        
        if (world.isRemote)
            return ActionResultType.PASS;

        TileEntity te = world.getTileEntity(pos);
        Block block = world.getBlockState(pos).getBlock();

        ISEGridNode gridNode = null;
        ISESimulatable delegatedNode = null;
        if (block instanceof ISENodeDelegateBlock)
            delegatedNode = ((ISENodeDelegateBlock<?>) block).getNode(world, pos);

        if (delegatedNode instanceof ISEGridNode) {
            gridNode = (ISEGridNode) delegatedNode;
        } else if (te instanceof ISEGridTile) {
            gridNode = ((ISEGridTile)te).getGridNode();
        }

        if (gridNode == null) {
            Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_current_selection_invalid");
            return ActionResultType.FAIL;
        }

        if (player.isCrouching()) {
            for (ISEGridNode neighbor : gridNode.getNeighborList()) {
                SEAPI.energyNetAgent.breakGridConnection(world, neighbor, gridNode);
            }
            lastCoordinates_cablecutter_hv.put(player, null);
            return ActionResultType.SUCCESS;
        }

        BlockPos lastSelectedPos = lastCoordinates_cablecutter_hv.get(player);
        if (lastSelectedPos == null) {
            Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_selected");
            lastCoordinates_cablecutter_hv.put(player, pos);
        } else {
            lastCoordinates_cablecutter_hv.put(player, null);
            TileEntity lastTE = world.getTileEntity(lastSelectedPos);
            Block lastBlock = world.getBlockState(lastSelectedPos).getBlock();
            if (lastBlock instanceof ISENodeDelegateBlock)
                delegatedNode = ((ISENodeDelegateBlock<?>) lastBlock).getNode(world, lastSelectedPos);

            ISEGridNode lastNode = null;
            if (delegatedNode instanceof ISEGridNode) {
                lastNode = (ISEGridNode) delegatedNode;
            } else if (lastTE instanceof ISEGridTile) {
                lastNode = ((ISEGridTile)lastTE).getGridNode();
            }

            if (lastNode == null) {
                Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_last_selection_invalid");
            } else if (lastNode == gridNode) {
                Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_selected");
                lastCoordinates_cablecutter_hv.put(player, pos);
            } else {
                if (gridNode.hasResistiveConnection(lastNode)) {
                    SEAPI.energyNetAgent.breakGridConnection(world, lastNode, gridNode);
                } else {
                    Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_not_adjacent");
                }
            }
        }

        return ActionResultType.SUCCESS;
    }
}
