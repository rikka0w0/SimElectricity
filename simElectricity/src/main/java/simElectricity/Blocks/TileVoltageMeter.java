package simElectricity.Blocks;

import simElectricity.API.Common.TileStandardSEMachine;

public class TileVoltageMeter extends TileStandardSEMachine {
    public float voltage = 0;

    @Override
    public float getResistance() {
        return Float.MAX_VALUE;
    }

    @Override
    public void onOverloaded() {
    }

    @Override
    public int getMaxPowerDissipation() {
        return 0;
    }

    @Override
    public float getOutputVoltage() {
        return 0;
    }

    @Override
    public float getMaxSafeVoltage() {
        return 0;
    }

    @Override
    public void onOverVoltage() {
    }

    @Override
    public int getInventorySize() {
        return 0;
    }
}
