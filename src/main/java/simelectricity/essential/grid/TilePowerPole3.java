package simelectricity.essential.grid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.utils.math.Vec3f;

public abstract class TilePowerPole3 extends TilePowerPole{
	public int facing;
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		facing = nbt.getInteger("facing");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt.setInteger("facing", facing);
		return super.writeToNBT(nbt);
	}
	
	public static class Pole10KvType0 extends TilePowerPole3{
		@Override
		@SideOnly(Side.CLIENT)
		protected PowerPoleRenderHelper createRenderHelper(){
			PowerPoleRenderHelper helper = new PowerPoleRenderHelper(world, pos, facing, 1, 3);
			helper.addInsulatorGroup(0, 0.5F, 0,
					helper.createInsulator(0, 1.2F, 0, 0.6F, -0.75F),
					helper.createInsulator(0, 1.2F, 0, 1.5F, 0),
					helper.createInsulator(0, 1.2F, 0, 0.6F, 0.75F)
					);
			return helper;
		}
	}
	
	public static class Pole10KvType1 extends TilePowerPole3{
		@Override
		@SideOnly(Side.CLIENT)
		protected PowerPoleRenderHelper createRenderHelper(){
			int rotation = this.facing;
			PowerPoleRenderHelper helper = new PowerPoleRenderHelper(world, pos, rotation, 2, 3) {
				@Override
				public void updateRenderData(BlockPos... neighborPosList) {
					super.updateRenderData(neighborPosList);
					
					if (connectionInfo.size() < 2)
						return;
					
		    		ConnectionInfo[] connection1 = this.connectionInfo.getFirst();
		    		ConnectionInfo[] connection2 = this.connectionInfo.getLast();
		    		
		    		Vec3f pos = new Vec3f(
		    		0.5F + this.pos.getX(),
		    		this.pos.getY() + 1.5F,
		    		0.5F + this.pos.getZ()
		    		);
		    		
		    		
		    		addExtraWire(connection1[1].fixedFrom, pos, -0.4F);
		    		addExtraWire(pos, connection2[1].fixedFrom, -0.4F);
		    		if (PowerPoleRenderHelper.hasIntersection(
		    				connection1[0].fixedFrom, connection2[0].fixedFrom,
		    				connection1[2].fixedFrom, connection2[2].fixedFrom)) {
		    			addExtraWire(connection1[0].fixedFrom, connection2[2].fixedFrom, 0.8F);
		    			addExtraWire(connection1[2].fixedFrom, connection2[0].fixedFrom, 0.8F);;
		    		}else {
		    			addExtraWire(connection1[0].fixedFrom, connection2[0].fixedFrom, 0.8F);
		    			addExtraWire(connection1[2].fixedFrom, connection2[2].fixedFrom, 0.8F);
		    		}
				}
			};
			helper.addInsulatorGroup(-0.6F, 0.9F, 0,
					helper.createInsulator(0.5F, 2, -0.05F, 0.1F, -0.75F),
					helper.createInsulator(0.5F, 2, -0.05F, 0.9F, 0),
					helper.createInsulator(0.5F, 2, -0.05F, 0.1F, 0.75F)
					);
			helper.addInsulatorGroup(0.6F, 0.9F, 0,
					helper.createInsulator(0.5F, 2, 0.05F, 0.1F, -0.75F),
					helper.createInsulator(0.5F, 2, 0.05F, 0.9F, 0),
					helper.createInsulator(0.5F, 2, 0.05F, 0.1F, 0.75F)
					);
			return helper;
		}
	}
	

}
