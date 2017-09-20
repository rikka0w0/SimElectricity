package simelectricity.essential.grid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.Utils;
import rikka.librikka.math.Vec3f;
import simelectricity.api.node.ISEGridNode;
import simelectricity.essential.client.grid.PowerPoleRenderHelper;

public abstract class TilePowerPole3 extends TilePowerPoleBase {
	protected BlockPos accessory;
	public int facing;

    @Override
    @SideOnly(Side.CLIENT)
    protected boolean scheduleBlockRenderUpdateWhenChange() {
        return this instanceof Pole10Kv.Type1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateRenderInfo() {
        this.getRenderHelper().updateRenderData(this.neighbor1, this.neighbor2);
        if (this.scheduleBlockRenderUpdateWhenChange())
            markForRenderUpdate();
    }
    
    @Override
    public boolean canConnect(@Nullable BlockPos to) {
    	if (to == null)
    		return this.neighbor1 == null || this.neighbor2 == null || this.accessory == null;
    	
    	TileEntity te = world.getTileEntity(to);
    	if (te instanceof TileCableJoint) {
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
        	TileEntity tile = world.getTileEntity(node.getPos());
        	if (tile instanceof TilePoleAccessory) {
        		this.accessory = node.getPos();
        	} else if (neighbor1 == null){
        		neighbor1 = node.getPos();
        	} else {
        		neighbor2 = node.getPos();
        	}
        }
        
        markTileEntityForS2CSync();
    }
    
    //////////////////////////////
    /////TileEntity
    //////////////////////////////
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
        Utils.saveToNbt(nbt, "accessory", this.accessory);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
		this.facing = nbt.getInteger("facing");
		this.accessory = Utils.posFromNbt(nbt, "accessory");
        super.onSyncDataFromServerArrived(nbt);
        this.updateRenderInfo(this.accessory);
    }

    public static abstract class Pole10Kv extends TilePowerPole3 {
        public static class Type0 extends Pole10Kv {
            @Override
            @Nonnull
            @SideOnly(Side.CLIENT)
            protected PowerPoleRenderHelper createRenderHelper() {
                PowerPoleRenderHelper helper = new PowerPoleRenderHelper(world, pos, facing, 1, 3) {
                	@Override
                	public void onUpdate() {
            			PowerPoleRenderHelper helper = fromPos(accessory);
            			if (helper != null) {
            				Vec3f to0 = helper.groups[0].insulators[0].realPos;
            				Vec3f to1 = helper.groups[0].insulators[1].realPos;
            				Vec3f to2 = helper.groups[0].insulators[2].realPos;
            				
            				Vec3f from0 = this.groups[0].insulators[0].realPos;
            				Vec3f from1 = this.groups[0].insulators[1].realPos;
            				Vec3f from2 = this.groups[0].insulators[2].realPos;
            				
            				float tension = -0.2F;
            				this.addExtraWire(from1, to1, tension);
            				if (PowerPoleRenderHelper.hasIntersection(from0, to0, from2, to2)) {
            					this.addExtraWire(from0, to2, tension);
            					this.addExtraWire(from2, to0, tension);
            				} else {
            					this.addExtraWire(from0, to0, tension);
            					this.addExtraWire(from2, to2, tension);
            				}
            			}
                	}
                };
                helper.addInsulatorGroup(0, 0.5F, 0,
                        helper.createInsulator(0, 1.2F, 0, 0.55F, -0.74F),
                        helper.createInsulator(0, 1.2F, 0, 1.5F, 0),
                        helper.createInsulator(0, 1.2F, 0, 0.55F, 0.74F)
                );
                return helper;
            }
        }
        
        public static class Type1 extends Pole10Kv {
            @Override
            @Nonnull
            @SideOnly(Side.CLIENT)
            protected PowerPoleRenderHelper createRenderHelper() {
                int rotation = facing;
                PowerPoleRenderHelper helper = new PowerPoleRenderHelper(world, pos, rotation, 2, 3) {
                    @Override
                    public void onUpdate() {
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
    						this.addExtraWire(connection1[0].fixedFrom, connection2[2].fixedFrom, 0.5F);
    						this.addExtraWire(connection1[2].fixedFrom, connection2[0].fixedFrom, 0.5F);
    					} else {
    						this.addExtraWire(connection1[0].fixedFrom, connection2[0].fixedFrom, 0.5F);
    						this.addExtraWire(connection1[2].fixedFrom, connection2[2].fixedFrom, 0.5F);
                        }
                    }
                };
                helper.addInsulatorGroup(-0.6F, 0.9F, 0,
                        helper.createInsulator(0.5F, 1.2F, -0.05F, 0.1F, -0.74F),
                        helper.createInsulator(0.5F, 1.2F, -0.05F, 0.9F, 0),
                        helper.createInsulator(0.5F, 1.2F, -0.05F, 0.1F, 0.74F)
                );
                helper.addInsulatorGroup(0.6F, 0.9F, 0,
                        helper.createInsulator(0.5F, 1.2F, 0.05F, 0.1F, -0.74F),
                        helper.createInsulator(0.5F, 1.2F, 0.05F, 0.9F, 0),
                        helper.createInsulator(0.5F, 1.2F, 0.05F, 0.1F, 0.74F)
                );
                return helper;
            }
        }
    }
    
    public static class Pole415vType0 extends TilePowerPole3 {
        @Override
        @Nonnull
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
