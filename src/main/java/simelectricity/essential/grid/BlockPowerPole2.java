package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.block.ISubBlock;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.properties.Properties;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.api.ISEHVCableConnector;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class BlockPowerPole2 extends BlockBase implements ISubBlock, ISEHVCableConnector {
    public static final String[] subNames = {"0", "1"};
    ///////////////////
    /// Utils
    ///////////////////
    private static final int[][] rodBlockOffsetMatrix = {
            {0, 3},    //{x,z}
            {3, 0},
            {0, -3},
            {-3, 0}
    };
    private static final int[][] cbOffsetMatrix = {
            {0, 1},    //{x,z}
            {1, 0},
            {0, -1},
            {-1, 0}
    };

    public BlockPowerPole2() {
        super("essential_powerpole2", Material.ROCK, BlockPowerPole2.ItemBlock.class);
		setCreativeTab(SEAPI.SETab);
		setHardness(0.2F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    private static LinkedList<BlockInfo> getRodBlockOffsets(IBlockState state) {
        LinkedList<BlockInfo> list = new LinkedList();

        int facing = state.getValue(Properties.facing2bit);
        int facing1 = facing - 1 & 3;
        int facing2 = facing + 1 & 3;

        list.add(new BlockInfo(BlockPowerPole2.rodBlockOffsetMatrix[facing1][0], 0, BlockPowerPole2.rodBlockOffsetMatrix[facing1][1], facing2 | 12));
        list.add(new BlockInfo(BlockPowerPole2.rodBlockOffsetMatrix[facing2][0], 0, BlockPowerPole2.rodBlockOffsetMatrix[facing2][1], facing1 | 12));

        for (int i = 1; i < 15; i++) {
            list.add(new BlockInfo(BlockPowerPole2.rodBlockOffsetMatrix[facing1][0], i, BlockPowerPole2.rodBlockOffsetMatrix[facing1][1], facing2 | 4));
            list.add(new BlockInfo(BlockPowerPole2.rodBlockOffsetMatrix[facing2][0], i, BlockPowerPole2.rodBlockOffsetMatrix[facing2][1], facing1 | 4));
        }

        return list;
    }

    private static LinkedList<Vec3i> getCollisionBoxBlockXZOffsets(IBlockState state) {
        LinkedList<Vec3i> list = new LinkedList();

        int facing = state.getValue(Properties.facing2bit);

        int facing1 = facing - 1 & 3;
        int xoffset = BlockPowerPole2.cbOffsetMatrix[facing1][0];
        int zoffset = BlockPowerPole2.cbOffsetMatrix[facing1][1];
        list.add(new Vec3i(xoffset, 0, zoffset));
        list.add(new Vec3i(xoffset * 2, 0, zoffset * 2));
        list.add(new Vec3i(xoffset * 4, 0, zoffset * 4));
        list.add(new Vec3i(xoffset * 5, 0, zoffset * 5));

        list.add(new Vec3i(xoffset * -1, 0, zoffset * -1));
        list.add(new Vec3i(xoffset * -2, 0, zoffset * -2));
        list.add(new Vec3i(xoffset * -4, 0, zoffset * -4));
        list.add(new Vec3i(xoffset * -5, 0, zoffset * -5));

        return list;
    }

    @Override
    public String[] getSubBlockUnlocalizedNames() {
        return BlockPowerPole2.subNames;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        subItems.add(new ItemStack(this, 1, 0));
        subItems.add(new ItemStack(this, 1, 1));
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    public static final IProperty<Boolean> propertyIsPole = PropertyBool.create("ispole");                //bit3
    @Override
    protected final BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this,
                new IProperty[]{Properties.type1bit, propertyIsPole, Properties.facing2bit},
                new IUnlistedProperty[]{UnlistedNonNullProperty.propertyGridTile});
    }

    @Override
    public final IBlockState getStateFromMeta(int meta) {
        meta &= 15;
        return getDefaultState().withProperty(Properties.type1bit, meta >> 3)
                .withProperty(propertyIsPole, (meta & 4) > 0)
                .withProperty(Properties.facing2bit, meta & 3);
    }

    @Override
    public final int getMetaFromState(IBlockState state) {
        int type = state.getValue(Properties.type1bit);
        boolean isRod = state.getValue(propertyIsPole);
        int facing = state.getValue(Properties.facing2bit);
        int meta = type << 3 | (isRod ? 4 : 0) | facing;
        return meta;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState retval = (IExtendedBlockState) state;

            TileEntity te = world.getTileEntity(pos);

            if (te instanceof ISEGridTile) {
                retval = retval.withProperty(UnlistedNonNullProperty.propertyGridTile, new WeakReference<>((ISEGridTile) te));
            }

            return retval;
        }
        return state;
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(Properties.type1bit);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int damage, EntityLivingBase placer) {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, damage, placer);
        int facingInt = 4 - MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int type = damage;
        return state.withProperty(Properties.type1bit, type)
                .withProperty(propertyIsPole, false)
                .withProperty(Properties.facing2bit, facingInt);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity te = this.getCenterTileFromRodPos(world, pos);
        if (te instanceof TilePowerPole)
            return new ItemStack(this.itemBlock, 1, this.damageDropped(world.getBlockState(te.getPos())));

        return null;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote)
            return;

        SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, 3));
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        int type = state.getValue(Properties.type1bit);
        boolean isRod = state.getValue(propertyIsPole);
        int facing = state.getValue(Properties.facing2bit);

        if (isRod) {
            int yc;
            if (state.getValue(Properties.type1bit) > 0) {
                yc = pos.getY() + 11;
            } else {
                yc = this.getBottomRodY(world, pos) + 11;
            }

            BlockPos centerPos = new BlockPos(pos.getX() + BlockPowerPole2.rodBlockOffsetMatrix[facing][0], yc, pos.getZ() + BlockPowerPole2.rodBlockOffsetMatrix[facing][1]);
            IBlockState centerState = world.getBlockState(centerPos);

            if (centerState.getBlock() == this && !centerState.getValue(propertyIsPole))
                world.setBlockToAir(centerPos);    //Sequentially everthing else will be removed
        } else {
            //Center Block
            TileEntity te = world.getTileEntity(pos);    //Do this before the tileEntity is removed!
            if (te instanceof ISEGridTile)
                SEAPI.energyNetAgent.detachGridNode(world, ((ISEGridTile) te).getGridNode());

            for (BlockInfo blockInfo : BlockPowerPole2.getRodBlockOffsets(state)) {
                BlockPos rodPos = blockInfo.getRealPos(pos).down(11);
                IBlockState rodState = world.getBlockState(rodPos);

                if (rodState.getBlock() == this && rodState.getValue(propertyIsPole))
                    world.setBlockToAir(rodPos);
            }

            for (Vec3i posXZOffset : BlockPowerPole2.getCollisionBoxBlockXZOffsets(state)) {
                BlockPos cbPos = pos.add(posXZOffset);
                IBlockState cbState = world.getBlockState(cbPos);
                if (cbState.getBlock() == BlockRegistry.powerPoleCollisionBox)
                    world.setBlockToAir(cbPos);
            }
        }


        super.breakBlock(world, pos, state);
    }

    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(IBlockState state) {return true;}
	
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (!state.getValue(propertyIsPole))
            return new TilePowerPole2();
        return null;
    }

    ///////////////////
    /// BoundingBox
    ///////////////////
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        boolean isRod = state.getValue(propertyIsPole);
        int facing = state.getValue(Properties.facing2bit);

        if (isRod) {
            BlockPos centerPos = pos.add(BlockPowerPole2.rodBlockOffsetMatrix[facing][0], 0, BlockPowerPole2.rodBlockOffsetMatrix[facing][1]);
            IBlockState centerState = source.getBlockState(centerPos);

            if (centerState.getBlock() == this) {
                if (!centerState.getValue(propertyIsPole))
                    if (facing == 0 || facing == 2) {
                        return new AxisAlignedBB(0.125F, 0, 0, 0.875F, 1, 1);
                    } else {
                        return new AxisAlignedBB(0, 0, 0.125F, 1, 1, 0.875F);
                    }
                else
                    return new AxisAlignedBB(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
            } else {
                return new AxisAlignedBB(0.375F, 0, 0.375F, 0.625F, 1, 0.625F);
            }
        } else {
            if (facing == 0 || facing == 2) {
                return new AxisAlignedBB(0, 0, 0.125F, 1, 0.25F, 0.875F);
            } else {
                return new AxisAlignedBB(0.125F, 0, 0, 0.875F, 0.25F, 1);
            }
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean magicBool) {
        boolean isRod = state.getValue(propertyIsPole);
        int facing = state.getValue(Properties.facing2bit);

        if (isRod) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.375F, 0, 0.375F, 0.625F, 1, 0.625F));

            BlockPos centerPos = pos.add(BlockPowerPole2.rodBlockOffsetMatrix[facing][0], 0, BlockPowerPole2.rodBlockOffsetMatrix[facing][1]);
            IBlockState centerState = world.getBlockState(centerPos);

            if (centerState.getBlock() == this) {
                if (!centerState.getValue(propertyIsPole)) {
                    if (facing == 0 || facing == 2)
                        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 0.125F, 1, 0.25F, 0.875F));
                    else
                        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.125F, 0, 0, 0.875F, 0.25F, 1));
                }
            }
        } else {
            if (facing == 0 || facing == 2) {
                Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0, 0, 0.125F, 1, 0.25F, 0.875F));
            } else {
                Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(0.125F, 0, 0, 0.875F, 0.25F, 1));
            }
        }
    }

    private int getBottomRodY(IBlockAccess world, BlockPos pos) {
        for (int count = 0; count < 20; count++) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (block == this && state.getValue(propertyIsPole) && state.getValue(Properties.type1bit) > 0)
                return pos.getY();

            pos = pos.down();
        }

        return -1;
    }

    private TileEntity getCenterTileFromRodPos(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        int type = state.getValue(Properties.type1bit);
        boolean isRod = state.getValue(propertyIsPole);
        int facing = state.getValue(Properties.facing2bit);

        if (isRod) {
            int yc;
            if (state.getValue(Properties.type1bit) > 0) {
                yc = pos.getY() + 11;
            } else {
                yc = this.getBottomRodY(world, pos) + 11;
            }

            BlockPos centerPos = new BlockPos(pos.getX() + BlockPowerPole2.rodBlockOffsetMatrix[facing][0], yc, pos.getZ() + BlockPowerPole2.rodBlockOffsetMatrix[facing][1]);
            IBlockState centerState = world.getBlockState(centerPos);

            if (centerState.getBlock() == this && !centerState.getValue(propertyIsPole))
                return world.getTileEntity(centerPos);

            return null;
        }

        return world.getTileEntity(pos);
    }

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(World world, BlockPos pos) {
        TileEntity te = this.getCenterTileFromRodPos(world, pos);

        if (te instanceof TilePowerPole)
            return (TilePowerPole) te;
        else
            return null;
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public static class ItemBlock extends ItemBlockBase implements ISimpleTexture {
        public ItemBlock(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getIconName(int damage) {
            return "essential_powerpole2_" + damage;
        }

        @Override
        public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
            int facing = newState.getValue(Properties.facing2bit);

            for (BlockInfo blockInfo : BlockPowerPole2.getRodBlockOffsets(newState))
                world.setBlockState(blockInfo.getRealPos(pos), BlockRegistry.powerPole2.getStateFromMeta(blockInfo.part));

            int cbtype = facing == 0 || facing == 2 ? 9 : 10;
            for (Vec3i posXZOffset : BlockPowerPole2.getCollisionBoxBlockXZOffsets(newState))
                world.setBlockState(pos.up(11).add(posXZOffset), BlockRegistry.powerPoleCollisionBox.getStateFromMeta(cbtype));

            //metedata [type][isRod][rotation]
            return super.placeBlockAt(stack, player, world, pos.up(11), side, hitX, hitY, hitZ, newState);
        }
    }
}
