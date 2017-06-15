package simelectricity.essential.grid;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.utils.Utils;

public class BlockTransmissionTowerCollisionBox extends SEBlock{
	public BlockTransmissionTowerCollisionBox() {
		super("essential_transmission_tower_collision_box", Material.rock, SEItemBlock.class);
	}
	
	@Override
	public void beforeRegister() {

	}

	////////////////////////////////////
	/// Rendering
	////////////////////////////////////
    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isNormalCube() {
		return false;
	}
    
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z,
			AxisAlignedBB axisAlignedBB, List collidingBoxes, Entity par7Entity){

		int meta = world.getBlockMetadata(x, y, z);
		
		if (meta == 0)
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0, 1, 0.05, 1);
		else if (meta == 1)
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0.5, 1, 0.05, 1);
		else if (meta == 2)
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0.5, 0, 0, 1, 0.05, 1);
		else if (meta == 3)
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0, 1, 0.05, 0.5);
		else if (meta == 4)
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0, 0.5, 0.05, 1);
		else if (meta == 5)
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0.5, 0.5, 0.05, 1);
		else if (meta == 6)
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0.5, 0, 0.5, 1, 0.05, 1);
		else if (meta == 7)
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0.5, 0, 0, 1, 0.05, 0.5);
		else if (meta == 8)
			Utils.addCollisionBoxToList(x, y, z, axisAlignedBB, collidingBoxes, 0, 0, 0, 0.5, 0.05, 0.5);
	}
	
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		
		if (meta == 0)
			setBlockBounds(0, 0, 0, 1, 0.05F, 1);
		else if (meta == 1)
			setBlockBounds(0, 0, 0.5F, 1, 0.05F, 1);
		else if (meta == 2)
				setBlockBounds(0.5F, 0, 0, 1, 0.05F, 1);
		else if (meta == 3)
			setBlockBounds(0, 0, 0, 1, 0.05F, 0.5F);
		else if (meta == 4)
			setBlockBounds(0, 0, 0, 0.5F, 0.05F, 1);
		else if (meta == 5)
			setBlockBounds(0, 0, 0.5F, 0.5F, 0.05F, 1);
		else if (meta == 6)
			setBlockBounds(0.5F, 0, 0.5F, 1, 0.05F, 1);
		else if (meta == 7)
			setBlockBounds(0.5F, 0, 0, 1, 0.05F, 0.5F);
		else if (meta == 8)
			setBlockBounds(0, 0, 0, 0.5F, 0.05F, 0.5F);
		else
			setBlockBounds(0, 0, 0, 1, 1, 1);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player){
		return null; //Player can not get this block anyway!
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		return null; //Player can not get this block anyway!
	}
	
	@Override
	public int damageDropped(int meta){
		return 0;
	}
}
