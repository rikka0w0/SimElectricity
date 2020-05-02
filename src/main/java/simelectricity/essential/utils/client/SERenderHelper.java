package simelectricity.essential.utils.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.math.MathAssitant;

import org.lwjgl.opengl.GL11;
import simelectricity.essential.Essential;

/**
 * double[index][dimension]: each double[index] contains a 3D double array. double[index][0] - xCoord, double[index][1] - yCoord, double[index][2] - zCoord
 *
 * @author Rikka0_0
 */
@Deprecated
@OnlyIn(Dist.CLIENT)
public class SERenderHelper {
    public static ResourceLocation createResourceLocation(String path) {
        return new ResourceLocation(Essential.MODID, path);
    }

    public static void rotateUpwardCoordSysTo(Direction direction) {
        switch (direction) {
            case DOWN:
                GL11.glRotatef(180, 1, 0, 0);
                return;
            case NORTH:
                GL11.glRotatef(270, 1, 0, 0);
                GL11.glRotatef(180, 0, 1, 0);
                return;
            case SOUTH:
                GL11.glRotatef(90, 1, 0, 0);
                return;
            case WEST:
                GL11.glRotatef(90, 0, 0, 1);
                GL11.glRotatef(270, 0, 1, 0);
                return;
            case EAST:
                GL11.glRotatef(270, 0, 0, 1);
                GL11.glRotatef(90, 0, 1, 0);
                return;
            default:
                return;
        }
    }


    ////////////////////////////////////////////////////////////
    /// Cube-Based model generation, double[8][3] cubeVertexes
    ////////////////////////////////////////////////////////////
    public static double[][] createCubeVertexes(double maxX, double maxY, double maxZ) {
        double[][] vertexes = new double[8][];
        double x = maxX / 2.0D;
        double z = maxZ / 2.0D;

        //Top
        vertexes[0] = new double[]{x, maxY, z};
        vertexes[1] = new double[]{x, maxY, -z};
        vertexes[2] = new double[]{-x, maxY, -z};
        vertexes[3] = new double[]{-x, maxY, z};

        //Bottom
        vertexes[4] = new double[]{x, 0, z};
        vertexes[5] = new double[]{x, 0, -z};
        vertexes[6] = new double[]{-x, 0, -z};
        vertexes[7] = new double[]{-x, 0, z};

        return vertexes;
    }

    public static void rotateCubeToDirection(double[][] cubeVertexes, Direction direction) {
        switch (direction) {
            case DOWN:
                SERenderHelper.rotateAroundX(cubeVertexes, 180);
                break;
            case NORTH:
                SERenderHelper.rotateAroundY(cubeVertexes, 180);
                SERenderHelper.rotateAroundX(cubeVertexes, 270);
                break;
            case SOUTH:
                SERenderHelper.rotateAroundX(cubeVertexes, 90);
                break;
            case WEST:
                SERenderHelper.rotateAroundY(cubeVertexes, 270);
                SERenderHelper.rotateAroundZ(cubeVertexes, 90);
                break;
            case EAST:
                SERenderHelper.rotateAroundY(cubeVertexes, 90);
                SERenderHelper.rotateAroundZ(cubeVertexes, 270);
                break;
            default:
                break;
        }
    }


    /////////////////////////////////////////////////////////////
    /// Utilities, double[i][3] vertexes, i can be any numbers
    /////////////////////////////////////////////////////////////
    public static double[][] createSafeCopy(double[][] vertexes) {
        double[][] ret = new double[vertexes.length][];
        for (int i = 0; i < vertexes.length; i++) {
            ret[i] = new double[vertexes[i].length];
            for (int j = 0; j < vertexes[i].length; j++)
                ret[i][j] = vertexes[i][j];
        }

        return ret;
    }

    public static void translateCoord(double[][] vertexes, double x, double y, double z) {
        for (int i = 0; i < vertexes.length; i++) {
            vertexes[i][0] += x;
            vertexes[i][1] += y;
            vertexes[i][2] += z;
        }
    }

    public static void rotateAroundX(double[][] vertexes, float angle) {
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);

        for (int i = 0; i < vertexes.length; i++) {
            double d0 = vertexes[i][0];
            double d1 = vertexes[i][1] * f1 + vertexes[i][2] * f2;
            double d2 = vertexes[i][2] * f1 - vertexes[i][1] * f2;
            vertexes[i][0] = d0;
            vertexes[i][1] = d1;
            vertexes[i][2] = d2;
        }
    }

    public static void rotateAroundY(double[][] vertexes, float angle) {
        float f1 = MathHelper.cos(angle * 0.01745329252F);
        float f2 = MathHelper.sin(angle * 0.01745329252F);

        for (int i = 0; i < vertexes.length; i++) {
            double d0 = vertexes[i][0] * f1 + vertexes[i][2] * f2;
            double d1 = vertexes[i][1];
            double d2 = vertexes[i][2] * f1 - vertexes[i][0] * f2;
            vertexes[i][0] = d0;
            vertexes[i][1] = d1;
            vertexes[i][2] = d2;
        }
    }

    public static void rotateAroundZ(double[][] vertexes, float angle) {
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);


        for (int i = 0; i < vertexes.length; i++) {
            double d0 = vertexes[i][0] * f1 + vertexes[i][1] * f2;
            double d1 = vertexes[i][1] * f1 - vertexes[i][0] * f2;
            double d2 = vertexes[i][2];
            vertexes[i][0] = d0;
            vertexes[i][1] = d1;
            vertexes[i][2] = d2;
        }
    }

    public static void rotateToVec(double[][] vertexes, double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd) {
        double distance = MathAssitant.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        SERenderHelper.rotateAroundY(vertexes, (float) (Math.atan2(zStart - zEnd, xEnd - xStart) * 180 / Math.PI));
        SERenderHelper.rotateAroundVector(vertexes, (float) (Math.acos((yEnd - yStart) / distance) * 180 / Math.PI), (zEnd - zStart) / distance, 0, (xStart - xEnd) / distance);
    }

    public static void rotateAroundVector(double[][] vertexes, float angle, double x, double y, double z) {
        //Normalize the axis vector
        double length = Math.sqrt(x * x + y * y + z * z);
        x = x / length;
        y = y / length;
        z = z / length;

        angle = angle * 0.01745329252F;    //Cast to radian
        double cos = MathHelper.cos(angle);
        double sin = MathHelper.sin(angle);

        for (int i = 0; i < vertexes.length; i++) {
            double d0 = vertexes[i][0] * (cos + x * x * (1 - cos)) + vertexes[i][1] * (x * y * (1 - cos) - z * sin) + vertexes[i][2] * (x * z * (1 - cos) + y * sin);
            double d1 = vertexes[i][0] * (x * y * (1 - cos) + z * sin) + vertexes[i][1] * (cos + y * y * (1 - cos)) + vertexes[i][2] * (y * z * (1 - cos) - x * sin);
            double d2 = vertexes[i][0] * (x * z * (1 - cos) - y * sin) + vertexes[i][1] * (y * z * (1 - cos) + x * sin) + vertexes[i][2] * (cos + z * z * (1 - cos));
            vertexes[i][0] = d0;
            vertexes[i][1] = d1;
            vertexes[i][2] = d2;
        }
    }

    public static TextureAtlasSprite[] createTextureArray(TextureAtlasSprite texture) {
        return new TextureAtlasSprite[]{texture, texture, texture, texture, texture, texture};
    }
}
