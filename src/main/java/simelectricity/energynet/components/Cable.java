package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simelectricity.api.components.ISECable;
import simelectricity.api.tile.ISECableTile;
import simelectricity.energynet.components.SEComponent.Tile;

public class Cable extends Tile<ISECable> implements ISECable {
    //Properties, do not modify their value!
    public final boolean isGridInterConnectionPoint;
    public boolean isGridLinkEnabled;
    public int color;
    public double resistance;
    public boolean hasShuntResistance;
    public double shuntResistance;
    //Simulation & Optimization
    public GridNode connectedGridNode;
    private final boolean[] canConnectOnSide;        //Use canConnectOnSide() instead

    public Cable(ISECableTile dataProvider, TileEntity te, boolean isGridInterConnectionPoint) {
        super(dataProvider, te);
        this.isGridInterConnectionPoint = isGridInterConnectionPoint;
        canConnectOnSide = new boolean[6];

        connectedGridNode = null;
    }

    @Override
    public void updateComponentParameters() {
        color = this.dataProvider.getColor();
        resistance = this.dataProvider.getResistance();
        isGridLinkEnabled = this.dataProvider.isGridLinkEnabled();
        hasShuntResistance = this.dataProvider.hasShuntResistance();
        shuntResistance = this.dataProvider.getShuntResistance();

        int i = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            canConnectOnSide[i] = this.dataProvider.canConnectOnSide(dir);
            i++;
        }
    }

    /////////////////////////
    ///ISECableParameter
    /////////////////////////
    @Override
    public boolean canConnectOnSide(EnumFacing direction) {
        return canConnectOnSide[direction.ordinal()];
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public double getResistance() {
        return this.resistance;
    }

    @Override
    public boolean isGridLinkEnabled() {
        return this.isGridLinkEnabled;
    }

    @Override
    public boolean hasShuntResistance() {
        return this.hasShuntResistance;
    }

    @Override
    public double getShuntResistance() {
        return this.shuntResistance;
    }
}
