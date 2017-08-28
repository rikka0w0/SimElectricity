package rikka.librikka.model.quadbuilder;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

import java.util.LinkedList;
import java.util.List;

public class SERawQuadGroup implements ISERawModel<SERawQuadGroup> {
    private final LinkedList<ISERawElement> elements = new LinkedList();

    public SERawQuadGroup() {
    }

    public SERawQuadGroup(ISERawElement... rawModels) {
        this.add(rawModels);
    }

    public void add(ISERawElement... rawModels) {
        for (ISERawElement rawModel : rawModels) {
            this.elements.add(rawModel);
        }
    }

    public void merge(SERawQuadGroup group) {
        elements.addAll(group.elements);
    }

    @Override
    public SERawQuadGroup clone() {
        SERawQuadGroup ret = new SERawQuadGroup();
        for (ISERawElement part : this.elements)
            ret.add(part.clone());
        return ret;
    }

    @Override
    public SERawQuadGroup translateCoord(float x, float y, float z) {
        for (ISERawModel part : this.elements)
            part.translateCoord(x, y, z);

        return this;
    }

    @Override
    public SERawQuadGroup rotateAroundX(float angle) {
        for (ISERawModel part : this.elements)
            part.rotateAroundX(angle);

        return this;
    }

    @Override
    public SERawQuadGroup rotateAroundY(float angle) {
        for (ISERawModel part : this.elements)
            part.rotateAroundY(angle);

        return this;
    }

    @Override
    public SERawQuadGroup rotateAroundZ(float angle) {
        for (ISERawModel part : this.elements)
            part.rotateAroundZ(angle);

        return this;
    }

    @Override
    public SERawQuadGroup rotateToVec(float xStart, float yStart, float zStart,
                                      float xEnd, float yEnd, float zEnd) {
        for (ISERawModel part : this.elements)
            part.rotateToVec(xStart, yStart, zStart, xEnd, yEnd, zEnd);

        return this;
    }

    @Override
    public SERawQuadGroup rotateToDirection(EnumFacing direction) {
        for (ISERawModel part : this.elements)
            part.rotateToDirection(direction);

        return this;
    }

    @Override
    public SERawQuadGroup rotateAroundVector(float angle, float x, float y, float z) {
        for (ISERawModel part : this.elements)
            part.rotateAroundVector(angle, x, y, z);

        return this;
    }

    @Override
    public void bake(List<BakedQuad> list) {
        for (ISERawModel part : this.elements)
            part.bake(list);
    }
}
