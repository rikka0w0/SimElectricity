package simelectricity.essential.common.semachine;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
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
    
    ///////////////////////////////////
    /// TileEntity
    ///////////////////////////////////
    @Override
    public void read(BlockState blockState, CompoundNBT tagCompound) {
        super.read(blockState, tagCompound);
        CoverPanelUtils.coverPanelsFromNBT(this, tagCompound, installedCoverPanels);
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        CoverPanelUtils.coverPanelsToNBT(this, tagCompound);
        return super.write(tagCompound);
    }
    
    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundNBT nbt) {
        super.prepareS2CPacketData(nbt);

        CoverPanelUtils.coverPanelsToNBT(this, nbt);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onSyncDataFromServerArrived(CompoundNBT nbt) {
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
        world.notifyNeighborsOfStateChange(pos, this.getBlockState().getBlock());
        
        //Initiate Server->Client synchronization
		this.markTileEntityForS2CSync();
    }
    
    /////////////////////////////////////////////////////////
    /// ISECoverPanelHost
    /////////////////////////////////////////////////////////    
    @Override
    public Direction getSelectedCoverPanel(PlayerEntity player) {
        Vector3d start = player.getPositionVec().add(0, player.getEyeHeight(), 0);
        double reachDistance = 5;
        Vector3d end = start.add(player.getLookVec().normalize().scale(reachDistance));

        World world = this.getWorld();
        BlockPos pos = this.getPos();
        BlockState blockstate = this.getBlockState();
        VoxelShape shape = blockstate.getShape(world, pos);
        BlockRayTraceResult result = world.rayTraceBlocks(start, end, pos, shape, blockstate);
        if (result == null || result.getType() != RayTraceResult.Type.BLOCK)
        	return null;
        
        Direction side = result.getFace();
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
        coverPanel.setHost((TileEntity)this, side);
        
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
