package simelectricity.essential.common;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import rikka.librikka.Utils;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;
import simelectricity.essential.api.coverpanel.ISEGuiCoverPanel;

public class CoverPanelUtils {
	public static ListNBT coverPanelsToNBT(ISECoverPanelHost host, CompoundNBT nbt) {
		return coverPanelsToNBT(host, nbt, "coverPanels");
	}
	
    public static ListNBT coverPanelsToNBT(ISECoverPanelHost host, CompoundNBT nbt, String name) {
        ListNBT tagList = new ListNBT();
        for (Direction side: Direction.values()) {
            ISECoverPanel coverPanel = host.getCoverPanelOnSide(side);
            if (coverPanel != null) {
                CompoundNBT tag = new CompoundNBT();
                tag.putInt("side", side.ordinal());
                SEEAPI.coverPanelRegistry.saveToNBT(coverPanel, tag);
                tagList.add(tag);
            }
        }
        nbt.put(name, tagList);
        return tagList;
    }

    public static <T extends TileEntity&ISECoverPanelHost> void coverPanelsFromNBT(T host, CompoundNBT nbt, ISECoverPanel[] ret) {
		coverPanelsFromNBT(host, nbt, "coverPanels", ret);
    }
    
    public static <T extends TileEntity&ISECoverPanelHost> void coverPanelsFromNBT(T host, CompoundNBT nbt, String name, ISECoverPanel[] ret) {
    	if (ret == null || ret.length < Direction.values().length)
    		throw new RuntimeException("ISECoverPanel[] is invalid or not ready");
    	
    	ListNBT tagList = nbt.getList(name, NBT.TAG_COMPOUND);
    	
    	for (Direction side: Direction.values())
        	ret[side.ordinal()] = null;

    	for (Direction side: Direction.values()) {
            CompoundNBT tag = tagList.getCompound(side.ordinal());
            int sideId = tag.getInt("side");
            if (tag.contains("side") && sideId > -1 && sideId < Direction.values().length) {
                ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromNBT(tag);
                ret[sideId] = coverPanel;

                if (coverPanel != null) {
                    coverPanel.setHost(host, Direction.byIndex(sideId));
                }
            }
        }
    }
    
    ///////////////////////////////
    /// CoverPanel Handler
    ///////////////////////////////
	public static ActionResultType installCoverPanel(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult ray) {
		Direction side = ray.getFace();
        TileEntity te = world.getTileEntity(pos);
        
        if (!(te instanceof ISECoverPanelHost))
        	return ActionResultType.FAIL;        //Normally this could not happen, but just in case!
        ISECoverPanelHost host = (ISECoverPanelHost) te;
        
        ItemStack itemStack = player.getHeldItemMainhand();
        if (itemStack == null || itemStack.isEmpty())
        	return ActionResultType.FAIL;
        
        // Check if it is an cover panel item
        ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromItemStack(itemStack);
        if (coverPanel == null)
        	return ActionResultType.FAIL;
        
        
        // Attempt to install cover panel
        if (host.installCoverPanel(side, coverPanel, true)) {
            if (!player.isCreative())
                itemStack.shrink(1);

            if (coverPanel instanceof ISEFacadeCoverPanel
            		&&((ISEFacadeCoverPanel)coverPanel).getBlockState().isAir())
            	return ActionResultType.FAIL;
            
            if (!world.isRemote)    //Handle on server side
            	host.installCoverPanel(side, coverPanel, false);
            return ActionResultType.SUCCESS;
        }
        
    	return ActionResultType.FAIL;
	}
	
	/**
	 * Attempt to remove a cover panel selected by the player, 
	 * called by {@link net.minecraftforge.common.extensions.IForgeBlock#removedByPlayer}
	 * @param state
	 * @param world
	 * @param pos
	 * @param player
	 * @return true if the cover panel is removed and the host should be kept. In this case, call super.removedByPlayer.
	 */
    public static boolean removeCoverPanel(ISECoverPanelHost host, PlayerEntity player) {
    	World world = ((TileEntity)host).getWorld();
    	BlockPos pos = ((TileEntity)host).getPos();
        
        Direction side = host.getSelectedCoverPanel(player);
        if (side == null) {
        	// Center, remove all cover panels and drop items
			for (Direction side2 : Direction.values()) {
				ISECoverPanel coverPanel = host.getCoverPanelOnSide(side2);
				if (host.removeCoverPanel(side2, true)) {
					if (!world.isRemote)
						host.removeCoverPanel(side2, false);
					
			        //Spawn an item entity for player to pick up
					if(!player.isCreative())
						Utils.dropItemIntoWorld(world, pos, coverPanel.getDroppedItemStack());
				}
			}
			
			return false;
        } else {
        	// CoverPanel
        	ISECoverPanel coverPanel = host.getCoverPanelOnSide(side);
			if (host.removeCoverPanel(side, true)) {
				if (!world.isRemote)
					host.removeCoverPanel(side, false);
				
		        //Spawn an item entity for player to pick up
				if(!player.isCreative())
					Utils.dropItemIntoWorld(world, pos, coverPanel.getDroppedItemStack());
			}

			return true;
        }
    }
    
    public static ActionResultType openCoverPanelGui(ISECoverPanelHost host, PlayerEntity player) {
        if (player.isCrouching())
            return ActionResultType.FAIL;

        Direction panelSide = host.getSelectedCoverPanel(player);
        if (panelSide == null)
        	return ActionResultType.FAIL;
        ISECoverPanel coverPanel = host.getCoverPanelOnSide(panelSide);

        if (coverPanel instanceof ISEGuiCoverPanel) {
            player.openContainer((ISEGuiCoverPanel) coverPanel);
            return ActionResultType.SUCCESS;
        }
        
        return ActionResultType.FAIL;
    }
}
