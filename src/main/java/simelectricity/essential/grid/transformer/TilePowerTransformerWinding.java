package simelectricity.essential.grid.transformer;

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
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEEnergyTile;
import simelectricity.essential.common.multiblock.ISEMultiBlockTile;
import simelectricity.essential.common.multiblock.MultiBlockTileInfo;
import simelectricity.essential.utils.Utils;

public abstract class TilePowerTransformerWinding extends SEEnergyTile implements ISEMultiBlockTile, ISEGridTile, ISEPowerPole {
    protected MultiBlockTileInfo mbInfo;

    @SideOnly(Side.CLIENT)
    protected EnumFacing facing;
    @SideOnly(Side.CLIENT)
    protected boolean mirrored;
    @SideOnly(Side.CLIENT)
    protected PowerPoleRenderHelper renderHelper;
    protected BlockPos neighbor;
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

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.mbInfo = new MultiBlockTileInfo(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        this.mbInfo.saveToNBT(nbt);
        return super.writeToNBT(nbt);
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        Utils.saveToNbt(nbt, "neighbor", this.neighbor);
        Utils.saveToNbt(nbt, "facing", this.mbInfo.facing);
        nbt.setBoolean("mirrored", this.mbInfo.mirrored);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        neighbor = Utils.posFromNbt(nbt, "neighbor");
        facing = Utils.facingFromNbt(nbt, "facing");
        mirrored = nbt.getBoolean("mirrored");

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

    //////////////////////////////
    /////ISEMultiBlockTile
    //////////////////////////////
    @Override
    public MultiBlockTileInfo getMultiBlockTileInfo() {
        return mbInfo;
    }

    @Override
    public void onStructureCreating(MultiBlockTileInfo mbInfo) {
        this.mbInfo = mbInfo;
        markDirty();

        gridNode = SEAPI.energyNetAgent.newGridNode(this.pos, 3);
        SEAPI.energyNetAgent.attachGridNode(this.world, this.gridNode);
    }

    @Override
    public void onStructureCreated() {
    }

    @Override
    public void onStructureRemoved() {
        SEAPI.energyNetAgent.detachGridNode(this.world, this.gridNode);
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
    ///ITransmissionTower
    /////////////////////////////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public PowerPoleRenderHelper getRenderHelper() {
        return this.renderHelper;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateRenderInfo() {
        this.renderHelper.updateRenderData(this.neighbor);
    }

    @SideOnly(Side.CLIENT)
    protected abstract PowerPoleRenderHelper createRenderHelper();

    public static class Primary extends TilePowerTransformerWinding {
        @Override
        public void onStructureCreated() {
            super.onStructureCreated();
            BlockPos pos = this.mbInfo.getPartPos(EnumBlockType.Secondary.offset);
            TilePowerTransformerWinding.Secondary secondaryTile = (TilePowerTransformerWinding.Secondary) this.world.getTileEntity(pos);
            SEAPI.energyNetAgent.makeTransformer(this.world, getGridNode(), secondaryTile.getGridNode(), 1, 1 / 3.5);
        }

        @Override
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.world, this.pos, this.facing, this.mirrored, 1, 3);
            renderHelper.addInsulatorGroup(0F, 2.8F, 0F,
                    renderHelper.createInsulator(0, 2, 0, 2.8F, 1.5F),
                    renderHelper.createInsulator(0, 2, 0, 2.8F, 0),
                    renderHelper.createInsulator(0, 2, 0, 2.8F, -1.5F));
            return renderHelper;
        }
    }

    public static class Secondary extends TilePowerTransformerWinding {
        @Override
        protected PowerPoleRenderHelper createRenderHelper() {
            //Create renderHelper on client side
            PowerPoleRenderHelper renderHelper = new PowerPoleRenderHelper(this.world, this.pos, this.facing, this.mirrored, 1, 3);
            renderHelper.addInsulatorGroup(0, 1.8F, 0,
                    renderHelper.createInsulator(0, 0.5F, 0, 2.1F, 0.8F),
                    renderHelper.createInsulator(0, 0.5F, 0, 2.1F, 0),
                    renderHelper.createInsulator(0, 0.5F, 0, 2.1F, -0.8F));
            return renderHelper;
        }
    }
}
