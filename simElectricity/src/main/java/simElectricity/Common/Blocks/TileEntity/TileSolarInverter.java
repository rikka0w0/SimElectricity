package simElectricity.Common.Blocks.TileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.ITransformer;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.INetworkEventHandler;

import java.util.List;

public class TileSolarInverter extends TileEntitySE implements ITransformer, IEnergyNetUpdateHandler, INetworkEventHandler {
    private static float p = 0.04F, i = 0.0000001F; //PI controller parameters
    public EnumFacing inputSide = EnumFacing.NORTH, outputSide = EnumFacing.SOUTH;
    public Primary primary = new ITransformer.Primary(this);
    public Secondary secondary = new ITransformer.Secondary(this);
    public float ratio = 10, outputResistance = 0.1F;
    public float outputVoltage = 230;
    private float aError = 0;                       //PI accumulated error

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        ratio = tagCompound.getFloat("ratio");
        outputResistance = tagCompound.getFloat("outputResistance");
        inputSide = EnumFacing.getFront(tagCompound.getByte("inputSide"));
        outputSide = EnumFacing.getFront(tagCompound.getByte("outputSide"));
        outputVoltage = tagCompound.getFloat("outputVoltage");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("ratio", ratio);
        tagCompound.setFloat("outputResistance", outputResistance);
        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
        tagCompound.setFloat("outputVoltage", outputVoltage);
    }

    @Override
    public boolean attachToEnergyNet() {
        return true;
    }

    @Override
    public double getResistance() {
        return outputResistance;
    }

    @Override
    public double getRatio() {
        return ratio;
    }

    @Override
    public EnumFacing getPrimarySide() {
        return inputSide;
    }

    @Override
    public EnumFacing getSecondarySide() {
        return outputSide;
    }

    @Override
    public ITransformerWinding getPrimary() {
        return primary;
    }

    @Override
    public ITransformerWinding getSecondary() {
        return secondary;
    }

    @Override
    public void onOverVoltage() {
        worldObj.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), (float) (4F + Energy.getVoltage(primary) / 60), true);
    }

    @Override
    public void onEnergyNetUpdate() {
        checkVoltage(Energy.getVoltage(primary), 60);

        double vo = Energy.getVoltage(secondary);
        double error = outputVoltage - vo;
        boolean needUpdate = false;
        if (Math.abs(error) > 1 && Energy.getVoltage(primary) > 8) {
            ratio += p * error;
            ratio += i * aError;
            aError += error;
            needUpdate = true;
        } else {
            aError = 0;
        }

        if (Energy.getVoltage(primary) == 0) {
            ratio = 1;
        }

        if (ratio > 30) {
            needUpdate = false;
            ratio = 30;
        }

        if (ratio < 0) {
            needUpdate = false;
            ratio = 0.00001F;
        }

        if (needUpdate)
            Energy.postTileChangeEvent(this);
    }

    @Override
    public void onFieldUpdate(String[] fields, Object[] values) {
        //Handling on server side
        if (!worldObj.isRemote) {
            for (String s : fields) {
                if (s.contains("inputSide") || s.contains("outputSide")) {
                    Energy.postTileRejoinEvent(this);
                    worldObj.notifyBlockOfStateChange(pos, blockType);
                } else if (s.contains("outputResistance") || s.contains("outputVoltage")) {
                    Energy.postTileChangeEvent(this);
                }
            }

        }
    }

    @Override
    public void addNetworkFields(List fields) {
    }

}
