package simelectricity.essential.machines.tile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISETransformer;
import simelectricity.essential.common.SETwoPortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;

public class TileAdjustableTransformer extends SETwoPortMachine implements ISETransformer, IEnergyNetUpdateHandler, ISESocketProvider{
    //Input - primary, output - secondary
    public double ratio = 10, outputResistance = 1;
    
    public double vPri, vSec;
    
	/////////////////////////////////////////////////////////
	///TileEntity
	/////////////////////////////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        ratio = tagCompound.getDouble("ratio");
        outputResistance = tagCompound.getDouble("outputResistance");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("ratio", ratio);
        tagCompound.setDouble("outputResistance", outputResistance);
        
        return super.writeToNBT(tagCompound);
    }
    
	/////////////////////////////////////////////////////////
	///IEnergyNetUpdateHandler
	/////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
    	vPri = SEAPI.energyNetAgent.getVoltage(input);
    	vSec = SEAPI.energyNetAgent.getVoltage(input.getComplement());
    }
    
	/////////////////////////////////////////////////////////
	///ISETransformerData
	/////////////////////////////////////////////////////////
	@Override
	public double getRatio() {
		return ratio;
	}

	@Override
	public double getInternalResistance() {
		return outputResistance;
	}
	
    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
	@Override
	@SideOnly(Side.CLIENT)
	public int getSocketIconIndex(EnumFacing side) {
		if (side == inputSide)
			return 2;
		else if (side == outputSide)
			return 3;
		else 
			return -1;
	}
}
