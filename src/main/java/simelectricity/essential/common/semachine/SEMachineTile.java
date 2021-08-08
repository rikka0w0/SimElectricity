package simelectricity.essential.common.semachine;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelDataMap;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEFacadeCoverPanel;
import simelectricity.essential.common.CoverPanelUtils;
import simelectricity.essential.common.SEEnergyTile;

/**
 * Handles sockets and facade panels
 * This type of machine only accepts facade panels.
 * Facades cannot block electrical connections when installed on a machine.
 * But non-hollow facades do prevents player from interacting with the machine.
 * @author Rikka0w0
 */
public abstract class SEMachineTile extends SEEnergyTile implements ISESocketProvider, ISECoverPanelHost {
	protected final ISECoverPanel[] installedCoverPanels = new ISECoverPanel[6];

    public SEMachineTile(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

    ///////////////////////////////////
    /// BlockEntity
    ///////////////////////////////////
    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        CoverPanelUtils.coverPanelsFromNBT(this, tagCompound, installedCoverPanels);
    }

    @Override
    public CompoundTag save(CompoundTag tagCompound) {
        CoverPanelUtils.coverPanelsToNBT(this, tagCompound);
        return super.save(tagCompound);
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
        super.prepareS2CPacketData(nbt);

        CoverPanelUtils.coverPanelsToNBT(this, nbt);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
        CoverPanelUtils.coverPanelsFromNBT(this, nbt, installedCoverPanels);

        // Flag 1 - update Rendering Only!
        this.markForRenderUpdate();

        super.onSyncDataFromServerArrived(nbt);
    }

    @Override
    protected void collectModelData(ModelDataMap.Builder builder) {
    	builder.withInitial(ISESocketProvider.prop, this)
    	.withInitial(ISECoverPanelHost.prop, this);
    }

    protected void postCoverPanelModification() {
        level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());

        //Initiate Server->Client synchronization
		this.markTileEntityForS2CSync();
    }

    /////////////////////////////////////////////////////////
    /// ISECoverPanelHost
    /////////////////////////////////////////////////////////
    @Override
    public Direction getSelectedCoverPanel(Player player) {
        Vec3 start = player.position().add(0, player.getEyeHeight(), 0);
        double reachDistance = 5;
        Vec3 end = start.add(player.getLookAngle().normalize().scale(reachDistance));

        Level world = this.getLevel();
        BlockPos pos = this.getBlockPos();
        BlockState blockstate = this.getBlockState();
        VoxelShape shape = blockstate.getShape(world, pos);
        BlockHitResult result = world.clipWithInteractionOverride(start, end, pos, shape, blockstate);
        if (result == null || result.getType() != BlockHitResult.Type.BLOCK)
        	return null;

        Direction side = result.getDirection();
        if (installedCoverPanels[side.ordinal()] == null)
        	return null;	// No cover panel installed on this side

        return side;
	}

    @Override
    public ISECoverPanel getCoverPanelOnSide(Direction side) {
        return installedCoverPanels[side.ordinal()];
	}

    @Override
    public boolean installCoverPanel(Direction side, ISECoverPanel coverPanel, boolean simulated) {
    	if (installedCoverPanels[side.ordinal()] != null)
    		return false;	// Already installed

    	if (!(coverPanel instanceof ISEFacadeCoverPanel))
    		return false;	// Not a facade

    	if (simulated)
    		return true;

    	installedCoverPanels[side.ordinal()] = coverPanel;
        coverPanel.setHost((BlockEntity)this, side);

        postCoverPanelModification();

		return true;
	}

    @Override
    public boolean removeCoverPanel(Direction side, boolean simulated) {
        if (side == null || installedCoverPanels[side.ordinal()] == null)
            return false;

        if (simulated)
        	return true;

        //Remove the panel
//        ISECoverPanel coverPanel = installedCoverPanels[side.ordinal()];
        installedCoverPanels[side.ordinal()] = null;

		postCoverPanelModification();

		return true;
	}
}
