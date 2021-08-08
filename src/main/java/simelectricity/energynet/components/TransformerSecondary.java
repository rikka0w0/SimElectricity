package simelectricity.energynet.components;

import net.minecraft.world.level.block.entity.BlockEntity;
import simelectricity.api.components.ISETransformer;
import simelectricity.api.node.ISEPairedComponent;

public class TransformerSecondary extends SEComponent implements ISEPairedComponent<TransformerPrimary>, ISETransformer {
    private volatile TransformerPrimary primary;

    public TransformerSecondary(TransformerPrimary primary, BlockEntity te) {
        this.primary = primary;
        this.te = te;
    }

    @Override
    public TransformerPrimary getComplement() {
        return this.primary;
    }

    @Override
    public double getRatio() {
        return this.primary.ratio;
    }

    @Override
    public double getInternalResistance() {
        return this.primary.rsec;
    }
}
