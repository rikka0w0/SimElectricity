package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import simelectricity.api.components.ISECable;
import simelectricity.api.tile.ISECableTile;

public class Cable extends SEComponent.Tile<ISECable> implements ISECable{
	//Properties, do not modify their value!
	public final boolean isGridInterConnectionPoint;
	private boolean[] canConnectOnSide;		//Use canConnectOnSide() instead
	
	public boolean isGridLinkEnabled;
	public int color;
	public double resistance;
	public boolean hasShuntResistance;
	public double shuntResistance;
	
	//Simulation & Optimization
	public GridNode connectedGridNode;
	
	public Cable(ISECableTile dataProvider, TileEntity te, boolean isGridInterConnectionPoint){
		super(dataProvider, te);
		this.isGridInterConnectionPoint = isGridInterConnectionPoint;
		this.canConnectOnSide = new boolean[6];
		
		this.connectedGridNode = null;
	}

	@Override
	public void updateComponentParameters() {
		this.color = dataProvider.getColor();
		this.resistance = dataProvider.getResistance();
		this.isGridLinkEnabled = dataProvider.isGridLinkEnabled();
		this.hasShuntResistance = dataProvider.hasShuntResistance();
		this.shuntResistance = dataProvider.getShuntResistance();
	
		int i = 0;
		for (EnumFacing dir: EnumFacing.VALUES){
			this.canConnectOnSide[i] = dataProvider.canConnectOnSide(dir);
			i++;
		}
	}
	
	/////////////////////////
	///ISECableParameter
	/////////////////////////
	@Override
	public boolean canConnectOnSide(EnumFacing direction){
		return this.canConnectOnSide[direction.ordinal()];
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public double getResistance() {
		return resistance;
	}

	@Override
	public boolean isGridLinkEnabled() {
		return isGridLinkEnabled;
	}

	@Override
	public boolean hasShuntResistance() {
		return hasShuntResistance;
	}

	@Override
	public double getShuntResistance() {
		return shuntResistance;
	}
}
