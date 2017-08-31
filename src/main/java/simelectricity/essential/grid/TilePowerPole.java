package simelectricity.essential.grid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;
import simelectricity.api.node.ISEGridNode;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.SEEnergyTile;


public class TilePowerPole extends SEEnergyTile implements ISEGridTile, ISEPowerPole {
    private BlockPos neighbor1, neighbor2;
    @SideOnly(Side.CLIENT)
    private PowerPoleRenderHelper renderHelper;
    //////////////////////////////
    /////ISEGridTile
    //////////////////////////////
    private ISEGridNode gridNode;

    @SideOnly(Side.CLIENT)
    protected boolean scheduleBlockRenderUpdateWhenChange() {
        return this.isSpecial();
    }

    protected boolean isSpecial() {
        return this.getBlockMetadata() >> 3 == 0;
    }

    @SideOnly(Side.CLIENT)
    protected PowerPoleRenderHelper createRenderHelper() {
        PowerPoleRenderHelper helper;
        int rotation = this.getBlockMetadata() & 7;

        if (this.isSpecial()) {
            helper = new PowerPoleRenderHelper(TilePowerPole.this.world, TilePowerPole.this.pos, rotation, 2, 3) {
                @Override
                public void updateRenderData(BlockPos... neighborPosList) {
                    super.updateRenderData(neighborPosList);

                    if (this.connectionInfo.size() < 2)
                        return;

                    PowerPoleRenderHelper.ConnectionInfo[] connection1 = this.connectionInfo.getFirst();
                    PowerPoleRenderHelper.ConnectionInfo[] connection2 = connectionInfo.getLast();

                    Vec3f pos = new Vec3f(
                            3.95F * MathHelper.sin(rotation / 180F * MathAssitant.PI) + 0.5F + this.pos.getX(),
                            this.pos.getY() + 23 - 18,
                            3.95F * MathHelper.cos(rotation / 180F * MathAssitant.PI) + 0.5F + this.pos.getZ()
                    );

                    this.addExtraWire(connection1[1].fixedFrom, pos, 2.5F);
                    this.addExtraWire(pos, connection2[1].fixedFrom, 2.5F);
                    if (PowerPoleRenderHelper.hasIntersection(
                            connection1[0].fixedFrom, connection2[0].fixedFrom,
                            connection1[2].fixedFrom, connection2[2].fixedFrom)) {
                        this.addExtraWire(connection1[0].fixedFrom, connection2[2].fixedFrom, 2.5F);
                        this.addExtraWire(connection1[2].fixedFrom, connection2[0].fixedFrom, 2.5F);
                    } else {
                        this.addExtraWire(connection1[0].fixedFrom, connection2[0].fixedFrom, 2.5F);
                        this.addExtraWire(connection1[2].fixedFrom, connection2[2].fixedFrom, 2.5F);
                    }
                }
            };
            helper.addInsulatorGroup(-0.7F, 5, 0,
                    helper.createInsulator(2, 3, -1, 0, -4.5F),
                    helper.createInsulator(2, 3, -0.7F, 5, 0),
                    helper.createInsulator(2, 3, -1, 0, 4.5F)
            );
            helper.addInsulatorGroup(0.7F, 5, 0,
                    helper.createInsulator(2, 3, 1, 0, -4.5F),
                    helper.createInsulator(2, 3, 0.7F, 5, 0),
                    helper.createInsulator(2, 3, 1, 0, 4.5F)
            );
        } else {
            helper = new PowerPoleRenderHelper(this.world, this.pos, rotation, 1, 3);
            helper.addInsulatorGroup(0, 5, 3.95F,
                    helper.createInsulator(0, 3, 0, -2, -4.9F),
                    helper.createInsulator(0, 3, 0, 5, 3.95F),
                    helper.createInsulator(0, 3, 0, -2, 4.9F)
            );
        }

        return helper;
    }

    //////////////////////////////
    /////ITransmissionTower
    //////////////////////////////
    @SideOnly(Side.CLIENT)
    @Override
    public void updateRenderInfo() {
        this.getRenderHelper().updateRenderData(this.neighbor1, this.neighbor2);
        if (this.scheduleBlockRenderUpdateWhenChange())
            markForRenderUpdate();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public PowerPoleRenderHelper getRenderHelper() {
        return this.renderHelper;
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

        //notifyNeighbor(neighbor1);
        //notifyNeighbor(neighbor2);
    }

    public boolean canConnect() {
        return this.neighbor1 == null || this.neighbor2 == null;
    }

    private void notifyNeighbor(BlockPos neighbor) {
        if (neighbor == null)
            return;
        IBlockState state = this.world.getBlockState(neighbor);
        this.world.notifyBlockUpdate(this.neighbor1, state, state, 2);
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
        //this.updateRenderInfo();

        this.updateRenderInfo(this.neighbor1);
        this.updateRenderInfo(this.neighbor2);

        super.onSyncDataFromServerArrived(nbt);
    }

    @SideOnly(Side.CLIENT)
    private void updateRenderInfo(BlockPos neighborPos) {
        if (neighborPos == null)
            return;

        TileEntity neighbor = this.world.getTileEntity(neighborPos);
        if (neighbor instanceof ISEPowerPole)
            PowerPoleRenderHelper.notifyChanged((ISEPowerPole) neighbor);
        //((ISEPowerPole)neighbor).updateRenderInfo();
    }


}
