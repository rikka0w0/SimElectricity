package simelectricity.essential.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants.NBT;
import rikka.librikka.Utils;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;
import simelectricity.essential.api.coverpanel.ISEGuiCoverPanel;

public class CoverPanelUtils {
	public static ListTag coverPanelsToNBT(ISECoverPanelHost host, CompoundTag nbt) {
		return coverPanelsToNBT(host, nbt, "coverPanels");
	}

    public static ListTag coverPanelsToNBT(ISECoverPanelHost host, CompoundTag nbt, String name) {
        ListTag tagList = new ListTag();
        for (Direction side: Direction.values()) {
            ISECoverPanel coverPanel = host.getCoverPanelOnSide(side);
            if (coverPanel != null) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("side", side.ordinal());
                SEEAPI.coverPanelRegistry.saveToNBT(coverPanel, tag);
                tagList.add(tag);
            }
        }
        nbt.put(name, tagList);
        return tagList;
    }

    public static <T extends BlockEntity&ISECoverPanelHost> void coverPanelsFromNBT(T host, CompoundTag nbt, ISECoverPanel[] ret) {
		coverPanelsFromNBT(host, nbt, "coverPanels", ret);
    }

    public static <T extends BlockEntity&ISECoverPanelHost> void coverPanelsFromNBT(T host, CompoundTag nbt, String name, ISECoverPanel[] ret) {
    	if (ret == null || ret.length < Direction.values().length)
    		throw new RuntimeException("ISECoverPanel[] is invalid or not ready");

    	ListTag tagList = nbt.getList(name, NBT.TAG_COMPOUND);

    	for (Direction side: Direction.values())
        	ret[side.ordinal()] = null;

    	for (Direction side: Direction.values()) {
            CompoundTag tag = tagList.getCompound(side.ordinal());
            int sideId = tag.getInt("side");
            if (tag.contains("side") && sideId > -1 && sideId < Direction.values().length) {
                ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromNBT(tag);
                ret[sideId] = coverPanel;

                if (coverPanel != null) {
                    coverPanel.setHost(host, Direction.from3DDataValue(sideId));
                }
            }
        }
    }

    ///////////////////////////////
    /// CoverPanel Handler
    ///////////////////////////////
	public static InteractionResult installCoverPanel(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult ray) {
		Direction side = ray.getDirection();
        BlockEntity te = world.getBlockEntity(pos);

        if (!(te instanceof ISECoverPanelHost))
        	return InteractionResult.FAIL;        //Normally this could not happen, but just in case!
        ISECoverPanelHost host = (ISECoverPanelHost) te;

        ItemStack itemStack = player.getMainHandItem();
        if (itemStack == null || itemStack.isEmpty())
        	return InteractionResult.FAIL;

        // Check if it is an cover panel item
        ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromItemStack(itemStack);
        if (coverPanel == null)
        	return InteractionResult.FAIL;


        // Attempt to install cover panel
        if (host.installCoverPanel(side, coverPanel, true)) {
            if (!player.isCreative())
                itemStack.shrink(1);

            if (coverPanel instanceof ISEFacadeCoverPanel
            		&&((ISEFacadeCoverPanel)coverPanel).getBlockState().isAir())
            	return InteractionResult.FAIL;

            if (!world.isClientSide)    //Handle on server side
            	host.installCoverPanel(side, coverPanel, false);
            return InteractionResult.SUCCESS;
        }

    	return InteractionResult.FAIL;
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
    public static boolean removeCoverPanel(ISECoverPanelHost host, Player player) {
    	Level world = ((BlockEntity)host).getLevel();
    	BlockPos pos = ((BlockEntity)host).getBlockPos();

        Direction side = host.getSelectedCoverPanel(player);
        if (side == null) {
        	// Center, remove all cover panels and drop items
			for (Direction side2 : Direction.values()) {
				ISECoverPanel coverPanel = host.getCoverPanelOnSide(side2);
				if (host.removeCoverPanel(side2, true)) {
					if (!world.isClientSide)
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
				if (!world.isClientSide)
					host.removeCoverPanel(side, false);

		        //Spawn an item entity for player to pick up
				if(!player.isCreative())
					Utils.dropItemIntoWorld(world, pos, coverPanel.getDroppedItemStack());
			}

			return true;
        }
    }

    public static InteractionResult openCoverPanelGui(ISECoverPanelHost host, Player player) {
        if (player.isCrouching())
            return InteractionResult.FAIL;

        Direction panelSide = host.getSelectedCoverPanel(player);
        if (panelSide == null)
        	return InteractionResult.FAIL;
        ISECoverPanel coverPanel = host.getCoverPanelOnSide(panelSide);

        if (coverPanel instanceof ISEGuiCoverPanel) {
            player.openMenu((ISEGuiCoverPanel) coverPanel);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
