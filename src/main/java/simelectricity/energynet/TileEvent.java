package simelectricity.energynet;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;

public abstract class TileEvent extends EnergyEventBase {
    protected final TileEntity te;

    private TileEvent(TileEntity te) {
        this.te = te;
    }

    public static class Attach extends TileEvent {
        public Attach(TileEntity te) {
            super(te);
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider, int pass) {
        	if (this.te instanceof ISETile || this.te instanceof ISECableTile) {
            	if (pass == TADD) {
            		dataProvider.addTile(te);
            	} else if (pass == TPARAMCHANGE) {
            		dataProvider.updateTileParam(te);
            	} else if (pass == TCONCHANGE) {
                    dataProvider.updateTileConnection(this.te);
            	}
        	}
        	
            if (this.te instanceof ISEGridTile && pass == TADD) {
                dataProvider.onGridTilePresent(this.te);
            }
        }

		@Override
		public boolean changedStructure() {
			return this.te instanceof ISETile || this.te instanceof ISECableTile;
		}

		@Override
		public boolean needUpdate() {
			return this.te instanceof ISETile || this.te instanceof ISECableTile;
		}
    }

    public static class Detach extends TileEvent {
        public Detach(TileEntity te) {
            super(te);
        }

        @Override
        public void process(EnergyNetDataProvider dataProvider, int pass) {
        	if (pass == TADD || pass == TDEL) {
        		if (this.te instanceof ISETile || this.te instanceof ISECableTile) {
                    dataProvider.removeTile(this.te);
            	}
        		

                if (this.te instanceof ISEGridTile && pass == TDEL) {
                    dataProvider.onGridTileInvalidate(this.te);
                }
            }

        }

		@Override
		public boolean changedStructure() {
			return this.te instanceof ISETile || this.te instanceof ISECableTile;
		}

		@Override
		public boolean needUpdate() {
			return this.te instanceof ISETile || this.te instanceof ISECableTile;
		}
    }

    public static class ParamChanged extends TileEvent {
        public ParamChanged(TileEntity te) {
            super(te);
        }

		@Override
		public void process(EnergyNetDataProvider dataProvider, int pass) {
			if (pass == TPARAMCHANGE)
				dataProvider.updateTileParam(this.te);
		}

		@Override
		public boolean changedStructure() {
			return false;
		}

		@Override
		public boolean needUpdate() {
			return true;
		}
    }

    public static class ConnectionChanged extends TileEvent {
        public ConnectionChanged(TileEntity te) {
            super(te);
        }

		@Override
		public void process(EnergyNetDataProvider dataProvider, int pass) {
			if (pass == TPARAMCHANGE)
				dataProvider.updateTileParam(this.te);
			if (pass == TCONCHANGE)
				dataProvider.updateTileConnection(this.te);
		}
        
		@Override
		public boolean changedStructure() {
			return true;
		}

		@Override
		public boolean needUpdate() {
			return true;
		}
    }
}
