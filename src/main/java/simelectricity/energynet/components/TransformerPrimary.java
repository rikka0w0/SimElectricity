package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISETransformer;
import simelectricity.api.node.ISEPairedComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class TransformerPrimary extends Tile<ISETransformer> implements ISEPairedComponent<TransformerSecondary>, ISETransformer {
    protected volatile double rsec, ratio;
    protected volatile TransformerSecondary secondary;

    public TransformerPrimary(ISETransformer dataProvider, TileEntity te) {
        super(dataProvider, te);
        this.secondary = new TransformerSecondary(this, te);
    }

    @Override
    public synchronized TransformerSecondary getComplement() {
        return this.secondary;
    }

    @Override
    public synchronized void updateComponentParameters() {
        rsec = this.dataProvider.getInternalResistance();
        ratio = this.dataProvider.getRatio();
    }

    @Override
    public synchronized double getRatio() {
        return this.ratio;
    }

    @Override
    public synchronized double getInternalResistance() {
        return this.rsec;
    }
}
