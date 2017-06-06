package simelectricity.api.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Provides texture for custom render
 *
 * @author rikka0w0
 */
@SideOnly(Side.CLIENT)
public interface ITextureProvider {
    /**
     * Do bindTexture(ResourceLocation) here!
     *
     * @param index Which cube is being rendered
     * @param side  The side of the cube
     */
    void bindTexture(int index, int side);
}
