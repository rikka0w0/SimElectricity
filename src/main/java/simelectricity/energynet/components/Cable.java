package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.components.ISECableParameter;
import simelectricity.api.tile.ISECableTile;

public class Cable extends SEComponent.Tile<ISECableTile> implements ISECableParameter{
	//Properties, do not modify their value!
	public final boolean isGridInterConnectionPoint;
	private boolean[] canConnectOnSide;		//Use canConnectOnSide() instead
	public boolean isGridLinkEnabled;
	public int color;
	public double resistance;
	
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
	
		int i = 0;
		for (ForgeDirection dir: ForgeDirection.VALID_DIRECTIONS){
			this.canConnectOnSide[i] = dataProvider.canConnectOnSide(dir);
			i++;
		}
	}
	
	////////////
	///ISECableParameter
	///////////
	@Override
	public boolean canConnectOnSide(ForgeDirection direction){
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
}
