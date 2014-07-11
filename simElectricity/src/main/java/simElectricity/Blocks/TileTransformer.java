package simElectricity.Blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.ITransformer;
import simElectricity.API.EnergyTile.ITransformerWinding;

public class TileTransformer extends TileEntity implements ITransformer{
	public static class Primary implements ITransformerWinding{
		private ITransformer core;
		
		public Primary(ITransformer _core){
			core=_core;
		}
		
		@Override
		public float getResistance() {return 10;}

		@Override
		public int getMaxPowerDissipation() {return 0;}

		@Override
		public void onOverloaded() {}

		@Override
		public float getRatio() {return 10;}

		@Override
		public boolean isPrimary() {return true;}

		@Override
		public ITransformer getCore() {return core;}
	}
	
	public static class Secondary extends Primary{
		public Secondary(ITransformer _core) {super(_core);}

		@Override
		public boolean isPrimary() {return false;}
	}
	
	
	public Primary primary = new Primary(this);
	public Secondary secondary = new Secondary(this);
	protected boolean isAddedToEnergyNet = false;
	
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote && !isAddedToEnergyNet) {
			Energy.postTileAttachEvent(this);
			this.isAddedToEnergyNet=true;
		}
	}
	
	@Override
	public void invalidate() {
		if (!worldObj.isRemote & isAddedToEnergyNet){	
			Energy.postTileDetachEvent(this);
			this.isAddedToEnergyNet=false;
		}
		
		super.invalidate();
	}
	
	@Override
	public ForgeDirection getInputSide() {return ForgeDirection.NORTH;}

	@Override
	public ForgeDirection getOutputSide() {return ForgeDirection.SOUTH;}

	@Override
	public ITransformerWinding getPrimary() {return primary;}

	@Override
	public ITransformerWinding getSecondary() {return secondary;}

}
