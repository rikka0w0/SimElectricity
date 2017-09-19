package simelectricity.essential.grid;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import simelectricity.api.node.ISEGridNode;
import simelectricity.essential.api.ISEPoleAccessory;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEEnergyTile;

public abstract class TilePoleAccessory extends SEEnergyTile implements ISEPoleAccessory, ISEPowerPole {
    protected BlockPos host;
    
    @Nonnull
    @SideOnly(Side.CLIENT)
    protected abstract PowerPoleRenderHelper createRenderHelper();
    
    @SideOnly(Side.CLIENT)
    protected boolean scheduleBlockRenderUpdateWhenChange() {
    	return false;
    }
    
    /////////////////////////////////////////////////////////
    /////ISEPowerPole
    /////////////////////////////////////////////////////////
    @SideOnly(Side.CLIENT)
    private PowerPoleRenderHelper renderHelper;
    
    @Override
    @SideOnly(Side.CLIENT)
	public void updateRenderInfo() {
        this.renderHelper.updateRenderData(this.host);
        if (this.scheduleBlockRenderUpdateWhenChange())
            markForRenderUpdate();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public PowerPoleRenderHelper getRenderHelper() {
        return this.renderHelper;
    }
    
    //////////////////////////////
    /////ISEGridTile
    //////////////////////////////
    private ISEGridNode gridNode;
    @Override
    public ISEGridNode getGridNode() {
        return this.gridNode;
    }

    @Override
    public void setGridNode(ISEGridNode gridObj) {
        this.gridNode = gridObj;
    }

    @Override
    public void onGridNeighborUpdated() {
    	host = null;
        
    	ISEGridNode[] neighbors = this.gridNode.getNeighborList();
    	if (neighbors.length > 0)
    		host = neighbors[0].getPos();

        markTileEntityForS2CSync();
    }
    
    @Override
    public abstract boolean canConnect(BlockPos toPos);
    
    //////////////////////////////
    /////TileEntity
    //////////////////////////////
    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return 100000;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }
    
    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        Utils.saveToNbt(nbt, "neighbor", this.host);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
    	host = Utils.posFromNbt(nbt, "neighbor");
    	
        if (this.renderHelper == null) 
            this.renderHelper = this.createRenderHelper();
        
        PowerPoleRenderHelper.notifyChanged(this);
        
        if (this.host != null) {
            TileEntity neighborTile = this.world.getTileEntity(host);
            if (neighborTile instanceof ISEPowerPole) 
                PowerPoleRenderHelper.notifyChanged((ISEPowerPole) neighborTile);
        }
        
        super.onSyncDataFromServerArrived(nbt);
    }
}
