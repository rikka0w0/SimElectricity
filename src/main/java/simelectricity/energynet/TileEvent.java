package simelectricity.energynet;

import net.minecraft.tileentity.TileEntity;

public class TileEvent implements IEnergyNetEvent{
	protected TileEntity te;
	
	public TileEvent(TileEntity te){
		this.te = te;
	}
	
	public static class Attach extends TileEvent {
		public Attach(TileEntity te) {
			super(te);
		}
	}
	
	public static class Detach extends TileEvent {
		public Detach(TileEntity te) {
			super(te);
		}
	}
	
	public static class ParamChanged extends TileEvent {
		public ParamChanged(TileEntity te) {
			super(te);
		}
	}
	
	public static class ConnectionChanged extends TileEvent {
		public ConnectionChanged(TileEntity te) {
			super(te);
		}
	}
	
	public static class GridTilePresent extends TileEvent {
		public GridTilePresent(TileEntity te) {
			super(te);
		}
	}

	public static class GridTileInvalidate extends TileEvent {
		public GridTileInvalidate(TileEntity te) {
			super(te);
		}
	}
}
