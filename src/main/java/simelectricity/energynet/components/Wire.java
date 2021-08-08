package simelectricity.energynet.components;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import simelectricity.api.components.ISEWire;
import simelectricity.api.node.ISESubComponent;

public final class Wire extends CableBase<ISEWire> implements ISESubComponent<ISESubComponent<?>>, ISEWire {
    private volatile boolean[] hasBranchOnSide;        //Use canConnectOnSide() instead

    public Wire(ISEWire dataProvider, BlockEntity te) {
        super(dataProvider, te);
    }

    @Override
    public synchronized void updateComponentParameters() {
        super.updateComponentParameters();

        hasBranchOnSide = new boolean[6];
        int i = 0;
        for (Direction dir : Direction.values()) {
            hasBranchOnSide[i] = this.dataProvider.hasBranchOnSide(dir);
            i++;
        }
    }

    @Override
    public boolean hasBranchOnSide(Direction side) {
        if (side == null) {
            for (Direction facing: Direction.values())
                if (hasBranchOnSide[facing.ordinal()])
                    return true;
            return false;
        }

        return hasBranchOnSide[side.ordinal()];
    }
}
