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

import org.apache.commons.lang3.tuple.Pair;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.grid.UnlistedNonNullProperty;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

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
    public LinkedList<PowerPoleRenderHelper.ConnectionInfo[]> connectionInfo = new LinkedList();
    public LinkedList<PowerPoleRenderHelper.ExtraWireInfo> extraWires = new LinkedList();
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

    public static Pair<Vec3f, Vec3f>[] swapIfIntersect(Pair<Vec3f, Vec3f>[] connections) {
        if (connections.length == 1)
            return connections;

        Vec3f from1 = connections[0].getLeft();
        Vec3f to1 = connections[0].getRight();
        Vec3f from2 = connections[connections.length - 1].getLeft();
        Vec3f to2 = connections[connections.length - 1].getRight();

        if (PowerPoleRenderHelper.hasIntersection(from1, to1, from2, to2)) {
            //Do Swap
            Pair<Vec3f, Vec3f>[] fixedConnections = new Pair[connections.length];

            for (int i = 0; i < connections.length; i++) {
                fixedConnections[i] = Pair.of(connections[i].getLeft(), connections[connections.length - 1 - i].getRight());
            }

            return fixedConnections;
        } else {
            return connections;
        }
    }

    public static boolean hasIntersection(Vec3f from1, Vec3f to1, Vec3f from2, Vec3f to2) {
        double m1 = (from1.x - to1.x) / (from1.z - to1.z);
        double k1 = from1.x - from1.z * m1;
        double m2 = (from2.x - to2.x) / (from2.z - to2.z);
        double k2 = from2.x - from2.z * m2;

        double zc = (k2 - k1) / (m1 - m2);
        double zx = m1 * zc + k1;

        return from1.x > zx && zx > to1.x || from1.x < zx && zx < to1.x;
    }

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

    public static PowerPoleRenderHelper fromState(IBlockState blockState) {
        if (!(blockState instanceof IExtendedBlockState))
            //Normally this should not happen, just in case, to prevent crashing
            return null;

        IExtendedBlockState exBlockState = (IExtendedBlockState) blockState;
        WeakReference<ISEGridTile> ref = exBlockState.getValue(UnlistedNonNullProperty.propertyGridTile);
        ISEGridTile gridTile = ref == null ? null : ref.get();

        if (!(gridTile instanceof ISEPowerPole))
            //Normally this should not happen, just in case, to prevent crashing
            return null;

        return ((ISEPowerPole) gridTile).getRenderHelper();
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

    /**
     * Override this to add extra wires, DO NOT forget to call super.updateRenderData() first!
     *
     * @param neighborPosList
     */
    public void updateRenderData(BlockPos... neighborPosList) {
        this.connectionInfo.clear();
        this.extraWires.clear();
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

    public void addExtraWire(Vec3f from, Vec3f to, float tension) {
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
        }else {
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

        private Insulator(PowerPoleRenderHelper parent, float length, float tension, float offsetX, float offsetY, float offsetZ) {
            this.parent = parent;
            this.length = length;
            this.tension = tension;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            realPos = new Vec3f(offsetX + parent.pos.getX(), offsetY + parent.pos.getY(), offsetZ + parent.pos.getZ());
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
            }
        }


        public float distanceTo(PowerPoleRenderHelper.Group group) {
            //Normalize respect to current TileEntity coordinate
            Vec3i offset = group.parent.pos.subtract(this.parent.pos);

            float x = offset.getX() + group.centerX - this.centerX;
            float y = offset.getY() + group.centerY - this.centerY;
            float z = offset.getZ() + group.centerZ - this.centerZ;

            return MathHelper.sqrt(x * x + z * z);
        }
        
        public float distanceTo(BlockPos pos) {
            //Normalize respect to current TileEntity coordinate
            Vec3i offset = pos.subtract(this.parent.pos);

            float x = offset.getX() - this.centerX;
            float y = offset.getY() - this.centerY;
            float z = offset.getZ()  - this.centerZ;

            return MathHelper.sqrt(x * x + z * z);
        }
    }

    public static class ConnectionInfo {
        public final Vec3f from, to;
        public final Vec3f fixedFrom, fixedTo;
        public final float insulatorAngle, tension;

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
