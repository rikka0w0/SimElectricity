package simelectricity.essential.utils;

import net.minecraft.util.math.MathHelper;

public class Vec3f {
    /** X coordinate of Vec3D */
    public final float xCoord;
    /** Y coordinate of Vec3D */
    public final float yCoord;
    /** Z coordinate of Vec3D */
    public final float zCoord;
    
    public Vec3f(float x, float y, float z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }
    
    public float distanceTo(Vec3f vec) {
    	float x = xCoord-vec.xCoord;
    	float y = yCoord-vec.yCoord;
    	float z = zCoord-vec.zCoord;
    	return MathHelper.sqrt(x*x+y*y+z*z);
    }
    
    public Vec3f add(float x, float y, float z) {
    	return new Vec3f(xCoord+x, yCoord+y, zCoord+z);
    }
}
