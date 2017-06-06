package simelectricity.essential.utils;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.essential.Essential;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class SERenderHelper {
	public static ResourceLocation createResourceLocation(String path){
		return new ResourceLocation(Essential.modID, path);
	}
	
	public static void rotateUpwardCoordSysTo(ForgeDirection direction){
		switch (direction){
		case DOWN:
			GL11.glRotatef(180, 1, 0, 0);
			return;
		case NORTH:
			GL11.glRotatef(270, 1, 0, 0);
			GL11.glRotatef(180, 0, 1, 0);
			return;
		case SOUTH:
			GL11.glRotatef(90, 1, 0, 0);
			return;
		case WEST:
			GL11.glRotatef(90, 0, 0, 1);
			GL11.glRotatef(270, 0, 1, 0);
			return;
		case EAST:
			GL11.glRotatef(270, 0, 0, 1);
			GL11.glRotatef(90, 0, 1, 0);
			return;
		default:
			return;
		}
    }
}
