package simelectricity.essential.cable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import rikka.librikka.tileentity.ISEGuiProvider;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISESimulatable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.ISEIuminousCoverPanelHost;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.api.coverpanel.*;
import simelectricity.essential.common.SEEnergyTile;

public class TileCable extends SEEnergyTile implements ISEGenericCable, ISEIuminousCoverPanelHost, ISECableTile, IEnergyNetUpdateHandler, ISEGuiProvider {
    public boolean emitRedstoneSignal;
    /**
     * Accessible from client
     */
    public byte lightLevel;
    private final ISESimulatable node = SEAPI.energyNetAgent.newCable(this, false);
    private int color;
    private double resistance = 10;
    private double voltage;
    private final boolean[] connections = new boolean[6];
    private final ISECoverPanel[] installedCoverPanels = new ISECoverPanel[6];

    ////////////////////////////////////////
    //Private functions
    ////////////////////////////////////////
    private NBTTagList coverPanelsToNBT() {
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < this.installedCoverPanels.length; i++) {
            ISECoverPanel coverPanel = this.installedCoverPanels[i];
            if (coverPanel != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("side", i);
                coverPanel.toNBT(tag);
                tagList.appendTag(tag);
            }
        }
        return tagList;
    }

    private void coverPanelsFromNBT(NBTTagList tagList) {
        for (int i = 0; i < 6; i++)
			this.installedCoverPanels[i] = null;

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            int side = tag.getInteger("side");
            if (side > -1 && side < this.installedCoverPanels.length) {
                ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromNBT(tag);
				this.installedCoverPanels[side] = coverPanel;

                if (coverPanel != null)
                    coverPanel.setHost(this, EnumFacing.getFront(side));
            }
        }
    }

    public void setResistanceOnPlace(double resistance) {
        this.resistance = resistance;
    }

    ////////////////////////////////////////
    //ISEGenericCable
    ////////////////////////////////////////
    @Override
    public void onCableRenderingUpdateRequested() {
        //Update connection
        EnumFacing[] dirs = EnumFacing.VALUES;
        for (int i = 0; i < 6; i++) {
			this.connections[i] = SEAPI.energyNetAgent.canConnectTo(this, dirs[i]);
        }


        //Initiate Server->Client synchronization
		this.markTileEntityForS2CSync();
    }

    @Override
    public boolean connectedOnSide(EnumFacing side) {
        return this.connections[side.ordinal()];
    }

    @Override
    public ISECoverPanel getSelectedCoverPanel(EntityPlayer player) {
        Block block = this.getBlockType();
        if (block instanceof BlockCable) {
            RayTraceResult result = ((BlockCable) block).rayTrace(this.world, this.pos, player);

            if (result.subHit < 7 || result.subHit > 12)
                return null;    //The player is not looking at anyinstalled cover panel

            EnumFacing side = EnumFacing.getFront(result.subHit - 7);
            return this.installedCoverPanels[side.ordinal()];
        }
        return null;
    }

    @Override
    public ISECoverPanel getCoverPanelOnSide(EnumFacing side) {
        return this.installedCoverPanels[side.ordinal()];
    }

    @Override
    public boolean canInstallCoverPanelOnSide(EnumFacing side, ISECoverPanel coverPanel) {
        return this.installedCoverPanels[side.ordinal()] == null;
    }

    @Override
    public void installCoverPanel(EnumFacing side, ISECoverPanel coverPanel) {
		this.installedCoverPanels[side.ordinal()] = coverPanel;
        coverPanel.setHost(this, side);

        if (!coverPanel.isHollow()) {
            //If the cover panel is not hollow, it may block some connection
            if (this.connectedOnSide(side))
                SEAPI.energyNetAgent.updateTileConnection(this);
        }

        if (coverPanel instanceof ISEElectricalCoverPanel)
            ((ISEElectricalCoverPanel) coverPanel).onPlaced(this.voltage);

        if (coverPanel instanceof ISEElectricalLoadCoverPanel)
            SEAPI.energyNetAgent.updateTileConnection(this);

		this.onCableRenderingUpdateRequested();
    }

    @Override
    public boolean removeCoverPanel(ISECoverPanel coverPanel, boolean dropItem) {
        if (coverPanel == null)
            return false;

        //Look for that panel and remove it
        EnumFacing side = null;
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (this.installedCoverPanels[facing.ordinal()] == coverPanel)
                side = facing;
        }

        if (side == null)
            return false;

        //Remove the panel
		this.installedCoverPanels[side.ordinal()] = null;

        if (coverPanel instanceof ISEElectricalLoadCoverPanel)
            SEAPI.energyNetAgent.updateTileConnection(this);

		this.onLightValueUpdated();

        //Notify neighbor block that this side no longer emits redstone signal
        if (coverPanel instanceof ISERedstoneEmitterCoverPanel)
			this.world.notifyNeighborsOfStateChange(this.pos, this.blockType, false);

		this.onCableRenderingUpdateRequested();

        if (!coverPanel.isHollow())
			this.world.neighborChanged(this.pos.offset(side), getBlockType(), this.pos);

        //Spawn an item entity for player to pick up
        if (dropItem)
            Utils.dropItemIntoWorld(this.world, this.pos, coverPanel.getDroppedItemStack());

        return true;
    }

    ///////////////////////////////////////
    ///TileEntity
    ///////////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = TileEntity.INFINITE_EXTENT_AABB;
        bb = new AxisAlignedBB(this.pos, this.pos.add(1, 1, 1));
        return bb;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

		this.color = tagCompound.getInteger("color");
		this.resistance = tagCompound.getDouble("resistance");
		this.coverPanelsFromNBT(tagCompound.getTagList("coverPanels", NBT.TAG_COMPOUND));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("color", this.color);
        tagCompound.setDouble("resistance", this.resistance);
        tagCompound.setTag("coverPanels", this.coverPanelsToNBT());

        return super.writeToNBT(tagCompound);
    }

    ///////////////////////////////////////
    ///ISECableTile
    ///////////////////////////////////////
    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public double getResistance() {
        return this.resistance;
    }

    @Override
    public ISESimulatable getNode() {
        return this.node;
    }

    @Override
    public boolean canConnectOnSide(EnumFacing direction) {
        ISECoverPanel coverPanel = this.getCoverPanelOnSide(direction);
        if (coverPanel == null)
            return true;
        else
            return coverPanel.isHollow();
    }

    @Override
    public boolean isGridLinkEnabled() {
        return false;
    }

    @Override
    public boolean hasShuntResistance() {
        boolean hasShuntResistance = false;
        for (ISECoverPanel coverPanel : installedCoverPanels)
            hasShuntResistance |= coverPanel instanceof ISEElectricalLoadCoverPanel;
        return hasShuntResistance;
    }

    @Override
    public double getShuntResistance() {
        double shuntConductance = 0;
        for (ISECoverPanel coverPanel : installedCoverPanels)
            if (coverPanel instanceof ISEElectricalLoadCoverPanel)
                shuntConductance += 1.0D / ((ISEElectricalLoadCoverPanel) coverPanel).getResistance();

        return 1.0D / shuntConductance;
    }

    ////////////////////////////////////////
    //Server->Client sync
    ////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        super.prepareS2CPacketData(nbt);

        byte bc = 0x00;
        if (this.connections[0]) bc |= 1;
        if (this.connections[1]) bc |= 2;
        if (this.connections[2]) bc |= 4;
        if (this.connections[3]) bc |= 8;
        if (this.connections[4]) bc |= 16;
        if (this.connections[5]) bc |= 32;

        nbt.setByte("connections", bc);

        nbt.setTag("coverPanels", this.coverPanelsToNBT());

        nbt.setByte("lightLevel", this.lightLevel);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
        byte connectionsBinary = nbt.getByte("connections");

		connections[0] = (connectionsBinary & 1) > 0;
		connections[1] = (connectionsBinary & 2) > 0;
		connections[2] = (connectionsBinary & 4) > 0;
		connections[3] = (connectionsBinary & 8) > 0;
		connections[4] = (connectionsBinary & 16) > 0;
		connections[5] = (connectionsBinary & 32) > 0;

		this.coverPanelsFromNBT(nbt.getTagList("coverPanels", NBT.TAG_COMPOUND));

        byte lightLevel = nbt.getByte("lightLevel");
        if (this.lightLevel != lightLevel) {
            this.lightLevel = lightLevel;
            //Detect change & proceed
			this.world.checkLight(this.pos);
            //world.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);	//checkLightFor
        }

        // Flag 1 - update Rendering Only!
		this.markForRenderUpdate();
    }

    ////////////////////////////////////////
    //IEnergyNetUpdateHandler
    ////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
		this.voltage = SEAPI.energyNetAgent.getVoltage(node);

        for (ISECoverPanel coverPanel : installedCoverPanels) {
            if (coverPanel instanceof ISEElectricalCoverPanel)
                ((ISEElectricalCoverPanel) coverPanel).onEnergyNetUpdate(this.voltage);
        }
    }

    ///////////////////////
    ///ISEGuiProvider
    ///////////////////////
    @Override
    public Container getContainer(EntityPlayer player, EnumFacing side) {
        ISECoverPanel coverPanel = this.installedCoverPanels[side.ordinal()];
        return coverPanel instanceof ISEGuiCoverPanel ? ((ISEGuiCoverPanel) coverPanel).getContainer(player, this) : null;
    }

    /////////////////////////////////
    ///ISEIuminousCoverPanelHost
    /////////////////////////////////
    @Override
    public void onLightValueUpdated() {
        byte lightLevel = 0;
        for (ISECoverPanel coverPanel : installedCoverPanels) {
            if (coverPanel instanceof ISEIuminousCoverPanel) {
                byte ll = ((ISEIuminousCoverPanel) coverPanel).getLightValue();
                if (ll > lightLevel)
                    lightLevel = ll;
            }
        }

        if (this.lightLevel != lightLevel) {
            this.lightLevel = lightLevel;
			markTileEntityForS2CSync();
        }
    }
}
