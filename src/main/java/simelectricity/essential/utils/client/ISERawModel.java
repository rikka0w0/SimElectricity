package simelectricity.essential.utils.client;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;

public interface ISERawModel<T extends ISERawModel> {
	T clone();
	void translateCoord(float x, float y, float z);
	void rotateAroundX(float angle);
	void rotateAroundY(float angle);
	void rotateAroundZ(float angle);
	void rotateToVec(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd);
	void rotateAroundVector(float angle, float x, float y, float z);
	
    /**
     * Convert vertex/texture data represented by this class to BakedQuads,
     * which can immediately be rendered by MineCraft
     * @param list BakedQuads will be added to this list, must NOT be null!!!
     */
	void bake(List<BakedQuad> list);
}
