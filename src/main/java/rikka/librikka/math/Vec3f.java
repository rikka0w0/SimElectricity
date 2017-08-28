package rikka.librikka.math;

import net.minecraft.util.math.MathHelper;

public class Vec3f {
    /**
     * X coordinate of Vec3D
     */
    public final float xCoord;
    /**
     * Y coordinate of Vec3D
     */
    public final float yCoord;
    /**
     * Z coordinate of Vec3D
     */
    public final float zCoord;

    public Vec3f(float x, float y, float z) {
        xCoord = x;
        yCoord = y;
        zCoord = z;
    }

    public float distanceTo(Vec3f vec) {
        float x = this.xCoord - vec.xCoord;
        float y = this.yCoord - vec.yCoord;
        float z = this.zCoord - vec.zCoord;
        return MathHelper.sqrt(x * x + y * y + z * z);
    }

    public Vec3f add(float x, float y, float z) {
        return new Vec3f(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Vec3f)) {
            return false;
        } else {
            Vec3f vec3f = (Vec3f) obj;
            return Float.compare(vec3f.xCoord, xCoord) == 0 && Float.compare(vec3f.yCoord, yCoord) == 0 && Float.compare(vec3f.zCoord, zCoord) == 0;
        }
    }

    public int hashCode() {
        long j = Float.floatToIntBits(xCoord);
        int i = (int) (j ^ j >>> 16);
        j = Float.floatToIntBits(yCoord);
        i = 15 * i + (int) (j ^ j >>> 16);
        j = Float.floatToIntBits(zCoord);
        i = 15 * i + (int) (j ^ j >>> 16);
        return i;
    }

    public String toString() {
        return "(" + xCoord + ", " + yCoord + ", " + zCoord + ")";
    }
}
