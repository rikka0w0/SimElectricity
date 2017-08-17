package simelectricity.essential.utils.math;

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
    
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (!(obj instanceof Vec3f))
        {
            return false;
        }
        else
        {
        	Vec3f vec3f = (Vec3f)obj;
            return Float.compare(vec3f.xCoord, this.xCoord) != 0 ? false : (Float.compare(vec3f.yCoord, this.yCoord) != 0 ? false : Float.compare(vec3f.zCoord, this.zCoord) == 0);
        }
    }

    public int hashCode()
    {
        long j = Float.floatToIntBits(this.xCoord);
        int i = (int)(j ^ j >>> 16);
        j = Float.floatToIntBits(this.yCoord);
        i = 15 * i + (int)(j ^ j >>> 16);
        j = Float.floatToIntBits(this.zCoord);
        i = 15 * i + (int)(j ^ j >>> 16);
        return i;
    }

    public String toString()
    {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }
}
