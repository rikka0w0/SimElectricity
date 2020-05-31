package simelectricity.essential.client.grid.pole;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.PowerPoleTER;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePoleBranch;
import simelectricity.essential.grid.TilePoleConcrete;

@OnlyIn(Dist.CLIENT)
public class ConcretePoleTER<T extends TileEntity & ISEPowerPole> extends PowerPoleTER<T> {
	public ConcretePoleTER(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	public static RawQuadGroup modelInsulator10kV = null;
	public static RawQuadGroup modelInsulator415V = null;

	@Override
	protected void bake(T te, PowerPoleRenderHelper helper) {
		if (te instanceof TilePoleConcrete.Pole10Kv.Type1) {
			renderInsulator(helper, modelInsulator10kV);

			if (helper.connectionList.size() == 2)
				modelInsulator10kV.clone().translateCoord(0.5F, 1F, 0.5F).bake(helper.quadBuffer);
			
		} else if (te instanceof TilePoleBranch.Type10kV) {
			renderInsulator(helper, modelInsulator10kV);
		} else if (te instanceof TilePoleBranch.Type415V) {
			renderInsulator(helper, modelInsulator415V);
		}
		
		super.bake(te, helper);
	}
}
