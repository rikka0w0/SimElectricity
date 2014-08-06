package simElectricity.Common.Blocks.TileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.IEnergyNetUpdateHandler;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.EnergyTile.ITransformer;

public class TileRegulator extends TileEntitySE implements ITransformer, IEnergyNetUpdateHandler{
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

	@Override
	public void onEnergyNetUpdate() {
		float error = Energy.getVoltage(secondary) - outputVoltage;
		if (Math.abs(error) > 1 && Energy.getVoltage(primary) > 10){
			if (error > 0) {
				ratio -=0.004;
			}else{
				ratio +=0.05;
			}
			Energy.postTileChangeEvent(this);
		}
	}
	
}
