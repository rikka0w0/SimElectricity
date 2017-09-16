package simelectricity.energynet;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;

public abstract class TileEvent extends EnergyEventBase {
    protected final TileEntity te;

    private TileEvent(int priority, TileEntity te) {
        super(priority);
        this.te = te;
    }

    public static class Attach extends TileEvent {
        public Attach(TileEntity te) {
            super(4, te);
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider) {
            if (this.te instanceof ISETile || this.te instanceof ISECableTile) {
                this.needUpdate = true;
                this.changedStructure = true;
                dataProvider.registerTile(this.te);
                dataProvider.updateTileParam(this.te);
            }

            if (this.te instanceof ISEGridTile) {
                dataProvider.onGridTilePresent(this.te);
            }
        }
    }

    public static class Detach extends TileEvent {
        public Detach(TileEntity te) {
            super(6, te);
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider) {
            if (this.te instanceof ISETile || this.te instanceof ISECableTile) {
                this.needUpdate = true;
                this.changedStructure = true;
                dataProvider.unregisterTile(this.te);
                dataProvider.updateTileParam(this.te);
            }

            if (this.te instanceof ISEGridTile) {
                dataProvider.onGridTileInvalidate(this.te);
            }
        }
    }

    public static class ParamChanged extends TileEvent {
        public ParamChanged(TileEntity te) {
            super(5, te);
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider) {
            this.needUpdate = true;
            dataProvider.updateTileParam(this.te);
        }
    }

    public static class ConnectionChanged extends TileEvent {
        public ConnectionChanged(TileEntity te) {
            super(5, te);
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider) {
            this.needUpdate = true;
            this.changedStructure = true;
            dataProvider.unregisterTile(this.te);
            dataProvider.registerTile(this.te);
            dataProvider.updateTileParam(this.te);
        }
    }
}
