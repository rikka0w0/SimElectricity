package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.tileentity.IGuiProviderTile;
import simelectricity.api.IEnergyNetUpdateHandler;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISETransformer;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerAdjustableTransformer;

public class TileAdjustableTransformer extends SETwoPortMachine implements ISETransformer, IEnergyNetUpdateHandler, ISESocketProvider, IGuiProviderTile {
    //Input - primary, output - secondary
    public double ratio = 10, outputResistance = 1;

    public double vPri, vSec;

    /////////////////////////////////////////////////////////
    ///TileEntity
    /////////////////////////////////////////////////////////
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

		this.ratio = tagCompound.getDouble("ratio");
		this.outputResistance = tagCompound.getDouble("outputResistance");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("ratio", this.ratio);
        tagCompound.setDouble("outputResistance", this.outputResistance);

        return super.writeToNBT(tagCompound);
    }

    /////////////////////////////////////////////////////////
    ///IEnergyNetUpdateHandler
    /////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
		this.vPri = SEAPI.energyNetAgent.getVoltage(this.input);
		this.vSec = SEAPI.energyNetAgent.getVoltage(this.input.getComplement());
    }

    /////////////////////////////////////////////////////////
    ///ISETransformerData
    /////////////////////////////////////////////////////////
    @Override
    public double getRatio() {
        return this.ratio;
    }

    @Override
    public double getInternalResistance() {
        return this.outputResistance;
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @SideOnly(Side.CLIENT)
    public int getSocketIconIndex(EnumFacing side) {
        if (side == this.inputSide)
            return 2;
        else if (side == this.outputSide)
            return 3;
        else
            return -1;
    }
    
    ///////////////////////////////////
    /// IGuiProviderTile
    ///////////////////////////////////
	@Override
	public Container getContainer(EntityPlayer player, EnumFacing side) {
		return new ContainerAdjustableTransformer(this);
	}
}
