package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simelectricity.api.components.ISEWire;
import simelectricity.api.node.ISESubComponent;

public final class Wire extends CableBase<ISEWire> implements ISESubComponent, ISEWire {
    private volatile boolean[] hasBranchOnSide;        //Use canConnectOnSide() instead

    public Wire(ISEWire dataProvider, TileEntity te) {
        super(dataProvider, te);
    }

    @Override
    public synchronized void updateComponentParameters() {
        super.updateComponentParameters();

        hasBranchOnSide = new boolean[6];
        int i = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            hasBranchOnSide[i] = this.dataProvider.hasBranchOnSide(dir);
            i++;
        }
    }

    @Override
    public boolean hasBranchOnSide(EnumFacing side) {
        if (side == null) {
            for (EnumFacing facing: EnumFacing.VALUES)
                if (hasBranchOnSide[facing.ordinal()])
                    return true;
            return false;
        }

        return hasBranchOnSide[side.ordinal()];
    }

    @Override
    public ISESubComponent getComplement() {
        return null;
    }
}
