package simelectricity.essential.grid;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import simelectricity.essential.client.grid.TransmissionLineRenderHelper;
import simelectricity.essential.utils.Vec3f;

public class TileTransmissionTower2 extends TileTransmissionTower {
	@Override
	protected int getTypeFromMeta(){
		return getBlockMetadata() >> 3;
	}
	
	@Override
	protected TransmissionLineRenderHelper createRenderHelper(){
		TransmissionLineRenderHelper helper;
		int rotation = (getBlockMetadata() & 3) * 2;
		
		if (getTypeFromMeta() == 0) {
			helper = new TransmissionLineRenderHelper(world, pos, rotation, 2, 3) {
				@Override
				public void updateRenderData(BlockPos... neighborPosList) {
					super.updateRenderData(neighborPosList);
					
					if (connectionInfo.size() < 2)
						return;
					
		    		ConnectionInfo[] connection1 = this.connectionInfo.getFirst();
		    		ConnectionInfo[] connection2 = this.connectionInfo.getLast();
		    		
		    		Vec3f pos = new Vec3f(
		    		3.95F * MathHelper.sin(this.rotation/180F*TransmissionLineRenderHelper.pi) + 0.5F + this.pos.getX(),
		    		this.pos.getY() + 23 -18,
		    		3.95F * MathHelper.cos(this.rotation/180F*TransmissionLineRenderHelper.pi) + 0.5F + this.pos.getZ()
		    		);
		    		
		    		extraWires.add(Pair.of(connection1[1].fixedFrom, connection2[1].fixedFrom));
		    		if (TransmissionLineRenderHelper.hasIntersection(
		    				connection1[0].fixedFrom, connection2[0].fixedFrom,
		    				connection1[2].fixedFrom, connection2[2].fixedFrom)) {
		    			extraWires.add(Pair.of(connection1[0].fixedFrom, connection2[2].fixedFrom));
		    			extraWires.add(Pair.of(connection1[2].fixedFrom, connection2[0].fixedFrom));
		    		}else {
		    			extraWires.add(Pair.of(connection1[0].fixedFrom, connection2[0].fixedFrom));
		    			extraWires.add(Pair.of(connection1[2].fixedFrom, connection2[2].fixedFrom));
		    		}
				}
			};
			helper.addInsulatorGroup(-0.25F, 0.125F, 0,
					helper.createInsulator(2, -0.25F, 0.125F, -4.5F),
					helper.createInsulator(2, -0.25F, 0.125F, 0),
					helper.createInsulator(2, -0.25F, 0.125F, 4.5F)
					);
			helper.addInsulatorGroup(0.25F, 0.125F, 0,
					helper.createInsulator(2, 0.25F, 0.125F, -4.5F),
					helper.createInsulator(2, 0.25F, 0.125F, 0),
					helper.createInsulator(2, 0.25F, 0.125F, 4.5F)
					);
		}else {
			helper = new TransmissionLineRenderHelper(world, pos, rotation, 1, 3);
			helper.addInsulatorGroup(0, 0.125F-1.95F, 0F,
					helper.createInsulator(0, 0, 0.125F-1.95F, -4.5F),
					helper.createInsulator(0, 0, 0.125F-1.95F, 0F),
					helper.createInsulator(0, 0, 0.125F-1.95F, 4.5F)
					);
		}

		return helper;
	}
}
