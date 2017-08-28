package simelectricity.essential.utils.client;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.quadbuilder.SERawQuadCube;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Deprecated
@SideOnly(Side.CLIENT)
public class SERenderHeap {
    private final LinkedList<double[][]> cubes = new LinkedList();
    private final LinkedList<TextureAtlasSprite[]> textures = new LinkedList();

    @Override
    public SERenderHeap clone() {
        SERenderHeap ret = new SERenderHeap();
        for (double[][] cube : this.cubes)
            ret.cubes.add(SERenderHelper.createSafeCopy(cube));

        for (TextureAtlasSprite[] textureArray : this.textures) {
            ret.textures.add(new TextureAtlasSprite[]{
                    textureArray[0],
                    textureArray[1],
                    textureArray[2],
                    textureArray[3],
                    textureArray[4],
                    textureArray[5]
            });
        }
        return ret;
    }

    public SERenderHeap appendHeap(SERenderHeap heap) {
        cubes.addAll(heap.cubes);
        textures.addAll(heap.textures);
        return this;
    }

    public SERenderHeap addCube(double[][] cube, TextureAtlasSprite texture) {
        cubes.add(cube);
        textures.add(SERenderHelper.createTextureArray(texture));
        return this;
    }

    public SERenderHeap addCube(double[][] cube, TextureAtlasSprite[] textures) {
        cubes.add(cube);
        this.textures.add(textures);
        return this;
    }

    public SERenderHeap transform(double x, double y, double z) {
        Iterator<double[][]> cubeIterator = this.cubes.iterator();
        while (cubeIterator.hasNext())
            SERenderHelper.translateCoord(cubeIterator.next(), x, y, z);
        return this;
    }

    public SERenderHeap rotateToVec(double xStart, double yStart, double zStart, double xEnd, double yEnd, double zEnd) {
        Iterator<double[][]> cubeIterator = this.cubes.iterator();
        while (cubeIterator.hasNext())
            SERenderHelper.rotateToVec(cubeIterator.next(), xStart, yStart, zStart, xEnd, yEnd, zEnd);
        return this;
    }

    /**
     * Rotate respect to vector (x,y,z)
     *
     * @param angle in degree
     * @param x
     * @param y
     * @param z
     */
    public SERenderHeap rotateAroundVector(float angle, double x, double y, double z) {
        Iterator<double[][]> cubeIterator = this.cubes.iterator();
        while (cubeIterator.hasNext())
            SERenderHelper.rotateAroundVector(cubeIterator.next(), angle, x, y, z);
        return this;
    }

    public SERenderHeap rotateAroundX(float angle) {
        Iterator<double[][]> cubeIterator = this.cubes.iterator();
        while (cubeIterator.hasNext())
            SERenderHelper.rotateAroundX(cubeIterator.next(), angle);
        return this;
    }

    public SERenderHeap rotateAroundY(float angle) {
        Iterator<double[][]> cubeIterator = this.cubes.iterator();
        while (cubeIterator.hasNext())
            SERenderHelper.rotateAroundY(cubeIterator.next(), angle);
        return this;
    }

    public SERenderHeap rotateAroundZ(float angle) {
        Iterator<double[][]> cubeIterator = this.cubes.iterator();
        while (cubeIterator.hasNext())
            SERenderHelper.rotateAroundZ(cubeIterator.next(), angle);
        return this;
    }

    public void bake(List<BakedQuad> list) {
        Iterator<double[][]> cubeIterator = this.cubes.iterator();
        Iterator<TextureAtlasSprite[]> textureIterator = this.textures.iterator();
        while (cubeIterator.hasNext()) {
            double[][] cube = cubeIterator.next();
            TextureAtlasSprite[] textureArray = textureIterator.next();

            SERawQuadCube rawCube = new SERawQuadCube(cube, textureArray);
            rawCube.bake(list);
        }
    }
}
