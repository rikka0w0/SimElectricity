package simelectricity.essential.utils;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
    public static final ForgeDirection getPlayerSight(EntityLivingBase player, boolean ignoreVertical) {
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
    
    /**
     * Drop items inside the inventory
     */
    public static final void dropItemIntoWorld(World world, int x, int y, int z, ItemStack item) {
        Random rand = new Random();

        if (item != null && item.stackSize > 0) {
            float rx = rand.nextFloat() * 0.8F + 0.1F;
            float ry = rand.nextFloat() * 0.8F + 0.1F;
            float rz = rand.nextFloat() * 0.8F + 0.1F;

            EntityItem entityItem = new EntityItem(world,
                    x + rx, y + ry, z + rz,
                    new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

            if (item.hasTagCompound()) {
                entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
            }

            float factor = 0.05F;
            entityItem.motionX = rand.nextGaussian() * factor;
            entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
            entityItem.motionZ = rand.nextGaussian() * factor;
            world.spawnEntityInWorld(entityItem);
            item.stackSize = 0;
        }
    }
    
	public static final void addCollisionBoxToList(int x, int y, int z, AxisAlignedBB addCollisionBoxToList, List collidingBoxes,
			double minX, double minY, double minZ, double maxX, double maxY, double maxZ){
		AxisAlignedBB axisalignedbb1 = AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
	
        if (axisalignedbb1 != null && addCollisionBoxToList.intersectsWith(axisalignedbb1))
        	collidingBoxes.add(axisalignedbb1);
	}
}
