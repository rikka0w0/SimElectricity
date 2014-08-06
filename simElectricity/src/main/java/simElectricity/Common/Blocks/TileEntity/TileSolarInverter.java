package simElectricity.Common.Blocks.TileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.ITransformer;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.Common.SEUtils;

public class TileSolarInverter extends TileEntitySE implements ITransformer, IEnergyNetUpdateHandler{
    public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH;

    public Primary primary = new ITransformer.Primary(this);
    public Secondary secondary = new ITransformer.Secondary(this);

    public float ratio = 10, outputResistance = 0.1F;
    public float outputVoltage = 230;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        ratio = tagCompound.getFloat("ratio");
        outputResistance = tagCompound.getFloat("outputResistance");
        inputSide = ForgeDirection.getOrientation(tagCompound.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation(tagCompound.getByte("outputSide"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("ratio", ratio);
        tagCompound.setFloat("outputResistance", outputResistance);
        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
    }

	@Override
	public boolean attachToEnergyNet() {
		return true;
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
        return inputSide;
    }

    @Override
    public ForgeDirection getSecondarySide() {
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


    float aError = 0;
	@Override
	public void onEnergyNetUpdate() {
		double p = 0.04, i = 0.0000001;
		double vo = Energy.getVoltage(secondary);
		double error = outputVoltage - vo;
		boolean needUpdate = false;
		if (Math.abs(error) > 1 && Energy.getVoltage(primary) > 8){
			ratio += p * error;
			ratio += i * aError;
			aError += error;
			needUpdate = true;
		} else {
			aError = 0;
		}

		if (Energy.getVoltage(primary) == 0){
			ratio = 1;
		}
		
		if (ratio >30){
			needUpdate = false;
			ratio =30;
		}

		if (ratio <0){
			needUpdate = false;
			ratio = 0.00001F;
		}
		
		if (needUpdate)
			Energy.postTileChangeEvent(this);

		
		SEUtils.logInfo(String.valueOf(vo)+":"+String.valueOf(ratio));
	}

}
