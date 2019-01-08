package simelectricity.essential.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rikka.librikka.Utils;
import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.item.ItemBase;
import simelectricity.api.ISECrowbarTarget;
import simelectricity.api.ISESidedFacing;
import simelectricity.api.ISEWrenchable;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISECable;
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

public class ItemTools extends ItemBase implements ISimpleTexture {
    private static final String[] subNames = {"crowbar", "wrench", "glove", "multimeter"};

    public ItemTools() {
        super("essential_tools", true);
        setMaxStackSize(1);
        setCreativeTab(SEAPI.SETab);
    }

    public static EnumActionResult useCrowbar(TileEntity te, EntityPlayer player, EnumFacing side) {
        if (te instanceof ISECoverPanelHost) {
            ISECoverPanel coverPanel = ((ISECoverPanelHost) te).getSelectedCoverPanel(player);

            if (coverPanel == null)
                return EnumActionResult.FAIL;

            if (te.getWorld().isRemote) {
                return EnumActionResult.PASS;
            } else {
                boolean ret = ((ISECoverPanelHost) te).removeCoverPanel(coverPanel, !player.capabilities.isCreativeMode);
                return ret ? EnumActionResult.PASS : EnumActionResult.FAIL;
            }
        } else if (te instanceof ISECrowbarTarget) {
            ISECrowbarTarget crowbarTarget = (ISECrowbarTarget) te;

            EnumFacing selectedDirection = side;

            if (crowbarTarget.canCrowbarBeUsed(selectedDirection)) {
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

    public static EnumActionResult useWrench(TileEntity te, EntityPlayer player, EnumFacing side) {
        if (te instanceof ISEWrenchable) {
            ISEWrenchable wrenchTarget = (ISEWrenchable) te;

            if (wrenchTarget.canWrenchBeUsed(side)) {
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

    public static EnumActionResult useGlove(TileEntity te, EntityPlayer player, EnumFacing side) {
        if (te instanceof ISESidedFacing) {
            ISESidedFacing target = (ISESidedFacing) te;

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

    public static EnumActionResult useMultimeter(World world, BlockPos pos, EntityPlayer player, EnumFacing side) {
        if (world.isRemote)
            return EnumActionResult.PASS;
        
        TileEntity te = world.getTileEntity(pos);
        Block block = world.getBlockState(pos).getBlock();
        
        ISESimulatable delegatedNode = null;
        if (block instanceof ISENodeDelegateBlock) {
        	delegatedNode = ((ISENodeDelegateBlock) block).getNode(world, pos);
        }
        
        if (!(	te instanceof ISECableTile ||
        		te instanceof ISETile ||
        		te instanceof ISETile) && delegatedNode == null)
            return EnumActionResult.PASS;
        
        Utils.chat(player, "------------------");
        
        if (block instanceof ISENodeDelegateBlock) {
        	delegatedNode = ((ISENodeDelegateBlock) block).getNode(world, pos);
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

            for (EnumFacing dir : EnumFacing.VALUES) {
                ISESubComponent comp = tile.getComponent(dir);
                if (comp != null && comp != delegatedNode) {
                    String[] temp = comp.toString().split("[.]");
                    Utils.chat(player, temp[temp.length - 1].split("@")[0] + ": " +
                            SEUnitHelper.getVoltageStringWithUnit(comp.getVoltage()));
                }
            }

        } 
        
        if (te instanceof ISEGridTile) {
            ISEGridNode node = ((ISEGridTile) te).getGridNode();
            if (node != delegatedNode)
            	ItemTools.printVI(node, player);
        }
        
        return EnumActionResult.PASS;
    }

    public static void printVI(ISESimulatable node, EntityPlayer player) {
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

    @Override
    public String[] getSubItemUnlocalizedNames() {
        return ItemTools.subNames;
    }

    @Override
    public String getIconName(int damage) {
        return "tool_" + ItemTools.subNames[damage];
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

        if (itemStack.getItem() != this) {
            itemStack = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            if (itemStack.getItem() != this)
                return EnumActionResult.FAIL;
        }

        TileEntity te = world.getTileEntity(pos);

        switch (itemStack.getItemDamage()) {
            case 0:
                return ItemTools.useCrowbar(te, player, side);
            case 1:
                return ItemTools.useWrench(te, player, side);
            case 2:
                return ItemTools.useGlove(te, player, side);
            case 3:
                return ItemTools.useMultimeter(world, pos, player, side);
        }

        return EnumActionResult.PASS;
    }
}
