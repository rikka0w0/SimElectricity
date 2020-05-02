package simelectricity.essential.grid.transformer;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.ISubBlock;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.multiblock.MultiBlockStructure.Result;
import rikka.librikka.properties.Properties;
import simelectricity.api.SEAPI;
import simelectricity.essential.common.SEMultiBlockEnergyTile;

public abstract class BlockAbstractTransformer extends BlockBase implements ISubBlock {
	public final MultiBlockStructure structureTemplate;

    public BlockAbstractTransformer(String unlocalizedName, Material material) {
		super(unlocalizedName, material, ItemBlockBase.class);

		this.structureTemplate = this.createStructureTemplate();
		
        setCreativeTab(SEAPI.SETab);
        setHardness(3.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
	}
    
    protected abstract MultiBlockStructure createStructureTemplate();
    
    protected abstract ItemStack getItemToDrop(BlockState state);

	///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(BlockState state) {return true;}
	
    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    public BlockState getActualState(@Nonnull BlockState state, IBlockAccess world, BlockPos pos) {
    	TileEntity te = world.getTileEntity(pos);
        if (te instanceof SEMultiBlockEnergyTile) {
        	SEMultiBlockEnergyTile render = (SEMultiBlockEnergyTile) te;
            EnumFacing facing = render.getFacing();
            boolean mirrored = render.isMirrored();
            if (facing == null) {
                return state; //Prevent crashing!
            }

            state = state.withProperty(BlockHorizontal.FACING, facing)
                    .withProperty(Properties.propertyMirrored, mirrored);
        }
        return state;
    }

    ///////////////////////////////
    /// Block activities
    ///////////////////////////////
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote)
            return;

        Result ret = this.structureTemplate.attempToBuild(world, pos);
        if (ret != null) {
            ret.createStructure();
        }
        return;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, BlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
            this.structureTemplate.restoreStructure(te, state, true);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public int damageDropped(BlockState state) {
    	return getItemToDrop(state).getItemDamage();
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
    	List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
    	
    	ItemStack itemStack = getItemToDrop(state);
    	if (itemStack.getItem() != Items.AIR)
    		ret.add(itemStack);

        return ret;
    }
    
    /**
     * Creative-mode middle mouse button clicks
     */
    @Override
    public ItemStack getItem(World world, BlockPos pos, BlockState state) {
    	return getItemToDrop(state);
    }
}
