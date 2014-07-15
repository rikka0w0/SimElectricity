package simElectricity.Blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.Events.TileAttachEvent;
import simElectricity.API.Events.TileDetachEvent;
import simElectricity.API.Energy;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Util;

public class TileWire extends TileEntity implements IConductor, ISyncPacketHandler {
    protected boolean isAddedToEnergyNet = false;
    public boolean[] renderSides = new boolean[6];

    public float resistance = 100;
    public float width = 0.1F;
    public String textureString;

    public TileWire() {
    }

    public TileWire(int meta) {
        super();
        resistance = BlockWire.resistanceList[meta];
        width = BlockWire.renderingWidthList[meta];
        textureString = BlockWire.subNames[meta];
    }

    @Override
    public void onClient2ServerUpdate(String field, Object value, short type) {
    }

    @Override
    public void onServer2ClientUpdate(String field, Object value, short type) {
        //worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        updateSides();
    }

    public void updateSides() {
        ForgeDirection[] dirs = ForgeDirection.values();
        for (int i = 0; i < 6; i++) {
            renderSides[i] = Util.possibleConnection(this, dirs[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
        return bb;
    }

    @Override
    public void updateEntity() {
    	super.updateEntity();
    	
        if (!worldObj.isRemote && !isAddedToEnergyNet) {
            Energy.postTileAttachEvent(this);
            this.isAddedToEnergyNet = true;
            Util.scheduleBlockUpdate(this, 20);
        }
    }

    @Override
    public void invalidate() {
        if (!worldObj.isRemote & isAddedToEnergyNet) {
            Energy.postTileDetachEvent(this);
            this.isAddedToEnergyNet = false;
        }

        super.invalidate();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        resistance = tagCompound.getFloat("resistance");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("resistance", resistance);
    }

    @Override
    public float getResistance() {
        return resistance;
    }

    @Override
    public int getInsulationBreakdownVoltage() {
        return 0;
    }

    @Override
    public void onInsulationBreakdown() {
    }
}
