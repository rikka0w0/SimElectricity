package simElectricity.API.Client;

/**
 * 
 * Provides texture for custom render
 * @author rikka0w0
 *
 */
public interface ITextureProvider {
	/**
	 * Do bindTexture(ResourceLocation) here!
	 * @param index Which cube is being rendered
	 * @param side  The side of the cube
	 */
	void bindTexture(int index, int side);
}
