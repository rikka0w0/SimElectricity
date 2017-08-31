package rikka.librikka.model.quadbuilder;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

import java.util.List;

public interface IRawModel<T extends IRawModel> {
    T clone();

    IRawModel translateCoord(float x, float y, float z);

    IRawModel rotateAroundX(float angle);

    IRawModel rotateAroundY(float angle);

    IRawModel rotateAroundZ(float angle);

    IRawModel rotateToVec(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd);

    IRawModel rotateToDirection(EnumFacing direction);

    IRawModel rotateAroundVector(float angle, float x, float y, float z);

    IRawModel scale(float scale);
    
    /**
     * Convert vertex/texture data represented by this class to BakedQuads,
     * which can immediately be rendered by MineCraft
     *
     * @param list BakedQuads will be added to this list, must NOT be null!!!
     */
    void bake(List<BakedQuad> list);
}
