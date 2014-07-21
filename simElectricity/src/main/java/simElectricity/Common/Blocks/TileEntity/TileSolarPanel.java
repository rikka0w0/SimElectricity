package simElectricity.Common.Blocks.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Common.TileStandardGenerator;
import simElectricity.API.Energy;

public class TileSolarPanel extends TileStandardGenerator {
    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote)
            return;

        //Server only

        if (worldObj.isDaytime()) {
            if (worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord))
                checkAndSendChange(18, 0.8F);
            else
                checkAndSendChange(0, Float.MAX_VALUE);
        } else {
            checkAndSendChange(12, 20);
        }
    }

    void checkAndSendChange(float voltage, float resistance) {
        if (voltage != outputVoltage | resistance != outputResistance) {
            outputVoltage = voltage;
            outputResistance = resistance;
            Energy.postTileChangeEvent(this);
        }
    }

    @Override
    public boolean canSetFunctionalSide(ForgeDirection newFunctionalSide) {
        return newFunctionalSide != ForgeDirection.UP;
    }
}
