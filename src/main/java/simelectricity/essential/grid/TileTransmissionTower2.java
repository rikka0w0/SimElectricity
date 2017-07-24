package simelectricity.essential.grid;

import simelectricity.essential.grid.render.TransmissionTowerRenderHelper;

public class TileTransmissionTower2 extends TileTransmissionTower {
	@Override
	protected int getTypeFromMeta(){
		return getBlockMetadata() >> 3;
	}
	
	@Override
	public int getRotation() {
		return (getBlockMetadata() & 3) * 2;
	}
	
	@Override
	protected TransmissionTowerRenderHelper createRenderHelper(){
		return new TransmissionTowerRenderHelper(this,2,
				getTypeFromMeta() == 0
				?
				new double[]{-0.25, 0.125, -4.5, -0.25, 0.125, 0, -0.25, 0.125, 4.5,
							0.25, 0.125, -4.5, 0.25, 0.125, 0, 0.25, 0.125, 4.5}
				:
				new double[]{0, 0.125-1.95, -4.5, 0, 0.125-1.95, 0, 0, 0.125-1.95, 4.5}
				);
	}
}
