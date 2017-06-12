package simelectricity.Templates.TileEntity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.components.ISERegulator;
import simelectricity.api.SEAPI;
import simelectricity.api.node.ISESubComponent;
import simelectricity.api.tile.ISETile;
import simelectricity.Templates.Common.TileEntityTwoPort;
import simelectricity.Templates.Utils.IGuiSyncHandler;

public class TileSolarInverter extends TileEntityTwoPort implements ISETile, ISERegulator, IGuiSyncHandler{
    public ISESubComponent input = SEAPI.energyNetAgent.newComponent(this, this);

    public double Vreg = 24;
    public double Ro = 0.001F;

	/////////////////////////////////////////////////////////
	///TileEntity
	/////////////////////////////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        Vreg = tagCompound.getDouble("Vreg");
        Ro = tagCompound.getDouble("Ro");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setDouble("Vreg", Vreg);
        tagCompound.setDouble("Ro", Ro);
    }


	/////////////////////////////////////////////////////////
	///ISETile
	/////////////////////////////////////////////////////////
	@Override
	public ISESubComponent getComponent(ForgeDirection side) {
		if (side == inputSide)
			return input;
		if (side == outputSide)
			return input.getComplement();
		return null;
	}
	
	/////////////////////////////////////////////////////////
	///ISERegulatorData
	/////////////////////////////////////////////////////////
	@Override
	public double getRegulatedVoltage() {return Vreg;}

	@Override
	public double getOutputResistance() {return Ro;}
	
	@Override
	public double getDMax() {return 1;}

	@Override
	public double getRc() {return 1;}

	@Override
	public double getGain() {return 1e5;}

	@Override
	public double getRs() {return 1e6;}

	@Override
	public double getRDummyLoad() {return 1e6;}

	/////////////////////////////////////////////////////////
	///IGuiSyncHandler
	/////////////////////////////////////////////////////////
	@Override
	public void onGuiEvent(byte eventID, Object[] data) {
		if (eventID == IGuiSyncHandler.EVENT_FACING_CHANGE){
			byte button = (Byte) data[0];
			ForgeDirection selectedDirection = (ForgeDirection) data[1];
			
		    if (button == 0) {        //Left key
		        if (outputSide == selectedDirection)
		            outputSide = inputSide;
		        inputSide = selectedDirection;
		    } else if (button == 1) { //Right key
		        if (inputSide == selectedDirection)
		            inputSide = outputSide;
		        outputSide = selectedDirection;
	        }

            SEAPI.energyNetAgent.updateTileConnection(this);
			this.markTileEntityForS2CSync();
			this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, null);
			return;
		}
		
		//EVENT_BUTTON_CLICK
		boolean isCtrlDown = (Boolean) data[0];
		byte button = (Byte) data[1];
		
		double Ro = this.Ro;
		double Vreg = this.Vreg;
		
        switch (button) {
        case 0:
            if (isCtrlDown)
                Ro -= 1;
            else
                Ro -= 0.1;
            break;
        case 1:
            if (isCtrlDown)
                Ro -= 0.001;
            else
                Ro -= 0.01;
            break;
        case 2:
            if (isCtrlDown)
                Ro += 0.001;
            else
                Ro += 0.01;
            break;
        case 3:
            if (isCtrlDown)
                Ro += 1;
            else
                Ro += 0.1;
            break;

        case 4:
            if (isCtrlDown)
                Vreg -= 100;
            else
                Vreg -= 10;
            break;
        case 5:
            if (isCtrlDown)
                Vreg -= 0.1;
            else
                Vreg -= 1;
            break;
        case 6:
            if (isCtrlDown)
                Vreg += 0.1;
            else
                Vreg += 1;
            break;
        case 7:
            if (isCtrlDown)
                Vreg += 100;
            else
                Vreg += 10;
            break;

        default:
	    }
	
	    if (Ro < 0.001)
	        Ro = 0.001F;
	    if (Ro > 100)
	        Ro = 100;
    
        if (Vreg < 1)
            Vreg = 1;
        if (Vreg > 50)
            Vreg = 50;
	    
	    this.Ro = Ro;
	    this.Vreg = Vreg;
	    
	    SEAPI.energyNetAgent.updateTileParameter(this);
	}
}
