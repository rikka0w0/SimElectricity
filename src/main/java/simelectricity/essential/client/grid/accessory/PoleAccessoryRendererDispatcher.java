package simelectricity.essential.client.grid.accessory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.grid.BlockEntityCableJoint;
import simelectricity.essential.grid.BlockEntityPoleBranch;
import simelectricity.essential.grid.BlockEntityPoleConcrete;
import simelectricity.essential.grid.transformer.BlockEntityDistributionTransformer;
import simelectricity.essential.client.grid.ISEPowerPole;
import simelectricity.essential.api.ISEPoleAccessory;

public class PoleAccessoryRendererDispatcher {
	private final static Map<Pair<Class<? extends ISEPowerPole>, Class<? extends ISEPoleAccessory>>,ISEAccessoryRenderer> registered = new HashMap<>();
	
	static {
		register(BlockEntityPoleConcrete.Pole10Kv.Type0.class, BlockEntityCableJoint.Type10kV.class, AR10kVType0CableJoint.instance);
		register(BlockEntityPoleConcrete.Pole10Kv.Type1.class, BlockEntityCableJoint.Type10kV.class, AR10kVType1CableJoint.instance);
		register(BlockEntityDistributionTransformer.Pole10kV.class, BlockEntityCableJoint.Type10kV.class, AR10kVType0CableJoint.instance);
		
		register(BlockEntityPoleConcrete.Pole10Kv.Type0.class, BlockEntityPoleBranch.Type10kV.class, AR10kVType0Branch.instance);
		register(BlockEntityPoleConcrete.Pole10Kv.Type1.class, BlockEntityPoleBranch.Type10kV.class, AR10kVType1Branch.instance);
		register(BlockEntityPoleConcrete.Pole415vType0.class, BlockEntityPoleBranch.Type415V.class, AR415VBranch.instance);
		
		register(BlockEntityPoleConcrete.Pole415vType0.class, BlockEntityCableJoint.Type415V.class, AR415VType0CableJoint.instance);
		register(BlockEntityDistributionTransformer.Pole415V.class, BlockEntityCableJoint.Type415V.class, AR415VType0CableJoint.instance);
	}
	
	public static void register(Class<? extends ISEPowerPole> poleClass, Class<? extends ISEPoleAccessory> accessoryClass, ISEAccessoryRenderer renderer) {
		registered.put(Pair.of(poleClass, accessoryClass), renderer);
	}
	
	public static <T extends ISEPowerPole> void render(BlockGetter world, T pole, @Nullable BlockPos accessoryPos) {
		if (accessoryPos == null)
			return;
		
		PowerPoleRenderHelper helper = pole.getRenderHelper();
		if (helper == null)
			return;

		BlockEntity neighborTile = world.getBlockEntity(accessoryPos);
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
