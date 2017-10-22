package simelectricity.essential.client.grid.pole;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import simelectricity.essential.client.grid.FastTESRPowerPole;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TilePoleBranch;
import simelectricity.essential.grid.TilePowerPole3;

@SideOnly(Side.CLIENT)
public class FastTESRPowerPole3<T extends TileEntity & ISEPowerPole> extends FastTESRPowerPole<T> {
	public final static FastTESRPowerPole3 instance = new FastTESRPowerPole3();
	private FastTESRPowerPole3() {}
	
	public static RawQuadGroup modelInsulator10kV = null;

	@Override
	protected void bake(T te, PowerPoleRenderHelper helper) {
		if (te instanceof TilePowerPole3.Pole10Kv.Type1) {
			renderInsulator(helper, modelInsulator10kV);
			
			if (helper.connectionInfo.size() == 2)
				modelInsulator10kV.clone().translateCoord(0.5F, 1F, 0.5F).bake(helper.quadBuffer);
			
		} else if (te instanceof TilePoleBranch.Type10kV) {
			renderInsulator(helper, modelInsulator10kV);
		}
		
		super.bake(te, helper);
	}
}
