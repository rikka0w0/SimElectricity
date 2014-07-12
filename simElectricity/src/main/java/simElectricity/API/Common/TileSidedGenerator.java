package simElectricity.API.Common;

public abstract class TileSidedGenerator extends TileStandardSEMachine {
    public float outputVoltage = 0;
    public float outputResistance = Float.MAX_VALUE;

    @Override
    public float getResistance() {
        return outputResistance;
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
        return outputVoltage;
    }

    @Override
    public float getMaxSafeVoltage() {
        return 0;
    }

    @Override
    public void onOverVoltage() {
    }
}
