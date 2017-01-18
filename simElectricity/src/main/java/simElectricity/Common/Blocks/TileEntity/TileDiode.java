package simElectricity.Common.Blocks.TileEntity;

import simElectricity.API.Common.TileEntitySE;
import simElectricity.API.EnergyTile.ISEDiodeInput;
import simElectricity.API.EnergyTile.ISEDiodeOutput;
import simElectricity.API.EnergyTile.ISESubComponent;
import simElectricity.API.EnergyTile.ISETile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileDiode extends TileEntitySE implements ISETile{
	public class DiodeInput implements ISEDiodeInput{
		private TileDiode _par;
		private ISEDiodeOutput _sec;
		
		public DiodeInput(TileDiode parent){
			_par = parent;
			_sec = new DiodeOutput(this);
		}
		
		@Override
		public double getForwardResistance() {
			return 0.1;
		}

		@Override
		public ISEDiodeOutput getOutput() {
			return _sec;
		}		
	}
	
	public class DiodeOutput implements ISEDiodeOutput{
		private ISEDiodeInput _pri;
		public DiodeOutput(ISEDiodeInput input){
			_pri = input;
		}

		@Override
		public ISEDiodeInput getInput() {
			return _pri;
		}
	}
	
	public DiodeInput input = new DiodeInput(this);
	public ForgeDirection inputSide = ForgeDirection.NORTH, outputSide = ForgeDirection.SOUTH;

	
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        
        inputSide = ForgeDirection.getOrientation(tagCompound.getByte("inputSide"));
        outputSide = ForgeDirection.getOrientation(tagCompound.getByte("outputSide"));
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setByte("inputSide", (byte) inputSide.ordinal());
        tagCompound.setByte("outputSide", (byte) outputSide.ordinal());
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
		else if (side == outputSide)
			return input.getOutput();
		return null;
	}

	@Override
	public boolean attachToEnergyNet() {
		return true;
	}
}
