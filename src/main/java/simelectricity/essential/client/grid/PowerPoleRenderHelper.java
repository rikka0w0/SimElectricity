package simelectricity.essential.client.grid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;
import rikka.librikka.model.quadbuilder.RawQuadGroup;
import rikka.librikka.properties.UnlistedPropertyRef;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class PowerPoleRenderHelper {
    public final BlockPos pos;    //Real MC pos
    public final boolean mirroredAboutZ;
    public final int rotation;
    public final PowerPoleRenderHelper.Group[] groups;
    public final int insulatorPerGroup;
    private final IBlockAccess world;
    
    /**
     * Buffer
     */
    public final LinkedList<PowerPoleRenderHelper.ConnectionInfo[]> connectionInfo = new LinkedList();
    public final LinkedList<PowerPoleRenderHelper.ExtraWireInfo> extraWires = new LinkedList();
    
    public final LinkedList<BakedQuad> quadBuffer = new LinkedList();
    
    private int addedGroup;

    public PowerPoleRenderHelper(IBlockAccess world, BlockPos pos, int rotationMC, int numOfGroup, int insulatorPerGroup) {
        this(world, pos, rotationMC, false, numOfGroup, insulatorPerGroup);
    }

    public PowerPoleRenderHelper(IBlockAccess world, BlockPos pos, EnumFacing facing, boolean mirroredAboutZ, int numOfGroup, int insulatorPerGroup) {
        this(world, pos, PowerPoleRenderHelper.facing2rotation(facing), mirroredAboutZ, numOfGroup, insulatorPerGroup);
    }

    public PowerPoleRenderHelper(IBlockAccess world, BlockPos pos, int rotationMC, boolean mirroredAboutZ, int numOfGroup, int insulatorPerGroup) {
        this.world = world;
        this.pos = pos;
        rotation = rotationMC * 45 - 90;
        this.mirroredAboutZ = mirroredAboutZ;
        groups = new PowerPoleRenderHelper.Group[numOfGroup];
        this.insulatorPerGroup = insulatorPerGroup;
        addedGroup = 0;
    }

    public static int facing2rotation(EnumFacing facing) {
        switch (facing) {
            case SOUTH:
                return 0;
            case WEST:
                return 6;
            case NORTH:
                return 4;
            case EAST:
                return 2;
            default:
                return 0;
        }
    }
    
    /**
     * @return true if interval1 and interval2 intersects
     */
    public static boolean hasIntersection(Vec3f from1, Vec3f to1, Vec3f from2, Vec3f to2) {
        float m1 = (from1.x - to1.x) / (from1.z - to1.z);
        float k1 = from1.x - from1.z * m1;
        float m2 = (from2.x - to2.x) / (from2.z - to2.z);
        float k2 = from2.x - from2.z * m2;

        float zc = (k2 - k1) / (m1 - m2);
        float zx = m1 * zc + k1;
        
        return from1.x > zx && zx > to1.x || from1.x < zx && zx < to1.x;
    }
    
/*    //http://blog.csdn.net/rickliuxiao/article/details/6259322
    private static boolean between(float a, float X0, float X1) {  
    	float temp1= a-X0;  
    	float temp2= a-X1;  
        return (temp1<1e-8 && temp2 > -1e-8) || (temp2 <1e-6 && temp1 > -1e-8); 
    } 
    
    *//**
     * @return true if interval(p1,p2) and interval(p3,p4) intersects
     *//*
    public static boolean hasIntersection(Vec3f p1, Vec3f p2, Vec3f p3, Vec3f p4) {  
    	float line_x,line_z; 
        if ((MathHelper.abs(p1.x-p2.x)<1e-6) && (MathHelper.abs(p3.x-p4.x)<1e-6)) {
            return false;
        } else if ((MathHelper.abs(p1.x-p2.x)<1e-6)) {  
            if (between(p1.x,p3.x,p4.x)) {  
            	float k = (p4.z-p3.z)/(p4.x-p3.x);  
                line_x = p1.x;  
                line_z = k*(line_x-p3.x)+p3.z;  
      
                return  between(line_z,p1.z,p2.z);
            }  
            else {  
                return false;  
            }  
        }  
        else if (MathHelper.abs(p3.x-p4.x) < 1e-6) {  
            if (between(p3.x,p1.x,p2.x)) {  
            	float k = (p2.z-p1.z)/(p2.x-p1.x);  
                line_x = p3.x;  
                line_z = k*(line_x-p2.x)+p2.z;  
      
                return between(line_z,p3.z,p4.z);
            }  
            else {
                return false;  
            }
        } else {  
            float k1 = (p2.z-p1.z)/(p2.x-p1.x);   
            float k2 = (p4.z-p3.z)/(p4.x-p3.x);  
      
            if (MathHelper.abs(k1-k2) < 1e-6) {  
                return false;  
            } else {  
                line_x = ((p3.z - p1.z) - (k2*p3.x - k1*p1.x)) / (k1-k2);  
                line_z = k1*(line_x-p1.x)+p1.z;  
            }  
      
            return between(line_x,p1.x,p2.x)&&between(line_x,p3.x,p4.x); 
        }  
    }  */

    private static float calcInitSlope(float length, float tension) {
        double b = 4 * tension / length;
        double a = -b / length;
        return (float) -Math.atan(2 * a + b);
    }

    private static float calcAngle(float distance, float yFrom, float yTo, float tension) {
        return calcInitSlope(distance, tension) + (float) Math.atan((yTo - yFrom) / distance);
    }

    private static Vec3f fixConnectionPoints(Vec3f from, Vec3f to, float distance, float angle, float insulatorLength, float tension) {
        float lcos = insulatorLength * MathHelper.cos(angle);
        float atan = (float) Math.atan2(to.x - from.x, from.z - to.z);

        return from.add(lcos * MathHelper.sin(atan), insulatorLength * MathHelper.sin(angle), -lcos * MathHelper.cos(atan));
    }

    ////////////////////////////
    /// Utils
    ////////////////////////////
    public static void notifyChanged(ISEPowerPole... list) {
        GridRenderMonitor.instance.notifyChanged(list);
    }

    @Nullable
    public static PowerPoleRenderHelper fromState(IBlockState blockState) {
        if (!(blockState instanceof IExtendedBlockState))
            //Normally this should not happen, just in case, to prevent crashing
            return null;

        IExtendedBlockState exBlockState = (IExtendedBlockState) blockState;
        WeakReference<TileEntity> ref = exBlockState.getValue(UnlistedPropertyRef.propertyTile);
        TileEntity gridTile = ref == null ? null : ref.get();

        if (!(gridTile instanceof ISEPowerPole))
            //Normally this should not happen, just in case, to prevent crashing
            return null;

        return ((ISEPowerPole) gridTile).getRenderHelper();
    }
    
    @Nullable
    public static PowerPoleRenderHelper fromPos(IBlockAccess world, @Nullable BlockPos pos) {
    	if (pos == null)
    		return null;
    	
    	TileEntity te = world.getTileEntity(pos);    	
    	return te instanceof ISEPowerPole ? ((ISEPowerPole) te).getRenderHelper() : null;
    }
    
    @Nullable
    public PowerPoleRenderHelper fromPos(@Nullable BlockPos pos) {
    	return fromPos(this.world, pos);
    }
    
    public PowerPoleRenderHelper.Insulator createInsulator(float length, float tension, float offsetX, float offsetY, float offsetZ) {
        if (this.mirroredAboutZ)
            offsetX = -offsetX;

        float rotatedX = offsetZ * MathHelper.sin(rotation / 180F * MathAssitant.PI) + offsetX * MathHelper.cos(rotation / 180F * MathAssitant.PI);
        float rotatedZ = offsetZ * MathHelper.cos(rotation / 180F * MathAssitant.PI) - offsetX * MathHelper.sin(rotation / 180F * MathAssitant.PI);


        return new PowerPoleRenderHelper.Insulator(this, length, tension, rotatedX + 0.5F, offsetY, rotatedZ + 0.5F);
    }

    public void addInsulatorGroup(float centerX, float centerY, float centerZ, PowerPoleRenderHelper.Insulator... insulators) {
        if (addedGroup == groups.length)
            return;

        if (insulators.length != insulatorPerGroup)
            return;

        float rotatedX = centerZ * MathHelper.sin(this.rotation / 180F * MathAssitant.PI) + centerX * MathHelper.cos(this.rotation / 180F * MathAssitant.PI) + 0.5F;
        float rotatedZ = centerZ * MathHelper.cos(this.rotation / 180F * MathAssitant.PI) - centerX * MathHelper.sin(this.rotation / 180F * MathAssitant.PI) + 0.5F;

        groups[addedGroup] = new PowerPoleRenderHelper.Group(this, rotatedX, centerY, rotatedZ, insulators);
        addedGroup++;
    }


    public final void updateRenderData(BlockPos... neighborPosList) {
        this.connectionInfo.clear();
        this.extraWires.clear();
        addNeighors(neighborPosList);
        onUpdate();
        
        //Bake Quads
        this.quadBuffer.clear();
    }
    
    /**
     * Override this to add extra wires
     */
    public void onUpdate() {}

    public final void addNeighors(BlockPos... neighborPosList) {
        for (BlockPos neighborPos : neighborPosList) {
            if (neighborPos == null)
                continue;

            TileEntity te = world.getTileEntity(neighborPos);
            if (te instanceof ISEPowerPole) {
                PowerPoleRenderHelper helper = ((ISEPowerPole) te).getRenderHelper();
                if (helper == null) {
                	findVirtualConnection(neighborPos);
                }else {
                	findConnection(helper);
                }
            }else {
            	findVirtualConnection(neighborPos);
            }
        }
    }
    
    public final void addExtraWire(Vec3f from, Vec3f to, float tension) {
        this.extraWires.add(new PowerPoleRenderHelper.ExtraWireInfo(from, to, tension));
    }

    private void findVirtualConnection(BlockPos neighborCoord) {
        PowerPoleRenderHelper.Group group1 = null;
        float minDistance = Float.MAX_VALUE;

        //Find shortest path
        for (int i = 0; i < this.groups.length; i++) {
            float distance = this.groups[i].distanceTo(neighborCoord);
            if (distance < minDistance) {
                minDistance = distance;
                group1 = this.groups[i];
            }
        }
        
        PowerPoleRenderHelper.ConnectionInfo[] ret = new PowerPoleRenderHelper.ConnectionInfo[this.insulatorPerGroup];
        Vec3f from1 = group1.insulators[0].realPos;
        Vec3f to1 = group1.insulators[0].virtualize(neighborCoord);
        Vec3f from2 = group1.insulators[insulatorPerGroup - 1].realPos;
        Vec3f to2 = group1.insulators[insulatorPerGroup - 1].virtualize(neighborCoord);
        if (hasIntersection(from1, to1, from2, to2)) {
        	for (int i = 0; i < insulatorPerGroup; i++) {
        		ret[i] = new PowerPoleRenderHelper.ConnectionInfo(group1.insulators[i], group1.insulators[insulatorPerGroup - 1 - i].virtualize(neighborCoord));
        	}
        } else {
            for (int i = 0; i < insulatorPerGroup; i++) {
                ret[i] = new PowerPoleRenderHelper.ConnectionInfo(group1.insulators[i], group1.insulators[i].virtualize(neighborCoord));
            }
        }
        
        this.connectionInfo.add(ret);        
    }
    
    private void findConnection(PowerPoleRenderHelper neighbor) {
        PowerPoleRenderHelper.Group group1 = null;
        PowerPoleRenderHelper.Group group2 = null;
        float minDistance = Float.MAX_VALUE;

        //Find shortest path
        for (int i = 0; i < this.groups.length; i++) {
            for (int j = 0; j < neighbor.groups.length; j++) {
                float distance = this.groups[i].distanceTo(neighbor.groups[j]);
                if (distance < minDistance) {
                    minDistance = distance;
                    group1 = this.groups[i];
                    group2 = neighbor.groups[j];
                }
            }
        }

        PowerPoleRenderHelper.ConnectionInfo[] ret = new PowerPoleRenderHelper.ConnectionInfo[this.insulatorPerGroup];

        Vec3f from1 = group1.insulators[0].realPos;
        Vec3f to1 = group2.insulators[0].realPos;
        Vec3f from2 = group1.insulators[insulatorPerGroup - 1].realPos;
        Vec3f to2 = group2.insulators[insulatorPerGroup - 1].realPos;
        if (hasIntersection(from1, to1, from2, to2)) {
            for (int i = 0; i < insulatorPerGroup; i++) {
                ret[i] = new PowerPoleRenderHelper.ConnectionInfo(group1.insulators[i], group2.insulators[insulatorPerGroup - 1 - i]);
            }
        }else {
            for (int i = 0; i < insulatorPerGroup; i++) {
                ret[i] = new PowerPoleRenderHelper.ConnectionInfo(group1.insulators[i], group2.insulators[i]);
            }
        }

        this.connectionInfo.add(ret);
    }

    public void renderInsulator(RawQuadGroup insulatorModel, List<BakedQuad> quads) {
        for (PowerPoleRenderHelper.ConnectionInfo[] connections : connectionInfo) {
            for (PowerPoleRenderHelper.ConnectionInfo connection : connections) {
                RawQuadGroup insulator = insulatorModel.clone();

                insulator.rotateAroundZ(connection.insulatorAngle / MathAssitant.PI * 180F);
                insulator.rotateToVec(connection.from.x, connection.from.y, connection.from.z,
                        connection.fixedTo.x, connection.from.y, connection.fixedTo.z);
                insulator.translateCoord(connection.from.x, connection.from.y, connection.from.z);
                insulator.translateCoord(-this.pos.getX(), -this.pos.getY(), -this.pos.getZ());
                insulator.bake(quads);
            }
        }
    }

    public static class Insulator {
        public final PowerPoleRenderHelper parent;
        /**
         * Rotated offsets
         */
        public final float length, tension, offsetX, offsetY, offsetZ;
        public final Vec3f realPos;
		private Group group;

        private Insulator(PowerPoleRenderHelper parent, float length, float tension, float offsetX, float offsetY, float offsetZ) {
            this.parent = parent;
            this.length = length;
            this.tension = tension;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            realPos = new Vec3f(offsetX + parent.pos.getX(), offsetY + parent.pos.getY(), offsetZ + parent.pos.getZ());
        }
        
        public Group getGroup() {
        	return group;
        }
        
        public Vec3f virtualize(BlockPos newPos) {
        	return realPos.add(newPos.subtract(parent.pos));
        }
    }

    public static class Group {
        public final PowerPoleRenderHelper parent;
        /**
         * Center offsets, XZ respect to Block center, rotated
         */
        public final float centerX, centerY, centerZ;
        public final PowerPoleRenderHelper.Insulator[] insulators;

        private Group(PowerPoleRenderHelper parent, float centerX, float centerY, float centerZ, PowerPoleRenderHelper.Insulator... insulators) {
            this.parent = parent;
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
            this.insulators = new PowerPoleRenderHelper.Insulator[insulators.length];
            for (int i = 0; i < insulators.length; i++) {
                this.insulators[i] = insulators[i];
                this.insulators[i].group = this;
            }
        }
        
        /**
         * Find the real distance
         * @param group
         * @return
         */
        public float distanceTo(PowerPoleRenderHelper.Group group) {
            //Normalize respect to current TileEntity coordinate
            Vec3i offset = group.parent.pos.subtract(this.parent.pos);

            float x = offset.getX() + group.centerX - this.centerX;
            float y = offset.getY() + group.centerY - this.centerY;
            float z = offset.getZ() + group.centerZ - this.centerZ;

            return MathHelper.sqrt(x * x + z * z);
        }
        
        /**
         * Estimate the distance
         * @param pos
         * @return
         */
        public float distanceTo(Vec3i pos) {
            float x = pos.getX() - (this.parent.pos.getX() + this.centerX);
            float y = pos.getY() - (this.parent.pos.getY() + this.centerY);
            float z = pos.getZ() - (this.parent.pos.getZ() + this.centerZ);

            return MathHelper.sqrt(x * x + z * z);
        }
        
        public Group closest(Group... groups) {
        	Group ret = null;
        	float minDistance = Float.MAX_VALUE;
        	
        	for (Group group: groups) {
                float distance = distanceTo(group);
                if (distance < minDistance) {
                    minDistance = distance;
                    ret = group;
                }
        	}
        	
        	return ret;
        }
        
        public ConnectionInfo[] closest(ConnectionInfo[]... connections) {
        	ConnectionInfo[] ret = null;
        	float minDistance = Float.MAX_VALUE;
        	
        	for (ConnectionInfo[] connection: connections) {
                float distance = distanceTo(connection[0].fromGroup);
                if (distance < minDistance) {
                    minDistance = distance;
                    ret = connection;
                }
        	}
        	
        	return ret;
        }
    }
    
    public static class ConnectionInfo {
        public final Vec3f from, to;
        public final Vec3f fixedFrom, fixedTo;
        public final float insulatorAngle, tension;
        public final Group fromGroup;

        public final boolean isVirtual;
        
        private ConnectionInfo(PowerPoleRenderHelper.Insulator from, PowerPoleRenderHelper.Insulator to) {
            float tension = from.tension;
            float tension2 = to.tension;
            tension = tension < tension2 ? tension : tension2;

            this.from = from.realPos;
            this.to = to.realPos;

            float distance = this.from.distanceTo(this.to);
            float angle = PowerPoleRenderHelper.calcAngle(distance, this.from.y, this.to.y, tension);
            Vec3f fixedFrom = PowerPoleRenderHelper.fixConnectionPoints(this.from, this.to, distance, angle, from.length, tension);

            float dummyAngle = PowerPoleRenderHelper.calcAngle(distance, this.to.y, this.from.y, tension);
            Vec3f fixedTo = PowerPoleRenderHelper.fixConnectionPoints(this.to, this.from, distance, dummyAngle, to.length, tension);

            this.fixedFrom = fixedFrom;
            this.fixedTo = fixedTo;
            insulatorAngle = angle;
            this.tension = tension;
            this.fromGroup = from.group;
            
            this.isVirtual = false;
        }
        
        private ConnectionInfo(PowerPoleRenderHelper.Insulator from, Vec3f to) {
        	float tension = from.tension;
        	
        	this.from = from.realPos;
        	this.to = to;
        	
            float distance = this.from.distanceTo(this.to);
            float angle = PowerPoleRenderHelper.calcAngle(distance, this.from.y, this.to.y, tension);
            Vec3f fixedFrom = PowerPoleRenderHelper.fixConnectionPoints(this.from, this.to, distance, angle, from.length, tension);

            float dummyAngle = PowerPoleRenderHelper.calcAngle(distance, this.to.y, this.from.y, tension);
            Vec3f fixedTo = PowerPoleRenderHelper.fixConnectionPoints(this.to, this.from, distance, dummyAngle, from.length, tension);
            
            this.fixedFrom = fixedFrom;
            this.fixedTo = fixedTo;
            this.insulatorAngle = angle;
            this.tension = tension;
            this.fromGroup = from.group;
            
            this.isVirtual = true;
        }
    }

    public static class ExtraWireInfo {
        public final Vec3f from, to;
        public final float tension;

        private ExtraWireInfo(Vec3f from, Vec3f to, float tension) {
            this.from = from;
            this.to = to;
            this.tension = tension;
        }
    }
}
