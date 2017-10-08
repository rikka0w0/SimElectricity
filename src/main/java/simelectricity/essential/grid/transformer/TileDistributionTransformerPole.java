package simelectricity.essential.grid.transformer;

import simelectricity.essential.common.SEMultiBlockEnergyTile;

public class TileDistributionTransformerPole extends SEMultiBlockEnergyTile{

	@Override
	public void onLoad() {}
	
	@Override
	public void onStructureCreated() {
		System.out.println("onStructureCreated");
	}

	@Override
	public void onStructureRemoved() {
		System.out.println("onStructureRemoved");
		
	}

	@Override
	protected void onStructureCreating() {
		System.out.println("onStructureCreating");
	}
}
