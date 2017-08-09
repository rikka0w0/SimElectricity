package simelectricity.essential.utils;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class Utils {
	/**
	 * [side][facing]
	 */
	public static final int[][] sideAndFacingToSpriteOffset = new int[][]{
        {3, 2, 0, 0, 0, 0},
        {2, 3, 1, 1, 1, 1},
        {1, 1, 3, 2, 5, 4},
        {0, 0, 2, 3, 4, 5},
        {4, 5, 4, 5, 3, 2},
        {5, 4, 5, 4, 2, 3}};
	
	/**
	 * @param player
	 * @param ignoreVertical If set to true, possible results are NESW, else the result can also be up or down/
	 * @return the direction where the player/entity is looking at
	 */
    public static final EnumFacing getPlayerSight(EntityLivingBase player) {
        int heading = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int pitch = Math.round(player.rotationPitch);
        
        if (pitch >= 65)
            return EnumFacing.DOWN;  //1

        if (pitch <= -65)
        	return EnumFacing.UP;    //0
        
        switch (heading) {
            case 0:
                return EnumFacing.SOUTH; //2
            case 1:
                return EnumFacing.WEST;  //5
            case 2:
                return EnumFacing.NORTH; //3
            case 3:
                return EnumFacing.EAST;  //4
            default:
                return null;
        }
    }
    
    public static final EnumFacing getPlayerSightHorizontal(EntityLivingBase player) {
        int heading = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        
        switch (heading) {
            case 0:
                return EnumFacing.SOUTH; //2
            case 1:
                return EnumFacing.WEST;  //5
            case 2:
                return EnumFacing.NORTH; //3
            case 3:
                return EnumFacing.EAST;  //4
            default:
                return null;
        }
    }
    
    /**
     * Drop items inside the inventory
     */
    public static final void dropItemIntoWorld(World world, BlockPos pos, ItemStack item) {
        Random rand = new Random();

        if (item != null && item.getCount() > 0) {
            float rx = rand.nextFloat() * 0.8F + 0.1F;
            float ry = rand.nextFloat() * 0.8F + 0.1F;
            float rz = rand.nextFloat() * 0.8F + 0.1F;

            EntityItem entityItem = new EntityItem(world,
                    pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
                    new ItemStack(item.getItem(), item.getCount(), item.getItemDamage()));

            if (item.hasTagCompound()) {
                entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
            }

            float factor = 0.05F;
            entityItem.motionX = rand.nextGaussian() * factor;
            entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
            entityItem.motionZ = rand.nextGaussian() * factor;
            world.spawnEntity(entityItem);
            item.setCount(0);
        }
    }
    	
	public static void chat(EntityPlayer player, String text) {
		player.sendMessage(new TextComponentString(text));
    }
	
	public static void chatWithLocalization(EntityPlayer player, String text) {
        player.sendMessage(new TextComponentString(I18n.translateToLocal(text)));
    }
	
	public static EnumFacing getDirectionFromRedstoneSide(int iSide){
		switch (iSide){
		case -1:
			return EnumFacing.UP;
		case 0:
			return EnumFacing.NORTH;
		case 1:
			return EnumFacing.EAST;
		case 2:
			return EnumFacing.SOUTH;
		case 3:
			return EnumFacing.WEST;
		}
		return null;
	}
}
