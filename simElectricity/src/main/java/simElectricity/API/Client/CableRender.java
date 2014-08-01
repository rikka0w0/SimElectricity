package simElectricity.API.Client;


public class CableRender extends CubeRender{
	public CableRender(ITextureProvider textureProvider) {
		super(textureProvider);
	}

	/**
	 * Render a parabolic wire between two points
	 * @param x0 Start X coordinate
	 * @param y0 Start Y coordinate
	 * @param z0 Start Z coordinate
	 * @param x1 End X coordinate
	 * @param y1 End Y coordinate
	 * @param z1 End Z coordinate
	 * @param thickness Thickness of the cable
	 * @param textureIndex The index of texture
	 */
	public void renderParabolicCable(double x0, double y0, double z0, double x1, double y1, double z1, double thickness, int textureIndex) {
		double distance=RenderUtil.distanceOf(x0,y0,z0,x1,y1,z1);
		RenderUtil.p2pRotation(x0, y0, z0, x1, y1, z1);
		render_parabola(distance,false, 6,12, thickness, textureIndex);
	}
	
	/**
	 * Render half of a parabolic wire between two points
	 * @param x0 Start X coordinate
	 * @param y0 Start Y coordinate
	 * @param z0 Start Z coordinate
	 * @param x1 End X coordinate
	 * @param y1 End Y coordinate
	 * @param z1 End Z coordinate
	 * @param thickness Thickness of the cable
	 * @param textureIndex The index of texture
	 */
	public void renderHalfParabolicCable(double x0, double y0, double z0, double x1, double y1, double z1, double thickness, int textureIndex) {
		double distance=RenderUtil.distanceOf(x0,y0,z0,x1,y1,z1);
		RenderUtil.p2pRotation(x0, y0, z0, x1, y1, z1);
		render_parabola(distance,true, distance * 0.075,12, thickness, textureIndex);
	}
	
	/**
	 * Render a straight piece of wire between two points
	 * @param x0 Start X coordinate
	 * @param y0 Start Y coordinate
	 * @param z0 Start Z coordinate
	 * @param x1 End X coordinate
	 * @param y1 End Y coordinate
	 * @param z1 End Z coordinate
	 * @param thickness Thickness of the cable
	 * @param textureIndex The index of texture
	 */
	public void renderCable(double x0, double y0, double z0, double x1, double y1, double z1, double thickness, int textureIndex) {
		double distance=RenderUtil.distanceOf(x0,y0,z0,x1,y1,z1);
		RenderUtil.p2pRotation(x0, y0, z0, x1, y1, z1);
		render_cube(thickness,distance,thickness,textureIndex);
	}
	
	/**
	 * Render half of a straight piece of wire between two points
	 * @param x0 Start X coordinate
	 * @param y0 Start Y coordinate
	 * @param z0 Start Z coordinate
	 * @param x1 End X coordinate
	 * @param y1 End Y coordinate
	 * @param z1 End Z coordinate
	 * @param thickness Thickness of the cable
	 * @param textureIndex The index of texture
	 */
	public void renderHalfCable(double x0, double y0, double z0, double x1, double y1, double z1, double thickness, int textureIndex) {
		double distance=RenderUtil.distanceOf(x0,y0,z0,x1,y1,z1);
		RenderUtil.p2pRotation(x0, y0, z0, x1, y1, z1);
		render_cube(thickness,distance/2,thickness,textureIndex);
	}	
}
