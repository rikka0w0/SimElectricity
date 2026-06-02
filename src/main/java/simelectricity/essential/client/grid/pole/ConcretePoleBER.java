package simelectricity.essential.client.grid.pole;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.PowerPoleBER;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.BlockEntityPoleBranch;
import simelectricity.essential.grid.BlockEntityPoleConcrete;

@OnlyIn(Dist.CLIENT)
public class ConcretePoleBER<T extends BlockEntity & ISEPowerPole> extends PowerPoleBER<T> {
	public ConcretePoleBER(BlockEntityRendererProvider.Context context) {
		super(context);
	}
	
	public static RawQuadGroup modelInsulator10kV = null;
	public static RawQuadGroup modelInsulator415V = null;

	@Override
	protected void bake(T te, PowerPoleRenderHelper helper) {
		if (te instanceof BlockEntityPoleConcrete.Pole10Kv.Type1) {
			renderInsulator(helper, modelInsulator10kV);

			if (helper.connectionList.size() == 2)
				modelInsulator10kV.clone().translateCoord(0.5F, 1F, 0.5F).bake(helper.quadBuffer);
			
		} else if (te instanceof BlockEntityPoleBranch.Type10kV) {
			renderInsulator(helper, modelInsulator10kV);
		} else if (te instanceof BlockEntityPoleBranch.Type415V) {
			renderInsulator(helper, modelInsulator415V);
		}
		
		super.bake(te, helper);
	}
}
