package simelectricity.essential.utils.client;

import com.google.common.primitives.Ints;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import simelectricity.essential.utils.math.SEMathHelper;

import java.awt.*;
import java.util.List;

/**
 * Represent a 'raw' cube
 *
 * @author Rikka0_0
 */
public class SERawQuadCube implements ISERawElement<SERawQuadCube> {
    private final float[][] vertexes;
    private final TextureAtlasSprite[] icons;

    public SERawQuadCube(float maxX, float maxY, float maxZ, TextureAtlasSprite icon) {
        this(maxX, maxY, maxZ, new TextureAtlasSprite[]{icon, icon, icon, icon, icon, icon});
    }

    public SERawQuadCube(float maxX, float maxY, float maxZ, TextureAtlasSprite[] icons) {
        this.icons = icons;
        vertexes = new float[8][];
        float x = maxX / 2.0F;
        float z = maxZ / 2.0F;

        //Top
        this.vertexes[0] = new float[]{x, maxY, z};
        this.vertexes[1] = new float[]{x, maxY, -z};
        this.vertexes[2] = new float[]{-x, maxY, -z};
        this.vertexes[3] = new float[]{-x, maxY, z};

        //Bottom
        this.vertexes[4] = new float[]{x, 0, z};
        this.vertexes[5] = new float[]{x, 0, -z};
        this.vertexes[6] = new float[]{-x, 0, -z};
        this.vertexes[7] = new float[]{-x, 0, z};
    }

    @Deprecated
    public SERawQuadCube(double[][] vertexes, TextureAtlasSprite[] icons) {
        this.icons = icons;
        this.vertexes = new float[vertexes.length][];

        for (int i = 0; i < vertexes.length; i++) {
            this.vertexes[i] = new float[vertexes[i].length];
            for (int j = 0; j < vertexes[i].length; j++)
                this.vertexes[i][j] = (float) vertexes[i][j];
        }
    }

    public SERawQuadCube(float[][] vertexes, TextureAtlasSprite[] icons) {
        this.icons = icons;
        this.vertexes = new float[vertexes.length][];

        for (int i = 0; i < vertexes.length; i++) {
            this.vertexes[i] = new float[vertexes[i].length];
            for (int j = 0; j < vertexes[i].length; j++)
                this.vertexes[i][j] = vertexes[i][j];
        }
    }

    @Override
    public SERawQuadCube clone() {
        return new SERawQuadCube(this.vertexes, this.icons);
    }

    @Override
    public SERawQuadCube translateCoord(float x, float y, float z) {
        for (int i = 0; i < this.vertexes.length; i++) {
            this.vertexes[i][0] += x;
            this.vertexes[i][1] += y;
            this.vertexes[i][2] += z;
        }

        return this;
    }

    @Override
    public SERawQuadCube rotateAroundX(float angle) {
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);

        for (int i = 0; i < this.vertexes.length; i++) {
            float d0 = this.vertexes[i][0];
            float d1 = this.vertexes[i][1] * f1 + this.vertexes[i][2] * f2;
            float d2 = this.vertexes[i][2] * f1 - this.vertexes[i][1] * f2;
            this.vertexes[i][0] = d0;
            this.vertexes[i][1] = d1;
            this.vertexes[i][2] = d2;
        }

        return this;
    }

    @Override
    public SERawQuadCube rotateAroundY(float angle) {
        float f1 = MathHelper.cos(angle * 0.01745329252F);
        float f2 = MathHelper.sin(angle * 0.01745329252F);

        for (int i = 0; i < this.vertexes.length; i++) {
            float d0 = this.vertexes[i][0] * f1 + this.vertexes[i][2] * f2;
            float d1 = this.vertexes[i][1];
            float d2 = this.vertexes[i][2] * f1 - this.vertexes[i][0] * f2;
            this.vertexes[i][0] = d0;
            this.vertexes[i][1] = d1;
            this.vertexes[i][2] = d2;
        }

        return this;
    }

    @Override
    public SERawQuadCube rotateAroundZ(float angle) {
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);


        for (int i = 0; i < this.vertexes.length; i++) {
            float d0 = this.vertexes[i][0] * f1 + this.vertexes[i][1] * f2;
            float d1 = this.vertexes[i][1] * f1 - this.vertexes[i][0] * f2;
            float d2 = this.vertexes[i][2];
            this.vertexes[i][0] = d0;
            this.vertexes[i][1] = d1;
            this.vertexes[i][2] = d2;
        }

        return this;
    }

    @Override
    public SERawQuadCube rotateToVec(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd) {
        float distance = SEMathHelper.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        this.rotateAroundY((float) (Math.atan2(zStart - zEnd, xEnd - xStart) * 180 / Math.PI));
        this.rotateAroundVector((float) (Math.acos((yEnd - yStart) / distance) * 180 / Math.PI), (zEnd - zStart) / distance, 0, (xStart - xEnd) / distance);

        return this;
    }

    @Override
    public SERawQuadCube rotateToDirection(EnumFacing direction) {
        switch (direction) {
            case DOWN:
                this.rotateAroundX(180);
                break;
            case NORTH:
                this.rotateAroundY(180);
                this.rotateAroundX(270);
                break;
            case SOUTH:
                this.rotateAroundX(90);
                break;
            case WEST:
                this.rotateAroundY(270);
                this.rotateAroundZ(90);
                break;
            case EAST:
                this.rotateAroundY(90);
                this.rotateAroundZ(270);
                break;
            default:
                break;
        }

        return this;
    }

    @Override
    public SERawQuadCube rotateAroundVector(float angle, float x, float y, float z) {
        //Normalize the axis vector
        float length = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / length;
        y = y / length;
        z = z / length;

        angle = angle * 0.01745329252F;    //Cast to radian
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);

        for (int i = 0; i < this.vertexes.length; i++) {
            float d0 = this.vertexes[i][0] * (cos + x * x * (1 - cos)) + this.vertexes[i][1] * (x * y * (1 - cos) - z * sin) + this.vertexes[i][2] * (x * z * (1 - cos) + y * sin);
            float d1 = this.vertexes[i][0] * (x * y * (1 - cos) + z * sin) + this.vertexes[i][1] * (cos + y * y * (1 - cos)) + this.vertexes[i][2] * (y * z * (1 - cos) - x * sin);
            float d2 = this.vertexes[i][0] * (x * z * (1 - cos) - y * sin) + this.vertexes[i][1] * (y * z * (1 - cos) + x * sin) + this.vertexes[i][2] * (cos + z * z * (1 - cos));
            this.vertexes[i][0] = d0;
            this.vertexes[i][1] = d1;
            this.vertexes[i][2] = d2;
        }

        return this;
    }

    @Override
    public void bake(List<BakedQuad> list) {
        float uMin, uMax, vMin, vMax;

        //Down - Yneg
        if (this.icons[0] != null) {
            uMin = 0;
            uMax = 16;    //For 32x32 64x64 textures, this number is still 16 !!!!!
            vMin = 0;
            vMax = 16;
            list.add(new BakedQuad(Ints.concat(
                    SEBakedQuadHelper.vertexToInts(this.vertexes[7][0], this.vertexes[7][1], this.vertexes[7][2], Color.WHITE.getRGB(), this.icons[0], uMin, vMin),    //uMin, vMax
                    SEBakedQuadHelper.vertexToInts(this.vertexes[6][0], this.vertexes[6][1], this.vertexes[6][2], Color.WHITE.getRGB(), this.icons[0], uMin, vMax),    //uMin, vMin
                    SEBakedQuadHelper.vertexToInts(this.vertexes[5][0], this.vertexes[5][1], this.vertexes[5][2], Color.WHITE.getRGB(), this.icons[0], uMax, vMax), //uMax, vMin
                    SEBakedQuadHelper.vertexToInts(this.vertexes[4][0], this.vertexes[4][1], this.vertexes[4][2], Color.WHITE.getRGB(), this.icons[0], uMax, vMin)    //uMax, vMax
            ), 0, EnumFacing.DOWN, this.icons[0], true, DefaultVertexFormats.ITEM));
        }

        //Up - Ypos
        if (this.icons[1] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(new BakedQuad(Ints.concat(
                    SEBakedQuadHelper.vertexToInts(this.vertexes[0][0], this.vertexes[0][1], this.vertexes[0][2], Color.WHITE.getRGB(), this.icons[1], uMax, vMax),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[1][0], this.vertexes[1][1], this.vertexes[1][2], Color.WHITE.getRGB(), this.icons[1], uMax, vMin),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[2][0], this.vertexes[2][1], this.vertexes[2][2], Color.WHITE.getRGB(), this.icons[1], uMin, vMin),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[3][0], this.vertexes[3][1], this.vertexes[3][2], Color.WHITE.getRGB(), this.icons[1], uMin, vMax)
            ), 0, EnumFacing.UP, this.icons[1], true, DefaultVertexFormats.ITEM));
        }

        //North - Zneg
        if (this.icons[2] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(new BakedQuad(Ints.concat(
                    SEBakedQuadHelper.vertexToInts(this.vertexes[2][0], this.vertexes[2][1], this.vertexes[2][2], Color.WHITE.getRGB(), this.icons[2], uMax, vMin),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[1][0], this.vertexes[1][1], this.vertexes[1][2], Color.WHITE.getRGB(), this.icons[2], uMin, vMin),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[5][0], this.vertexes[5][1], this.vertexes[5][2], Color.WHITE.getRGB(), this.icons[2], uMin, vMax),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[6][0], this.vertexes[6][1], this.vertexes[6][2], Color.WHITE.getRGB(), this.icons[2], uMax, vMax)
            ), 0, EnumFacing.NORTH, this.icons[2], true, DefaultVertexFormats.ITEM));
        }

        //South - Zpos
        if (this.icons[3] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(new BakedQuad(Ints.concat(
                    SEBakedQuadHelper.vertexToInts(this.vertexes[3][0], this.vertexes[3][1], this.vertexes[3][2], Color.WHITE.getRGB(), this.icons[3], uMin, vMin),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[7][0], this.vertexes[7][1], this.vertexes[7][2], Color.WHITE.getRGB(), this.icons[3], uMin, vMax),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[4][0], this.vertexes[4][1], this.vertexes[4][2], Color.WHITE.getRGB(), this.icons[3], uMax, vMax),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[0][0], this.vertexes[0][1], this.vertexes[0][2], Color.WHITE.getRGB(), this.icons[3], uMax, vMin)
            ), 0, EnumFacing.SOUTH, this.icons[3], true, DefaultVertexFormats.ITEM));
        }

        //West - Xneg
        if (this.icons[4] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(new BakedQuad(Ints.concat(
                    SEBakedQuadHelper.vertexToInts(this.vertexes[3][0], this.vertexes[3][1], this.vertexes[3][2], Color.WHITE.getRGB(), this.icons[4], uMax, vMin),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[2][0], this.vertexes[2][1], this.vertexes[2][2], Color.WHITE.getRGB(), this.icons[4], uMin, vMin),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[6][0], this.vertexes[6][1], this.vertexes[6][2], Color.WHITE.getRGB(), this.icons[4], uMin, vMax),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[7][0], this.vertexes[7][1], this.vertexes[7][2], Color.WHITE.getRGB(), this.icons[4], uMax, vMax)
            ), 0, EnumFacing.WEST, this.icons[4], true, DefaultVertexFormats.ITEM));
        }

        //East - Xpos
        if (this.icons[5] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(new BakedQuad(Ints.concat(
                    SEBakedQuadHelper.vertexToInts(this.vertexes[1][0], this.vertexes[1][1], this.vertexes[1][2], Color.WHITE.getRGB(), this.icons[5], uMax, vMin),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[0][0], this.vertexes[0][1], this.vertexes[0][2], Color.WHITE.getRGB(), this.icons[5], uMin, vMin),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[4][0], this.vertexes[4][1], this.vertexes[4][2], Color.WHITE.getRGB(), this.icons[5], uMin, vMax),
                    SEBakedQuadHelper.vertexToInts(this.vertexes[5][0], this.vertexes[5][1], this.vertexes[5][2], Color.WHITE.getRGB(), this.icons[5], uMax, vMax)
            ), 0, EnumFacing.EAST, this.icons[5], true, DefaultVertexFormats.ITEM));
        }
    }
}
