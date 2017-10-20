package simelectricity.essential.client.grid.accessory;

import simelectricity.essential.client.grid.PowerPoleRenderHelper;

public class AR415VType0CableJoint implements ISEAccessoryRenderer {
	public final static ISEAccessoryRenderer instance = new AR415VType0CableJoint();
	
	private AR415VType0CableJoint() {}

	@Override
	public void renderConnection(PowerPoleRenderHelper helper, PowerPoleRenderHelper neighbor) {
		System.out.println("Dummy Renderer!");
	}
}
