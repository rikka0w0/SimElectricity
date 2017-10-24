package simelectricity.essential.client.grid.pole;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePowerPole;

@SideOnly(Side.CLIENT)
public class FastTESRPowerPoleTop extends FastTESRPowerPole<TilePowerPole>{
	public final static FastTESRPowerPoleTop instance = new FastTESRPowerPoleTop();
	private FastTESRPowerPoleTop() {}
	
	public final static List<BakedQuad>[] insulator35Kv = new List[8];
	public final static List<BakedQuad>[] bakedModelType0 = new List[8];
	public final static List<BakedQuad>[] bakedModelType1 = new List[8];
	public static RawQuadGroup modelInsulator = null;
	
	@Override
	protected void bake(TilePowerPole te, PowerPoleRenderHelper helper) {
		List<BakedQuad> quads = helper.quadBuffer;
		
		if (te.isType0()) {
			quads.addAll(bakedModelType0[helper.orientation]);
			
	        if (helper.connectionInfo.size() > 1)
            quads.addAll(this.insulator35Kv[helper.orientation]);
			
	        renderInsulator(helper, modelInsulator);
		} else {
			quads.addAll(bakedModelType1[helper.orientation]);
		}
		
        super.bake(te, helper);
	}
}
