package simelectricity.essential.grid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.utils.Utils;

public class TileCableJoint extends SEEnergyTile implements ISECableTile, ISEGridTile, ISEPowerPole {
    private final ISESimulatable cableNode = SEAPI.energyNetAgent.newCable(this, true);

    @SideOnly(Side.CLIENT)
    private PowerPoleRenderHelper renderHelper;
    private BlockPos neighbor;
    //////////////////////////////
    /////ISEGridTile
    //////////////////////////////
    private ISEGridNode gridNode;

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

    /////////////////////////////////////////////////////////
    ///ISECableTile
    /////////////////////////////////////////////////////////
    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public ISESimulatable getNode() {
        return this.cableNode;
    }

    @Override
    public boolean canConnectOnSide(EnumFacing direction) {
        return direction == EnumFacing.DOWN;
    }

    @Override
    public boolean isGridLinkEnabled() {
        return true;
    }

    @Override
    public boolean hasShuntResistance() {
        return false;
    }

    @Override
    public double getShuntResistance() {
        return 0;
    }

    @Override
    public ISEGridNode getGridNode() {
        return this.gridNode;
    }

    @Override
    public void setGridNode(ISEGridNode gridObj) {
        gridNode = gridObj;
    }

    @Override
    public void onGridNeighborUpdated() {
        neighbor = null;
        f:
        for (ISESimulatable neighbor : this.gridNode.getNeighborList()) {
            if (neighbor instanceof ISEGridNode) {
                ISEGridNode gridNode = (ISEGridNode) neighbor;
                this.neighbor = gridNode.getPos().toImmutable();
                break f;
            }
        }

        markTileEntityForS2CSync();
    }

    public boolean canConnect() {
        return this.neighbor == null;
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        Utils.saveToNbt(nbt, "neighbor", this.neighbor);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        neighbor = Utils.posFromNbt(nbt, "neighbor");

        if (this.renderHelper == null)
            this.renderHelper = this.createRenderHelper();

        PowerPoleRenderHelper.notifyChanged(this);
        //this.updateRenderInfo();

        if (this.neighbor != null) {
            TileEntity neighborTile = this.world.getTileEntity(neighbor);
            if (neighborTile instanceof ISEPowerPole)
                PowerPoleRenderHelper.notifyChanged((ISEPowerPole) neighborTile);
            //((ISEPowerPole)neighborTile).updateRenderInfo();
        }

        super.onSyncDataFromServerArrived(nbt);
    }

    /////////////////////////////////////////////////////////
    ///ITransmissionTower
    /////////////////////////////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public void updateRenderInfo() {
        this.renderHelper.updateRenderData(this.neighbor);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public PowerPoleRenderHelper getRenderHelper() {
        return this.renderHelper;
    }

    @SideOnly(Side.CLIENT)
    protected PowerPoleRenderHelper createRenderHelper() {
        //Create renderHelper on client side
        int rotation = this.world.getBlockState(this.pos).getValue(Properties.propertyFacing);
        PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.world, this.pos, rotation, 1, 3);
        renderHelper.addInsulatorGroup(0.6F, 1.45F, 0F,
                renderHelper.createInsulator(0, 2, -0.3F, 1.17F, -0.95F),
                renderHelper.createInsulator(0, 2, 0.6F, 1.45F, 0F),
                renderHelper.createInsulator(0, 2, -0.3F, 1.17F, 0.95F));

        return renderHelper;
    }
}
