package simelectricity.energynet;

import net.minecraft.tileentity.TileEntity;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.api.tile.ISETile;

public abstract class TileEvent extends EnergyEventBase{
	protected final TileEntity te;
	
	private TileEvent(int priority, TileEntity te){
		super(priority);
		this.te = te;
	}

	public static class Attach extends TileEvent {
		public Attach(TileEntity te) {
			super(3, te);
		}

		@Override
		public void process(EnergyNetDataProvider dataProvider) {
			if (te instanceof ISETile || te instanceof ISECableTile) {
				needUpdate = true;
				changedStructure = true;
				dataProvider.registerTile(te);
				dataProvider.updateTileParam(te);
			}
			
			if (te instanceof ISEGridTile) {
				dataProvider.onGridTilePresent(te);
			}
		}
	}
	
	public static class Detach extends TileEvent {
		public Detach(TileEntity te) {
			super(3, te);
		}

		@Override
		public void process(EnergyNetDataProvider dataProvider) {
			if (te instanceof ISETile || te instanceof ISECableTile) {
				needUpdate = true;
				changedStructure = true;
				dataProvider.unregisterTile(te);
				dataProvider.updateTileParam(te);
			}
			
			if (te instanceof ISEGridTile) {
				dataProvider.onGridTileInvalidate(te);
			}
		}
	}
	
	public static class ParamChanged extends TileEvent {
		public ParamChanged(TileEntity te) {
			super(4, te);
		}

		@Override
		public void process(EnergyNetDataProvider dataProvider) {
			needUpdate = true;
			dataProvider.updateTileParam(te);
		}
	}
	
	public static class ConnectionChanged extends TileEvent {
		public ConnectionChanged(TileEntity te) {
			super(4, te);
		}

		@Override
		public void process(EnergyNetDataProvider dataProvider) {
			needUpdate = true;
			changedStructure = true;
			dataProvider.unregisterTile(te);
			dataProvider.registerTile(te);
			dataProvider.updateTileParam(te);
		}
	}
}
