package simElectricity.Blocks;

import simElectricity.API.Common.TileStandardSEMachine;

public class TileVoltageMeter extends TileStandardSEMachine {
    public float voltage = 0;

    @Override
    public float getResistance() {
        return 1e6F;
    }

    @Override
    public float getOutputVoltage() {
        return 0;
    }
    
    @Override
    public int getInventorySize() {
        return 0;
    }
}
