package simelectricity.essential.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class Utils {
	/**
	 * @param player
	 * @param ignoreVertical If set to true, possible results are NESW, else the result can also be up or down/
	 * @return the direction where the player/entity is looking at
	 */
    public static ForgeDirection getPlayerSight(EntityLivingBase player, boolean ignoreVertical) {
        int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int pitch = Math.round(player.rotationPitch);
        
        if (!ignoreVertical) {
            if (pitch >= 65)
                return ForgeDirection.DOWN;  //1

            if (pitch <= -65)
                return ForgeDirection.UP;    //0
        }
        
        switch (heading) {
            case 0:
                return ForgeDirection.SOUTH; //2
            case 1:
                return ForgeDirection.WEST;  //5
            case 2:
                return ForgeDirection.NORTH; //3
            case 3:
                return ForgeDirection.EAST;  //4
            default:
                return null;
        }
    }
}
