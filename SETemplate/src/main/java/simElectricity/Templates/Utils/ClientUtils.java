package simElectricity.Templates.Utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * This class contains static methods, these methods can only be used on the client side. Attempt calling client utils from server codes will crash the server instantly.
 * </p>
 * Textures, rendering
 * 
 * @author Rikka0w0
 *
 */
public class ClientUtils {
	
	/**
	 * Returns the texture ID for a specified side according to the block orientation
	 * @param side
	 * @param direction the block orientation, see {@link ForgeDirection}
	 * @return the texture ID, 0 - bottom, 1 - top, 2 - front, 4 - back
	 */
	@SideOnly(Side.CLIENT)
    public static int getTextureID(int side, ForgeDirection direction) {
        switch (direction) {
            case NORTH:
                return sideAndFacingToSpriteOffset[side][3];
            case SOUTH:
                return sideAndFacingToSpriteOffset[side][2];
            case WEST:
                return sideAndFacingToSpriteOffset[side][5];
            case EAST:
                return sideAndFacingToSpriteOffset[side][4];
            case UP:
                return sideAndFacingToSpriteOffset[side][0];
            case DOWN:
                return sideAndFacingToSpriteOffset[side][1];
            default:
                return 0;
        }
    }
    
    /**
     * Internal use only! [side][facing]
     */
	@SideOnly(Side.CLIENT)
    private static byte[][] sideAndFacingToSpriteOffset = new byte[][] {
            { 3, 2, 0, 0, 0, 0 },
            { 2, 3, 1, 1, 1, 1 },
            { 1, 1, 3, 2, 5, 4 },
            { 0, 0, 2, 3, 4, 5 },
            { 4, 5, 4, 5, 3, 2 },
            { 5, 4, 5, 4, 2, 3 }
    };
}
