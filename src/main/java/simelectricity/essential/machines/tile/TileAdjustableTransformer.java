package simelectricity.essential.machines.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.api.ISEEnergyNetUpdateHandler;
import simelectricity.api.components.ISETransformer;
import simelectricity.essential.common.semachine.ISESocketProvider;
import simelectricity.essential.common.semachine.SETwoPortMachine;
import simelectricity.essential.machines.gui.ContainerAdjustableTransformer;

public class TileAdjustableTransformer extends SETwoPortMachine<ISETransformer> implements ISETransformer, ISEEnergyNetUpdateHandler, ISESocketProvider, INamedContainerProvider {
    //Input - primary, output - secondary
    public double ratio = 10, outputResistance = 1;

    public double vPri, vSec;

    /////////////////////////////////////////////////////////
    ///TileEntity
    /////////////////////////////////////////////////////////
    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);

		this.ratio = tagCompound.getDouble("ratio");
		this.outputResistance = tagCompound.getDouble("outputResistance");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound.putDouble("ratio", this.ratio);
        tagCompound.putDouble("outputResistance", this.outputResistance);

        return super.write(tagCompound);
    }

    /////////////////////////////////////////////////////////
    ///ISEEnergyNetUpdateHandler
    /////////////////////////////////////////////////////////
    @Override
    public void onEnergyNetUpdate() {
		this.vPri = this.input.getVoltage();
		this.vSec = this.input.getComplement().getVoltage();
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
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        if (side == this.inputSide)
            return 2;
        else if (side == this.outputSide)
            return 3;
        else
            return -1;
    }

    ///////////////////////////////////
    /// INamedContainerProvider
    ///////////////////////////////////
	@Override
	public Container createMenu(int windowID, PlayerInventory inv, PlayerEntity player) {
		return new ContainerAdjustableTransformer(this, windowID);
	}
}
