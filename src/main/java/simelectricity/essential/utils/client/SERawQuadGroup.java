package simelectricity.essential.utils.client;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

public class SERawQuadGroup implements ISERawModel<SERawQuadGroup>{
	private final LinkedList<ISERawElement> elements = new LinkedList();
	
	public SERawQuadGroup(){}
	
	public SERawQuadGroup(ISERawElement... rawModels){
		add(rawModels);
	}
	
	public void add(ISERawElement... rawModels){
		for (ISERawElement rawModel: rawModels){
			elements.add(rawModel);
		}
	}
	
	public void merge(SERawQuadGroup group){
		this.elements.addAll(group.elements);
	}

	@Override
	public SERawQuadGroup clone(){
		SERawQuadGroup ret = new SERawQuadGroup();
		for (ISERawElement part: elements)
			ret.add(part.clone());
		return ret;
	}
	
	@Override
	public void translateCoord(float x, float y, float z) {
		for (ISERawModel part: elements)
			part.translateCoord(x, y, z);
	}

	@Override
	public void rotateAroundX(float angle) {
		for (ISERawModel part: elements)
			part.rotateAroundX(angle);
	}

	@Override
	public void rotateAroundY(float angle) {
		for (ISERawModel part: elements)
			part.rotateAroundY(angle);
	}

	@Override
	public void rotateAroundZ(float angle) {
		for (ISERawModel part: elements)
			part.rotateAroundZ(angle);
	}

	@Override
	public void rotateToVec(float xStart, float yStart, float zStart,
			float xEnd, float yEnd, float zEnd) {
		for (ISERawModel part: elements)
			part.rotateToVec(xStart, yStart, zStart, xEnd, yEnd, zEnd);
	}

	@Override
    public void rotateToDirection(EnumFacing direction){
		for (ISERawModel part: elements)
			part.rotateToDirection(direction);
	}
	
	@Override
	public void rotateAroundVector(float angle, float x, float y, float z) {
		for (ISERawModel part: elements)
			part.rotateAroundVector(angle, x, y, z);
	}

	@Override
	public void bake(List<BakedQuad> list) {
		for (ISERawModel part: elements)
			part.bake(list);
	}
}
