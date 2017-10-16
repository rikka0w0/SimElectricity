package simelectricity.essential.client.grid.pole;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePowerPole2;

@SideOnly(Side.CLIENT)
public class FastTESRPowerPole2 extends FastTESRPowerPole<TilePowerPole2>{
	public final static FastTESRPowerPole2 instance = new FastTESRPowerPole2();
	private FastTESRPowerPole2() {}
	
	public static RawQuadGroup modelInsulator = null;
	
	@Override
	protected void bake(TilePowerPole2 te, PowerPoleRenderHelper helper) {
		List<BakedQuad> quads = helper.quadBuffer;
		
		if (te.isType0())
			renderInsulator(helper, modelInsulator);
		
		super.bake(te, helper);
	}
}
