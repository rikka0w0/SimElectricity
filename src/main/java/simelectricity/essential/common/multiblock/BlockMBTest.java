package simelectricity.essential.common.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.SEBlock;
import simelectricity.essential.common.SEItemBlock;
import simelectricity.essential.utils.Utils;

public class BlockMBTest extends SEBlock implements ISEMultiBlock{
	public static MultiBlockStructure qaq;
	public BlockMBTest() {
		super("essential_mbtest", Material.rock, SEItemBlock.class);
		
		qaq = new MultiBlockStructure(generateConfiguration());
		MultiBlockRegistry.registerMultiBlock(this);
	}

	@Override
	public void beforeRegister() {
		this.isBlockContainer = true;
		this.setCreativeTab(SEAPI.SETab);
	}
	
	@Override
	public boolean isBlockTrigger(Block b, int meta) {
		return true;
	}

	@Override
	public boolean attempCreateStructure(World world, int x, int y, int z, int side, EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
    	Utils.getPlayerSight(player);
    	return;
    }
   	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_){
		if (world.isRemote)
			return true;
		
		MultiBlockStructure.Result ret = qaq.check(world, x, y, z);
		if (ret != null){
			int[] loc = ret.getActualOffset(2, 1, 1);
			world.setBlock(loc[0], loc[1], loc[2], Blocks.clay);
			//world.setBlock(ret.xOriginActual, ret.yOriginActual, ret.zOriginActual, Blocks.clay);
		}
		return true;
	}
    
	private MultiBlockStructure.BlockInfo[][][] generateConfiguration(){
		MultiBlockStructure.BlockInfo x = new MultiBlockStructure.BlockInfo(this, 0, Blocks.clay, 0);
		MultiBlockStructure.BlockInfo[][][] configuration = new MultiBlockStructure.BlockInfo[1][][];
		
		configuration[0] = new MultiBlockStructure.BlockInfo[][]{
				{x,null},
				{x,x,x}
		};
		
		return configuration;
	}
}
