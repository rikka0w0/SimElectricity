package simelectricity.essential.cable.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Part of this source code is from BuildCraft
 * <p/>
 * Special thanks to SpaceToad and the BuildCraft Team
 * @author BuildCraft Team
 */
@SideOnly(Side.CLIENT)
public class FakeBlock extends Block{
	public static final FakeBlock instance = new FakeBlock();
	
	private IIcon[] textures = new IIcon[6];
	private int renderMask = 0;
	private int colorMultiplier = 0xFFFFFF;
	
	private FakeBlock() {
		super(Material.glass);
		 //Always Clientside
	}
	
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		return colorMultiplier;
	}

	@Deprecated
	public int getColor() {
		return colorMultiplier;
	}

	@Override
	public int getBlockColor() {
		return colorMultiplier;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
		return (renderMask & (1 << side)) != 0;
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return textures[side];
	}
	
	public void clearRenderMask(){
		renderMask = 0;
	}
	
	public void setRenderSide(ForgeDirection side, boolean canRender){
		if (canRender) 
			renderMask |= 1 << side.ordinal();
		else 
			renderMask &= ~(1 << side.ordinal());
	}
	
	public void setIcon(ForgeDirection side, IIcon icon){
		textures[side.ordinal()] = icon;
	}
	
	public void setColor(int color) {
		this.colorMultiplier = color;
	}
	
}
