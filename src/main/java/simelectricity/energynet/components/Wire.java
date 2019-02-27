package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simelectricity.api.components.ISEWire;
import simelectricity.api.node.ISESubComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public final class Wire extends Tile<ISEWire> implements ISESubComponent, ISEWire {
    private volatile double resistance;
    private volatile boolean hasShuntResistance;
    private volatile double shuntResistance;
    private volatile boolean[] hasBranchOnSide;        //Use canConnectOnSide() instead

    public Wire(ISEWire dataProvider, TileEntity te) {
        super(dataProvider, te);
    }

    @Override
    public synchronized void updateComponentParameters() {
        resistance = this.dataProvider.getResistance();
        hasShuntResistance = this.dataProvider.hasShuntResistance();
        shuntResistance = this.dataProvider.getShuntResistance();

        hasBranchOnSide = new boolean[6];
        int i = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            hasBranchOnSide[i] = this.dataProvider.hasBranchOnSide(dir);
            i++;
        }
    }

    @Override
    public boolean hasBranchOnSide(EnumFacing side) {
        if (hasBranchOnSide == null)
            return false;

        return hasBranchOnSide[side.ordinal()];
    }

    @Override
    public synchronized double getResistance() {
        return this.resistance;
    }

    @Override
    public synchronized boolean hasShuntResistance() {
        return this.hasShuntResistance;
    }

    @Override
    public synchronized double getShuntResistance() {
        return this.shuntResistance;
    }

    @Override
    public ISESubComponent getComplement() {
        return null;
    }
}
