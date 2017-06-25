package simelectricity.essential.machines.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.components.ISETransformer;
import simelectricity.essential.common.SETwoPortMachine;
import simelectricity.essential.machines.render.ISESocketProvider;

public class TileAdjustableTransformer extends SETwoPortMachine implements ISETransformer, ISESocketProvider{
    //Input - primary, output - secondary
    public double ratio = 10, outputResistance = 1;
    
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
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setDouble("ratio", ratio);
        tagCompound.setDouble("outputResistance", outputResistance);
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
	public int getSocketIconIndex(ForgeDirection side) {
		if (side == inputSide)
			return 2;
		else if (side == outputSide)
			return 3;
		else 
			return -1;
	}
}
