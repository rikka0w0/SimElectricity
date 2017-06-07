package simelectricity.essential.utils;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Part of this source code is from BuildCraft
 * <p/>
 * Special thanks to SpaceToad and the BuildCraft Team
 * @author BuildCraft Team
 */
public class MatrixTranformations {
	/**
	 * Mirrors the array on the Y axis by calculating offsets from 0.5F
	 *
	 * @param targetArray
	 */
	public static void mirrorY(float[][] targetArray) {
		float temp = targetArray[1][0];
		targetArray[1][0] = (targetArray[1][1] - 0.5F) * -1F + 0.5F; // 1 -> 0.5F -> -0.5F -> 0F
		targetArray[1][1] = (temp - 0.5F) * -1F + 0.5F; // 0 -> -0.5F -> 0.5F -> 1F
	}

	/**
	 * Shifts the coordinates around effectively rotating something. Zero state
	 * is DOWN then -&gt; NORTH -&gt; WEST Note - To obtain Position, do a mirrorY() before
	 * rotating
	 *
	 * @param targetArray the array that should be rotated
	 */
	public static void rotate(float[][] targetArray) {
		for (int i = 0; i < 2; i++) {
			float temp = targetArray[2][i];
			targetArray[2][i] = targetArray[1][i];
			targetArray[1][i] = targetArray[0][i];
			targetArray[0][i] = temp;
		}
	}

	/**
	 * @param targetArray the array that should be transformed
	 * @param direction
	 */
	public static void transform(float[][] targetArray, ForgeDirection direction) {
		if ((direction.ordinal() & 0x1) == 1) {
			mirrorY(targetArray);
		}

		for (int i = 0; i < (direction.ordinal() >> 1); i++) {
			rotate(targetArray);
		}
	}
}
