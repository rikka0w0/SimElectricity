package simelectricity.essential.client.grid.accessory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TilePoleBranch;
import simelectricity.essential.grid.TilePoleConcrete;
//import simelectricity.essential.grid.transformer.TileDistributionTransformer;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.api.ISEPoleAccessory;

public class PoleAccessoryRendererDispatcher {
	private final static Map<Pair<Class<? extends ISEPowerPole>, Class<? extends ISEPoleAccessory>>,ISEAccessoryRenderer> registered = new HashMap();
	
	static {
		register(TilePoleConcrete.Pole10Kv.Type0.class, TileCableJoint.Type10kV.class, AR10kVType0CableJoint.instance);
		register(TilePoleConcrete.Pole10Kv.Type1.class, TileCableJoint.Type10kV.class, AR10kVType1CableJoint.instance);
//		register(TileDistributionTransformer.Pole10kV.class, TileCableJoint.Type10kV.class, AR10kVType0CableJoint.instance);
		
		register(TilePoleConcrete.Pole10Kv.Type0.class, TilePoleBranch.Type10kV.class, AR10kVType0Branch.instance);
		register(TilePoleConcrete.Pole10Kv.Type1.class, TilePoleBranch.Type10kV.class, AR10kVType1Branch.instance);
		register(TilePoleConcrete.Pole415vType0.class, TilePoleBranch.Type415V.class, AR415VBranch.instance);
		
		register(TilePoleConcrete.Pole415vType0.class, TileCableJoint.Type415V.class, AR415VType0CableJoint.instance);
//		register(TileDistributionTransformer.Pole415V.class, TileCableJoint.Type415V.class, AR415VType0CableJoint.instance);
	}
	
	public static void register(Class<? extends ISEPowerPole> poleClass, Class<? extends ISEPoleAccessory> accessoryClass, ISEAccessoryRenderer renderer) {
		registered.put(Pair.of(poleClass, accessoryClass), renderer);
	}
	
	public static <T extends ISEPowerPole> void render(IBlockReader world, T pole, @Nullable BlockPos accessoryPos) {
		if (accessoryPos == null)
			return;
		
		PowerPoleRenderHelper helper = pole.getRenderHelper();
		if (helper == null)
			return;
				
		TileEntity neighborTile = world.getTileEntity(accessoryPos);
		if (neighborTile instanceof ISEPoleAccessory && neighborTile instanceof ISEPowerPole) {
			ISEPoleAccessory accessory = (ISEPoleAccessory) neighborTile;
			ISEAccessoryRenderer renderer = registered.get(Pair.of(pole.getClass(), accessory.getClass()));
			
			if (renderer != null) {
				PowerPoleRenderHelper neighborHelper = ((ISEPowerPole)neighborTile).getRenderHelper();
				renderer.renderConnection(helper, neighborHelper);
			}
		}
	}
}
