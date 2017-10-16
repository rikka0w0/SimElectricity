package simelectricity.essential.client.grid.pole;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePowerPole3;

@SideOnly(Side.CLIENT)
public class FastTESRPowerPole3 extends FastTESRPowerPole<TilePowerPole3>{
	public final static FastTESRPowerPole3 instance = new FastTESRPowerPole3();
	private FastTESRPowerPole3() {}
	
	public static RawQuadGroup modelInsulator10kV = null;

	@Override
	protected void bake(TilePowerPole3 te, PowerPoleRenderHelper helper) {
		if (te instanceof TilePowerPole3.Pole10Kv.Type1)
			renderInsulator(helper, modelInsulator10kV);
		
		super.bake(te, helper);
	}
}
