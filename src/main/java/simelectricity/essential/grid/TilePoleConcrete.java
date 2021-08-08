package simelectricity.essential.grid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;
import rikka.librikka.math.Vec3f;
import simelectricity.api.node.ISEGridNode;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;
import simelectricity.essential.common.ISEFacing8;

public abstract class TilePoleConcrete extends TilePowerPoleBase implements ISEFacing8 {
	public TilePoleConcrete(BlockPos pos, BlockState blockState) {
		super(pos, blockState);
	}

	protected BlockPos accessory;

	protected abstract boolean acceptAccessory(BlockEntity accessory);

	@Override
	@OnlyIn(Dist.CLIENT)
    public BlockPos getAccessoryPos() {
		return accessory;
	}

    @Override
    public boolean canConnect(@Nullable BlockPos to) {
    	if (to == null)
    		return this.neighbor1 == null || this.neighbor2 == null || this.accessory == null;

    	BlockEntity te = level.getBlockEntity(to);
    	if (acceptAccessory(te)) {
    		return this.accessory == null;
    	} else {
    		return this.neighbor1 == null || this.neighbor2 == null;
    	}
    }

    @Override
    public void onGridNeighborUpdated() {
        this.neighbor1 = null;
        this.neighbor2 = null;
        this.accessory = null;

        for (ISEGridNode node: this.gridNode.getNeighborList()) {
        	BlockEntity tile = level.getBlockEntity(node.getPos());
        	if (acceptAccessory(tile)) {
        		this.accessory = node.getPos();
        	} else if (neighbor1 == null){
        		neighbor1 = node.getPos();
        	} else {
        		neighbor2 = node.getPos();
        	}
        }

        markTileEntityForS2CSync();
    }

    /////////////////////////////////////////////////////////
    ///Sync
    /////////////////////////////////////////////////////////
    @Override
    public void prepareS2CPacketData(CompoundTag nbt) {
        super.prepareS2CPacketData(nbt);
        Utils.saveToNbt(nbt, "accessory", this.accessory);
    }

    @Override
	@OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
		this.accessory = Utils.posFromNbt(nbt, "accessory");
        super.onSyncDataFromServerArrived(nbt);
        this.updateRenderInfo(this.accessory);
    }

    public static abstract class Pole10Kv extends TilePoleConcrete {
		public Pole10Kv(BlockPos pos, BlockState blockState) {
			super(pos, blockState);
		}

		@Override
		protected boolean acceptAccessory(BlockEntity accessory)  {
			if (accessory instanceof TileCableJoint.Type10kV)
				return true;

			if (accessory instanceof TilePoleBranch.Type10kV) {
				return accessory.getBlockPos().above().equals(worldPosition);
			}

			return false;
		}

        public static class Type0 extends Pole10Kv {
            public Type0(BlockPos pos, BlockState blockState) {
				super(pos, blockState);
			}

			@Override
            @Nonnull
            @OnlyIn(Dist.CLIENT)
            protected PowerPoleRenderHelper createRenderHelper() {
                PowerPoleRenderHelper helper = new PowerPoleRenderHelper(worldPosition, getRotation(), 1, 3);
                helper.addInsulatorGroup(0, 0.5F, 0,
                        helper.createInsulator(0, 1.2F, -0.74F, 0.55F, 0),
                        helper.createInsulator(0, 1.2F, 0, 1.5F, 0),
                        helper.createInsulator(0, 1.2F, 0.74F, 0.55F, 0)
                );
                return helper;
            }
        }

        public static class Type1 extends Pole10Kv {
            public Type1(BlockPos pos, BlockState blockState) {
				super(pos, blockState);
			}

			@Override
            @Nonnull
            @OnlyIn(Dist.CLIENT)
            protected PowerPoleRenderHelper createRenderHelper() {
                PowerPoleRenderHelper helper = new PowerPoleRenderHelper(this.worldPosition, getRotation(), 2, 3) {
                    @Override
                    public void onUpdate() {
                    	if (this.connectionList.size() == 2) {
                            PowerPoleRenderHelper.ConnectionInfo[] connection1 = connectionList.getFirst();
                            PowerPoleRenderHelper.ConnectionInfo[] connection2 = connectionList.getLast();

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
        						this.addExtraWire(connection1[0].fixedFrom, connection2[2].fixedFrom, 0.5F);
        						this.addExtraWire(connection1[2].fixedFrom, connection2[0].fixedFrom, 0.5F);
        					} else {
        						this.addExtraWire(connection1[0].fixedFrom, connection2[0].fixedFrom, 0.5F);
        						this.addExtraWire(connection1[2].fixedFrom, connection2[2].fixedFrom, 0.5F);
                            }
                        }
                    }
                };
                helper.addInsulatorGroup(0, 0.9F, -0.6F,
                        helper.createInsulator(0.5F, 1.2F, -0.74F, 0.1F, -0.05F),
                        helper.createInsulator(0.5F, 1.2F, 0, 0.9F, -0.05F),
                        helper.createInsulator(0.5F, 1.2F, 0.74F, 0.1F, -0.05F)
                );
                helper.addInsulatorGroup(0, 0.9F, 0.6F,
                        helper.createInsulator(0.5F, 1.2F, -0.74F, 0.1F, 0.05F),
                        helper.createInsulator(0.5F, 1.2F, 0, 0.9F, 0.05F),
                        helper.createInsulator(0.5F, 1.2F, 0.74F, 0.1F, 0.05F)
                );
                return helper;
            }
        }

    }

    public static class Pole415vType0 extends TilePoleConcrete {
		public Pole415vType0(BlockPos pos, BlockState blockState) {
			super(pos, blockState);
		}

		@Override
		protected boolean acceptAccessory(BlockEntity accessory)  {
			if (accessory instanceof TileCableJoint.Type415V)
				return true;

			if (accessory instanceof TilePoleBranch.Type415V) {
				return accessory.getBlockPos().above().equals(worldPosition);
			}

			return false;
		}

        @Override
        @Nonnull
        @OnlyIn(Dist.CLIENT)
        protected PowerPoleRenderHelper createRenderHelper() {
            PowerPoleRenderHelper helper = new PowerPoleRenderHelper(this.worldPosition, getRotation(), 1, 4);

            helper.addInsulatorGroup(0, 0.55F, 0,
                    helper.createInsulator(0, 1.2F, -0.9F, 0.3F, 0),
                    helper.createInsulator(0, 1.2F, -0.45F, 0.3F, 0),
                    helper.createInsulator(0, 1.2F, 0.45F, 0.3F, 0),
                    helper.createInsulator(0, 1.2F, 0.9F, 0.3F, 0)
            );
            return helper;
        }
    }
}
