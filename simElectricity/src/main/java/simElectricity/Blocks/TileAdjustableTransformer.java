package simElectricity.Blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.ITransformer;
import simElectricity.API.ISyncPacketHandler;
import simElectricity.API.Util;

public class TileAdjustableTransformer extends TileEntity implements ITransformer, ISyncPacketHandler {
    public Primary primary = new ITransformer.Primary(this);
    public Secondary secondary = new ITransformer.Secondary(this);
    protected boolean isAddedToEnergyNet = false;

    public ForgeDirection primarySide = ForgeDirection.NORTH, secondarySide = ForgeDirection.SOUTH;
    public float ratio = 10, outputResistance = 1;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote && !isAddedToEnergyNet) {
            Energy.postTileAttachEvent(this);
            this.isAddedToEnergyNet = true;
            Util.scheduleBlockUpdate(this);
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

        ratio = tagCompound.getFloat("ratio");
        outputResistance = tagCompound.getFloat("outputResistance");
        primarySide = Util.byte2Direction(tagCompound.getByte("primarySide"));
        secondarySide = Util.byte2Direction(tagCompound.getByte("secondarySide"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("ratio", ratio);
        tagCompound.setFloat("outputResistance", outputResistance);
        tagCompound.setByte("primarySide", Util.direction2Byte(primarySide));
        tagCompound.setByte("secondarySide", Util.direction2Byte(secondarySide));
    }

    @Override
    public void onClient2ServerUpdate(String field, Object value, short type) {
        if (field.contains("primarySide") || field.contains("secondarySide")) {
            Energy.postTileRejoinEvent(this);
            Util.scheduleBlockUpdate(this);
        } else if (field.contains("outputResistance") || field.contains("ratio")) {
            Energy.postTileChangeEvent(this);
        }
    }

    @Override
    public void onServer2ClientUpdate(String field, Object value, short type) {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public float getResistance() {
        return outputResistance;
    }

    @Override
    public float getRatio() {
        return ratio;
    }

    @Override
    public ForgeDirection getPrimarySide() {
        return primarySide;
    }

    @Override
    public ForgeDirection getSecondarySide() {
        return secondarySide;
    }

    @Override
    public ITransformerWinding getPrimary() {
        return primary;
    }

    @Override
    public ITransformerWinding getSecondary() {
        return secondary;
    }


}
