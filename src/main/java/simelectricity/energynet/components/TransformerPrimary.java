package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.components.ISETransformer;
import simelectricity.api.node.ISESubComponent;
import simelectricity.energynet.components.SEComponent.Tile;

public class TransformerPrimary extends Tile<ISETransformer> implements ISESubComponent, ISETransformer {
    public double rsec, ratio;
    public TransformerSecondary secondary;

    public TransformerPrimary(ISETransformer dataProvider, TileEntity te) {
        super(dataProvider, te);
        this.secondary = new TransformerSecondary(this, te);
    }

    @Override
    public ISESubComponent getComplement() {
        return this.secondary;
    }

    @Override
    public void updateComponentParameters() {
        rsec = this.dataProvider.getInternalResistance();
        ratio = this.dataProvider.getRatio();
    }

    @Override
    public double getRatio() {
        return this.ratio;
    }

    @Override
    public double getInternalResistance() {
        return this.rsec;
    }
}
