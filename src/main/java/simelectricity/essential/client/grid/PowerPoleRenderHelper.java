package simelectricity.essential.client.grid;

import java.util.LinkedList;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.utils.math.SEMathHelper;
import simelectricity.essential.utils.math.Vec3f;

@SideOnly(Side.CLIENT)
public class PowerPoleRenderHelper {	
	private final IBlockAccess world;
	public final BlockPos pos;	//Real MC pos
	public final boolean mirroredAboutZ;
	public final int rotation;
	public final Group[] groups;
	public final int insulatorPerGroup;
	private int addedGroup;
	
	/**
	 * Buffer
	 */
	public LinkedList<ConnectionInfo[]> connectionInfo = new LinkedList();
	public LinkedList<Pair<Vec3f, Vec3f>> extraWires = new LinkedList();
	
	public PowerPoleRenderHelper(IBlockAccess world, BlockPos pos, int rotationMC, int numOfGroup, int insulatorPerGroup) {
		this(world, pos, rotationMC, false, numOfGroup, insulatorPerGroup);
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

	public PowerPoleRenderHelper(IBlockAccess world, BlockPos pos, EnumFacing facing, boolean mirroredAboutZ, int numOfGroup, int insulatorPerGroup) {
		this(world, pos, facing2rotation(facing), mirroredAboutZ, numOfGroup, insulatorPerGroup);
	}
	
	public PowerPoleRenderHelper(IBlockAccess world, BlockPos pos, int rotationMC, boolean mirroredAboutZ, int numOfGroup, int insulatorPerGroup) {
		this.world = world;
		this.pos = pos;
		this.rotation = rotationMC*45 - 90;
		this.mirroredAboutZ = mirroredAboutZ;
		this.groups = new Group[numOfGroup];
		this.insulatorPerGroup = insulatorPerGroup;
		this.addedGroup = 0;
	}
	
	public Insulator createInsulator(float length, float offsetX, float offsetY, float offsetZ) {	
		if (this.mirroredAboutZ)
			offsetX = -offsetX;
		
		float rotatedX = offsetZ * MathHelper.sin(rotation/180F*SEMathHelper.PI) + offsetX * MathHelper.cos(rotation/180F*SEMathHelper.PI);
		float rotatedZ = offsetZ * MathHelper.cos(rotation/180F*SEMathHelper.PI) - offsetX * MathHelper.sin(rotation/180F*SEMathHelper.PI);
		
		
		return new Insulator(this, length, rotatedX + 0.5F, offsetY, rotatedZ + 0.5F);
	}
	
	public void addInsulatorGroup(float centerX, float centerY, float centerZ, Insulator... insulators) {
		if (addedGroup == groups.length)
			return;
		
		if (insulators.length != insulatorPerGroup)
			return;
		
		float rotatedX = centerZ * MathHelper.sin(this.rotation/180F*SEMathHelper.PI) + centerX * MathHelper.cos(this.rotation/180F*SEMathHelper.PI) + 0.5F;
		float rotatedZ = centerZ * MathHelper.cos(this.rotation/180F*SEMathHelper.PI) - centerX * MathHelper.sin(this.rotation/180F*SEMathHelper.PI) + 0.5F;
		
		groups[addedGroup] = new Group(this, rotatedX, centerY, rotatedZ, insulators);
		addedGroup++;
	}
	
	public void updateRenderData(BlockPos... neighborPosList) {
		this.connectionInfo.clear();
		this.extraWires.clear();
		for (BlockPos neighborPos: neighborPosList) {
			if (neighborPos == null)
				continue;
			
			TileEntity te = world.getTileEntity(neighborPos);
			if (te instanceof ISEPowerPole) {
				PowerPoleRenderHelper helper = ((ISEPowerPole) te).getRenderHelper();
				if (helper == null)
					continue;//TODO: helper might be null, double check this! (far future)
				this.findConnection(helper);
			}
		}
	}
	
	public static Pair<Vec3f, Vec3f>[] swapIfIntersect(Pair<Vec3f, Vec3f>[] connections) {
		if (connections.length == 1)
			return connections;
		
		Vec3f from1 = connections[0].getLeft();
		Vec3f to1 = connections[0].getRight();
		Vec3f from2 = connections[connections.length-1].getLeft();
		Vec3f to2 = connections[connections.length-1].getRight();
		
		if (hasIntersection(from1, to1, from2, to2)){
			//Do Swap
			Pair<Vec3f, Vec3f>[] fixedConnections = new Pair[connections.length];

			for (int i=0; i<connections.length; i++) {
				fixedConnections[i] = Pair.of(connections[i].getLeft(), connections[connections.length-1-i].getRight());
			}
			
			return fixedConnections;
		}else {
			return connections;
		}
	}
	
	public static boolean hasIntersection(Vec3f from1, Vec3f to1, Vec3f from2, Vec3f to2) {
		double m1 = (from1.xCoord-to1.xCoord)/(from1.zCoord-to1.zCoord);
		double k1 = from1.xCoord - from1.zCoord * m1;
		double m2 = (from2.xCoord-to2.xCoord)/(from2.zCoord-to2.zCoord);
		double k2 = from2.xCoord - from2.zCoord * m2;
		
		double zc = (k2-k1)/(m1-m2);
		double zx = m1*zc + k1;
		
		return (from1.xCoord> zx && zx > to1.xCoord) || (from1.xCoord<zx && zx<to1.xCoord);
	}
	
	private static Pair<Insulator, Insulator>[] swapIfIntersectInternal(Pair<Insulator, Insulator>[] connections) {
		if (connections.length == 1)
			return connections;
				
		Vec3f from1 = connections[0].getLeft().realPos;
		Vec3f to1 = connections[0].getRight().realPos;
		Vec3f from2 = connections[connections.length-1].getLeft().realPos;
		Vec3f to2 = connections[connections.length-1].getRight().realPos;
		

		
		if (hasIntersection(from1, to1, from2, to2)){
			//Do Swap
			Pair<Insulator, Insulator>[] fixedConnections = new Pair[connections.length];

			for (int i=0; i<connections.length; i++) {
				fixedConnections[i] = Pair.of(connections[i].getLeft(), connections[connections.length-1-i].getRight());
			}
			
			return fixedConnections;
		}else {
			return connections;
		}
	}

    private static float calcInitSlope(float length, float tension){
    	double b = 4 * tension / length;
    	double a = -b / length;
    	return (float) -Math.atan(2*a+b);
    }
	
    private static float calcAngle(float distance, float yFrom, float yTo, float tension) {
    	return calcInitSlope(distance, tension) + (float) Math.atan((yTo-yFrom)/distance);
    }
    
    private static Vec3f fixConnectionPoints(Vec3f from, Vec3f to, float distance, float angle, float insulatorLength, float tension){        
        float lcos = insulatorLength * MathHelper.cos(angle);
        float atan = (float) Math.atan2(to.xCoord - from.xCoord, from.zCoord - to.zCoord);        
        
        return from.add(lcos * MathHelper.sin(atan), insulatorLength * MathHelper.sin(angle), - lcos * MathHelper.cos(atan));
    }
    
	private void findConnection(PowerPoleRenderHelper neighbor) {
		Group group1 = null;
		Group group2 = null;
		float minDistance = Float.MAX_VALUE;
		
		//Find shortest path
		for (int i=0; i<this.groups.length; i++) {
			for (int j=0; j<neighbor.groups.length; j++) {
				float distance = this.groups[i].distanceTo(neighbor.groups[j]);
				if (distance < minDistance) {
					minDistance = distance;
					group1 = this.groups[i];
					group2 = neighbor.groups[j];
				}
			}
		}
		
		Pair<Insulator, Insulator>[] connections = new Pair[insulatorPerGroup];
		for (int i=0; i<insulatorPerGroup; i++) {
			connections[i] = Pair.of(group1.insulators[i], group2.insulators[i]);
		}
		connections = swapIfIntersectInternal(connections);

		ConnectionInfo[] ret = new ConnectionInfo[insulatorPerGroup];
		for (int i=0; i<insulatorPerGroup; i++) {
			ret[i] = new ConnectionInfo(connections[i]);
		}

		this.connectionInfo.add(ret);
	}
	
	public static class Insulator {
		public final PowerPoleRenderHelper parent;
		/**
		 * Rotated offsets
		 */
		public final float length, offsetX, offsetY, offsetZ;
		public final Vec3f realPos;
		
		private Insulator(PowerPoleRenderHelper parent, float length, float offsetX, float offsetY, float offsetZ) {
			this.parent = parent;
			this.length = length;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.realPos = new Vec3f(offsetX + parent.pos.getX(), offsetY + parent.pos.getY(), offsetZ + parent.pos.getZ());
		}
	}
	
	public static class Group{
		public final PowerPoleRenderHelper parent;
		/**
		 * Center offsets, XZ respect to Block center, rotated
		 */
		public final float centerX, centerY, centerZ;
		public final Insulator[] insulators;
		private Group(PowerPoleRenderHelper parent, float centerX, float centerY, float centerZ, Insulator... insulators) {
			this.parent = parent;
			this.centerX = centerX;
			this.centerY = centerY;
			this.centerZ = centerZ;
			this.insulators = new Insulator[insulators.length];
			for (int i=0; i<insulators.length; i++) {
				this.insulators[i] = insulators[i];
			}
		}
		
		
		public float distanceTo(Group group) {			
			//Normalize respect to current TileEntity coordinate
			Vec3i offset = group.parent.pos.subtract(this.parent.pos);

			float x = offset.getX() + group.centerX - this.centerX;
			float y = offset.getY() + group.centerY - this.centerY;
			float z = offset.getZ() + group.centerZ - this.centerZ;
			
			return MathHelper.sqrt(x*x + z*z);
		}
	}
	
    public static class ConnectionInfo {
    	public final Vec3f from, to;
    	public final Vec3f fixedFrom, fixedTo;
    	public final float insulatorAngle;
    	
    	private ConnectionInfo(Pair<Insulator, Insulator> connection) {
    		float tension = 3;
    		this.from = connection.getLeft().realPos;
    		this.to = connection.getRight().realPos;
    		
    		float distance = from.distanceTo(to);
    		float angle = calcAngle(distance, from.yCoord, to.yCoord, tension);
    		Vec3f fixedFrom = fixConnectionPoints(from, to, distance, angle, connection.getLeft().length, tension);
    		
    		float dummyAngle = calcAngle(distance, to.yCoord, from.yCoord, tension);
    		Vec3f fixedTo = fixConnectionPoints(to, from, distance, dummyAngle, connection.getRight().length, tension);
            
    		this.fixedFrom = fixedFrom;
    		this.fixedTo = fixedTo;
    		this.insulatorAngle = angle;
    	}
    }
}
