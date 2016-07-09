package simElectricity.Common.Blocks.TileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.ISERegulatorInput;
import simElectricity.API.EnergyTile.ISERegulatorOutput;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.EnergyTile.ISETile;
import simElectricity.API.EnergyTile.ISETransformerPrimary;
import simElectricity.API.EnergyTile.ISETransformerSecondary;
import simElectricity.API.INetworkEventHandler;
import simElectricity.Common.Blocks.TileEntity.TileAdjustableTransformer.TSecondary;

import java.util.List;

public class TileSolarInverter extends TileEntitySE implements ISETile, INetworkEventHandler{
	public class RPrimary implements ISERegulatorInput{
		private ISERegulatorOutput _sec;
		private TileSolarInverter _par;
		
		public RPrimary(TileSolarInverter parent){
			_sec = new RSecondary(this);
			_par = parent;
		}
		
		@Override
		public ISERegulatorOutput getOutput() {
			return _sec;
		}

		@Override
		public double getMinimumInputVoltage() {
			return 8;
		}

		@Override
		public double getRegulatedVoltage() {
			return _par.Vreg;
		}

		@Override
		public double getOutputResistance() {
			return _par.Ro;
		}

		@Override
		public double getMaximumInputVoltage() {
			return 30;
		}

		@Override
		public double getOutputRipple() {
			return 1;
		}
	}
	
	public class RSecondary implements ISERegulatorOutput{
		private ISERegulatorInput _pri;
		
		public RSecondary(ISERegulatorInput primary){
			_pri = primary;
		}
		
		@Override
		public ISERegulatorInput getInput() {
			return _pri;
		}
		
	}
	
	public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH;

    public ISERegulatorInput input = new RPrimary(this);

    public float Vreg = 230;
    public float Ro = 0.001F;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        Vreg = tagCompound.getFloat("Vreg");
        Ro = tagCompound.getFloat("Ro");
        inputSide = ForgeDirection.getOrientation(tagCompound.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation(tagCompound.getByte("outputSide"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("Vreg", Vreg);
        tagCompound.setFloat("Ro", Ro);
        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
    }

	@Override
	public boolean attachToEnergyNet() {
		return true;
	}


	@Override
	public void onFieldUpdate(String[] fields, Object[] values) {
		//Handling on server side
		if (!worldObj.isRemote){
			for (String s:fields){
		        if (s.contains("inputSide") || s.contains("outputSide")) {
		            Energy.postTileRejoinEvent(this);
		            worldObj.notifyBlockChange(xCoord, yCoord, zCoord,
		            		worldObj.getBlock(xCoord, yCoord, zCoord));
		        } else if (s.contains("Ro") || s.contains("Vreg")) {
		            Energy.postTileChangeEvent(this);
		        }
			}

		}
	}

	@Override
	public void addNetworkFields(List fields) {
	}

	@Override
	public int getNumberOfComponents() {
		return 2;
	}

	@Override
	public ForgeDirection[] getValidDirections() {
		return new ForgeDirection[]{inputSide, outputSide};
	}

	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		if (side == inputSide)
			return input;
		if (side == outputSide)
			return input.getOutput();
		return null;
	}
	
	public ForgeDirection getPrimarySide(){
		return inputSide;
	}
	
	public ForgeDirection getSecondarySide(){
		return outputSide;
	}
}
