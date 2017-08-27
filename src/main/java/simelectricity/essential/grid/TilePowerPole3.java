package simelectricity.essential.grid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.utils.math.Vec3f;

public abstract class TilePowerPole3 extends TilePowerPole {
    public int facing;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
		this.facing = nbt.getInteger("facing");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("facing", this.facing);
        return super.writeToNBT(nbt);
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(NBTTagCompound nbt) {
        super.prepareS2CPacketData(nbt);
        nbt.setInteger("facing", this.facing);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
		this.facing = nbt.getInteger("facing");
        super.onSyncDataFromServerArrived(nbt);
    }

    public static class Pole10KvType0 extends TilePowerPole3 {
        @Override
        @SideOnly(Side.CLIENT)
        protected PowerPoleRenderHelper createRenderHelper() {
            PowerPoleRenderHelper helper = new PowerPoleRenderHelper(this.world, this.pos, this.facing, 1, 3);
            helper.addInsulatorGroup(0, 0.5F, 0,
                    helper.createInsulator(0, 1.2F, 0, 0.55F, -0.74F),
                    helper.createInsulator(0, 1.2F, 0, 1.5F, 0),
                    helper.createInsulator(0, 1.2F, 0, 0.55F, 0.74F)
            );
            return helper;
        }
    }

    public static class Pole10KvType1 extends TilePowerPole3 {
        @Override
        @SideOnly(Side.CLIENT)
        protected PowerPoleRenderHelper createRenderHelper() {
            int rotation = facing;
            PowerPoleRenderHelper helper = new PowerPoleRenderHelper(Pole10KvType1.this.world, Pole10KvType1.this.pos, rotation, 2, 3) {
                @Override
                public void updateRenderData(BlockPos... neighborPosList) {
                    super.updateRenderData(neighborPosList);

                    if (this.connectionInfo.size() < 2)
                        return;

                    PowerPoleRenderHelper.ConnectionInfo[] connection1 = this.connectionInfo.getFirst();
                    PowerPoleRenderHelper.ConnectionInfo[] connection2 = connectionInfo.getLast();

                    Vec3f pos = new Vec3f(
                            0.5F + this.pos.getX(),
                            this.pos.getY() + 1.5F,
                            0.5F + this.pos.getZ()
                    );


					this.addExtraWire(connection1[1].fixedFrom, pos, -0.4F);
					this.addExtraWire(pos, connection2[1].fixedFrom, -0.4F);
                    if (PowerPoleRenderHelper.hasIntersection(
                            connection1[0].fixedFrom, connection2[0].fixedFrom,
                            connection1[2].fixedFrom, connection2[2].fixedFrom)) {
						this.addExtraWire(connection1[0].fixedFrom, connection2[2].fixedFrom, 0.8F);
						this.addExtraWire(connection1[2].fixedFrom, connection2[0].fixedFrom, 0.8F);
					} else {
						this.addExtraWire(connection1[0].fixedFrom, connection2[0].fixedFrom, 0.8F);
						this.addExtraWire(connection1[2].fixedFrom, connection2[2].fixedFrom, 0.8F);
                    }
                }
            };
            helper.addInsulatorGroup(-0.6F, 0.9F, 0,
                    helper.createInsulator(0.5F, 2, -0.05F, 0.1F, -0.74F),
                    helper.createInsulator(0.5F, 2, -0.05F, 0.9F, 0),
                    helper.createInsulator(0.5F, 2, -0.05F, 0.1F, 0.74F)
            );
            helper.addInsulatorGroup(0.6F, 0.9F, 0,
                    helper.createInsulator(0.5F, 2, 0.05F, 0.1F, -0.74F),
                    helper.createInsulator(0.5F, 2, 0.05F, 0.9F, 0),
                    helper.createInsulator(0.5F, 2, 0.05F, 0.1F, 0.74F)
            );
            return helper;
        }
    }

    public static class Pole415vType0 extends TilePowerPole3 {
        @Override
        @SideOnly(Side.CLIENT)
        protected PowerPoleRenderHelper createRenderHelper() {
            PowerPoleRenderHelper helper = new PowerPoleRenderHelper(this.world, this.pos, this.facing, 1, 4);
            helper.addInsulatorGroup(0, 0.55F, 0,
                    helper.createInsulator(0, 1.2F, 0, 0.3F, -0.9F),
                    helper.createInsulator(0, 1.2F, 0, 0.3F, -0.45F),
                    helper.createInsulator(0, 1.2F, 0, 0.3F, 0.45F),
                    helper.createInsulator(0, 1.2F, 0, 0.3F, 0.9F)
            );
            return helper;
        }
    }
}
