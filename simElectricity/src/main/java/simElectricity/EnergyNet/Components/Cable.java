package simelectricity.energynet.components;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.tile.ISECableTile;

public class Cable extends SEComponent.Tile<ISECableTile>{
	//Properties, do not modify their value!
	public boolean isGridInterConnectionPoint;
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
	
	public boolean canConnectOnSide(ForgeDirection direction){
		return this.canConnectOnSide[direction.ordinal()];
	}
}
