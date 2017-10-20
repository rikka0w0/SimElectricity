package simelectricity.essential.client.grid.accessory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.TileCableJoint;
import simelectricity.essential.grid.TilePowerPole3;
import simelectricity.essential.grid.transformer.TileDistributionTransformer;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.api.ISEPoleAccessory;

public class PoleAccessoryRendererDispatcher {
	private final static Map<Pair<Class<? extends ISEPowerPole>, Class<? extends ISEPoleAccessory>>,ISEAccessoryRenderer> registered = new HashMap();
	
	static {
		register(TilePowerPole3.Pole10Kv.Type0.class, TileCableJoint.Type10kV.class, AR10kVType0CableJoint.instance);
		register(TilePowerPole3.Pole10Kv.Type1.class, TileCableJoint.Type10kV.class, AR10kVType1CableJoint.instance);
		register(TileDistributionTransformer.Pole10kV.class, TileCableJoint.Type10kV.class, AR10kVType0CableJoint.instance);
		
		register(TilePowerPole3.Pole415vType0.class, TileCableJoint.Type415V.class, AR415VType0CableJoint.instance);
		register(TileDistributionTransformer.Pole415V.class, TileCableJoint.Type415V.class, AR415VType0CableJoint.instance);
	}
	
	public static void register(Class<? extends ISEPowerPole> poleClass, Class<? extends ISEPoleAccessory> accessoryClass, ISEAccessoryRenderer renderer) {
		registered.put(Pair.of(poleClass, accessoryClass), renderer);
	}
	
	public static <T extends TileEntity&ISEPowerPole> void render(T pole, @Nullable BlockPos accessoryPos) {
		if (accessoryPos == null)
			return;
		
		PowerPoleRenderHelper helper = pole.getRenderHelper();
		if (helper == null)
			return;
				
		TileEntity neighborTile = pole.getWorld().getTileEntity(accessoryPos);
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
