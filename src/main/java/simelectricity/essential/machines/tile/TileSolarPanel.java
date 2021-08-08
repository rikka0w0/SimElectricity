package simelectricity.essential.machines.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import rikka.librikka.tileentity.ITickableBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.api.SEAPI;
import simelectricity.api.components.ISEVoltageSource;
import simelectricity.essential.common.semachine.SESinglePortMachine;

public class TileSolarPanel extends SESinglePortMachine<ISEVoltageSource> implements
		ISEVoltageSource, ITickableBlockEntity {
    public TileSolarPanel(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

	private static byte STATE_DAY;
    private static final byte STATE_NIGHT = 1;
    private static final byte STATE_CAVE = 2;
    //Component parameters
    public double internalVoltage = 230;
    public double resistance = 0.1;
    private byte state = -1;

    ///////////////////////////////////
    /// BlockEntity
    ///////////////////////////////////
    @Override
    public void tick() {
        if (this.level.isClientSide)
            return;

        //Server only
        if (!this.level.dimensionType().hasSkyLight() || !this.level.canSeeSkyFromBelowWater(this.worldPosition.above())) {
            this.detectAndSendChange(TileSolarPanel.STATE_CAVE);
            return;
        }

        if (this.level.isDay())
            this.detectAndSendChange(TileSolarPanel.STATE_DAY);
        else
            this.detectAndSendChange(TileSolarPanel.STATE_NIGHT);

    }

    void detectAndSendChange(byte state) {
        if (this.state != state) {
            this.state = state;

            if (state == TileSolarPanel.STATE_DAY) {
                internalVoltage = 22;
                resistance = 0.8;
            } else if (state == TileSolarPanel.STATE_NIGHT) {
                internalVoltage = 18;
                resistance = 10;
            } else {
                internalVoltage = 10;
                resistance = 100;
            }

            SEAPI.energyNetAgent.updateTileParameter(this);
        }
    }

    ///////////////////////////////////
    /// ISESidedFacing
    ///////////////////////////////////
    @Override
    public boolean canWrenchBeUsed(Direction newFunctionalSide) {
        return newFunctionalSide != Direction.UP;
    }

    ///////////////////////////////////
    /// ISEVoltageSource
    ///////////////////////////////////
    @Override
    public double getOutputVoltage() {
        return this.internalVoltage;
    }

    @Override
    public double getResistance() {
        return this.resistance;
    }

    @Override
    public boolean isOn() {
        return true;
    }

    ///////////////////////////////////
    /// ISESocketProvider
    ///////////////////////////////////
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSocketIconIndex(Direction side) {
        return side == this.functionalSide ? 1 : -1;
    }
}
