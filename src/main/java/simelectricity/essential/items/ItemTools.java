package simelectricity.essential.items;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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
    private static Map<Player, BlockPos> lastCoordinates_cablecutter_hv = new HashMap<>();

    public static enum ItemType implements IMetaBase{
    	crowbar(ItemTools::useCrowbar),
    	wrench(ItemTools::useWrench),
    	glove(ItemTools::useGlove),
    	multimeter(ItemTools::useMultimeter),
    	cablecutter_hv(ItemTools::useHVCableCutter);

    	private final Function<UseOnContext, InteractionResult> handler;
    	ItemType(Function<UseOnContext, InteractionResult> handler) {
    		this.handler = handler;
    	}
    }

    private ItemTools(ItemType itemType) {
        super("tool_" + itemType.name(), (new Item.Properties())
        		.stacksTo(1)
        		.tab(SEAPI.SETab));
        this.itemType = itemType;
    }

    @Override
	public IMetaBase meta() {
		return itemType;
	}

    @Override
    public InteractionResult useOn(UseOnContext context) {
    	return itemType.handler.apply(context);
    }

    public static ItemTools[] create() {
    	ItemTools[] ret = new ItemTools[ItemType.values().length];
    	for (ItemType itemType: ItemType.values())
    		ret[itemType.ordinal()] = new ItemTools(itemType);
    	return ret;
    }

    public static InteractionResult useCrowbar(UseOnContext context) {
    	BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
    	Player player = context.getPlayer();
    	Direction side = context.getClickedFace();


        if (te instanceof ISECoverPanelHost) {
            Direction coverPanelSide = ((ISECoverPanelHost) te).getSelectedCoverPanel(player);

            if (coverPanelSide == null)
                return InteractionResult.FAIL;

            ISECoverPanelHost host = (ISECoverPanelHost) te;
            if (host.removeCoverPanel(coverPanelSide, true)) {
            	ISECoverPanel coverPanel = ((ISECoverPanelHost) te).getCoverPanelOnSide(side);

            	if (!te.getLevel().isClientSide())
                	host.removeCoverPanel(coverPanelSide, false);

                if (!player.isCreative()) {
                	Utils.dropItemIntoWorld(te.getLevel(), te.getBlockPos(), coverPanel.getDroppedItemStack());
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        } else if (te instanceof ISECrowbarTarget) {
            ISECrowbarTarget crowbarTarget = (ISECrowbarTarget) te;

            Direction selectedDirection = side;

            if (crowbarTarget.canCrowbarBeUsed(selectedDirection)) {
                if (te.getLevel().isClientSide) {
                    return InteractionResult.PASS;
                } else {
                    crowbarTarget.onCrowbarAction(selectedDirection, player.isCreative());
                    return InteractionResult.PASS;
                }

            }

            return InteractionResult.FAIL;
        }

        return InteractionResult.FAIL;
    }

    public static InteractionResult useWrench(UseOnContext context) {
    	BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
    	Player player = context.getPlayer();
    	Direction side = context.getClickedFace();

        if (te instanceof ISEWrenchable) {
            ISEWrenchable wrenchTarget = (ISEWrenchable) te;

            if (wrenchTarget.canWrenchBeUsed(side)) {
                if (te.getLevel().isClientSide) {
                    return InteractionResult.PASS;
                } else {
                    wrenchTarget.onWrenchAction(side, player.isCreative());
                    return InteractionResult.PASS;
                }
            }
            return InteractionResult.FAIL;
        }

        return InteractionResult.FAIL;
    }

    public static InteractionResult useGlove(UseOnContext context) {
    	BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
    	Direction side = context.getClickedFace();

        if (te instanceof ISESidedFacing) {
            ISESidedFacing target = (ISESidedFacing) te;

            if (target.canSetFacing(side)) {
                if (te.getLevel().isClientSide) {
                    return InteractionResult.PASS;
                } else {
                    target.setFacing(side);
                    return InteractionResult.PASS;
                }
            }
            return InteractionResult.FAIL;
        }

        return InteractionResult.FAIL;
    }

    public static InteractionResult useMultimeter(UseOnContext context) {
    	Level world = context.getLevel();
    	BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (world.isClientSide)
            return InteractionResult.PASS;

        BlockEntity te = world.getBlockEntity(pos);
        Block block = world.getBlockState(pos).getBlock();

        ISESimulatable delegatedNode = null;
        if (block instanceof ISENodeDelegateBlock) {
        	delegatedNode = ((ISENodeDelegateBlock<?>) block).getNode(world, pos);
        }

        if (!(	te instanceof ISECableTile ||
        		te instanceof ISETile ||
        		te instanceof ISETile) && delegatedNode == null)
            return InteractionResult.PASS;

        Utils.chat(player, "------------------");
        player.sendMessage(BlockUtils.getDisplayName(world, pos), net.minecraft.Util.NIL_UUID);

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

        return InteractionResult.PASS;
    }

    public static void printVI(ISESimulatable node, Player player) {
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

    private static InteractionResult useHVCableCutter(UseOnContext context) {
    	Level world = context.getLevel();
    	BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (world.isClientSide)
            return InteractionResult.PASS;

        BlockEntity te = world.getBlockEntity(pos);
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
            return InteractionResult.FAIL;
        }

        if (player.isCrouching()) {
            for (ISEGridNode neighbor : gridNode.getNeighborList()) {
                SEAPI.energyNetAgent.breakGridConnection(world, neighbor, gridNode);
            }
            lastCoordinates_cablecutter_hv.put(player, null);
            return InteractionResult.SUCCESS;
        }

        BlockPos lastSelectedPos = lastCoordinates_cablecutter_hv.get(player);
        if (lastSelectedPos == null) {
            Utils.chatWithLocalization(player, "chat.sime_essential:powerpole_selected");
            lastCoordinates_cablecutter_hv.put(player, pos);
        } else {
            lastCoordinates_cablecutter_hv.put(player, null);
            BlockEntity lastTE = world.getBlockEntity(lastSelectedPos);
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

        return InteractionResult.SUCCESS;
    }
}
