package rikka.librikka.model.quadbuilder;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

import java.util.List;

public interface ISERawModel<T extends ISERawModel> {
    T clone();

    ISERawModel translateCoord(float x, float y, float z);

    ISERawModel rotateAroundX(float angle);

    ISERawModel rotateAroundY(float angle);

    ISERawModel rotateAroundZ(float angle);

    ISERawModel rotateToVec(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd);

    ISERawModel rotateToDirection(EnumFacing direction);

    ISERawModel rotateAroundVector(float angle, float x, float y, float z);

    /**
     * Convert vertex/texture data represented by this class to BakedQuads,
     * which can immediately be rendered by MineCraft
     *
     * @param list BakedQuads will be added to this list, must NOT be null!!!
     */
    void bake(List<BakedQuad> list);
}
