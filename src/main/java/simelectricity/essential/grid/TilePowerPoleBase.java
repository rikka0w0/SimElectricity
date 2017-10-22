package simelectricity.essential.grid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEEnergyTile;

public abstract class TilePowerPoleBase extends SEEnergyTile implements ISEGridTile, ISEPowerPole {
    protected BlockPos neighbor1, neighbor2;

    @Nonnull
    @SideOnly(Side.CLIENT)
    protected abstract PowerPoleRenderHelper createRenderHelper();
    
    //////////////////////////////
    /////ISEPowerPole
    //////////////////////////////
    @SideOnly(Side.CLIENT)
    private PowerPoleRenderHelper renderHelper;
    
    @Override
    @SideOnly(Side.CLIENT)
    public BlockPos[] getNeighborPosArray() {
        return new BlockPos[] {this.neighbor1, this.neighbor2};
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public PowerPoleRenderHelper getRenderHelper() {
        return this.renderHelper;
    }

    //////////////////////////////
    /////ISEGridTile
    //////////////////////////////
    protected ISEGridNode gridNode;
    @Override
    public ISEGridNode getGridNode() {
        return this.gridNode;
    }

    @Override
    public void setGridNode(ISEGridNode gridNode) {
        this.gridNode = gridNode;
    }

    @Override
    public void onGridNeighborUpdated() {
        this.neighbor1 = null;
        this.neighbor2 = null;

        ISEGridNode[] neighbors = this.gridNode.getNeighborList();
        if (neighbors.length == 1) {
            this.neighbor1 = neighbors[0].getPos();
        } else if (neighbors.length > 1) {
            this.neighbor1 = neighbors[0].getPos();
            this.neighbor2 = neighbors[1].getPos();
        }

        markTileEntityForS2CSync();
    }

    @Override
    public boolean canConnect(@Nullable BlockPos to) {
        return this.neighbor1 == null || this.neighbor2 == null;
    }
    
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
    public AxisAlignedBB getRenderBoundingBox() {
        return TileEntity.INFINITE_EXTENT_AABB;
    }
    
    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        Utils.saveToNbt(nbt, "neighbor1", this.neighbor1);
        Utils.saveToNbt(nbt, "neighbor2", this.neighbor2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        this.neighbor1 = Utils.posFromNbt(nbt, "neighbor1");
        this.neighbor2 = Utils.posFromNbt(nbt, "neighbor2");

        if (this.renderHelper == null)
            this.renderHelper = this.createRenderHelper();

        PowerPoleRenderHelper.notifyChanged(this);

        this.updateRenderInfo(this.neighbor1);
        this.updateRenderInfo(this.neighbor2);
    }

    @SideOnly(Side.CLIENT)
    protected void updateRenderInfo(BlockPos neighborPos) {
        if (neighborPos == null)
            return;

        TileEntity neighbor = this.world.getTileEntity(neighborPos);
        if (neighbor instanceof ISEPowerPole)
            PowerPoleRenderHelper.notifyChanged((ISEPowerPole) neighbor);
    }
}
