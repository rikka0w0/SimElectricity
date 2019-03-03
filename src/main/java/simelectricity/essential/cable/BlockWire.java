package simelectricity.essential.cable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.RayTraceHelper;
import rikka.librikka.Utils;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.properties.UnlistedPropertyRef;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISECableTile;
import simelectricity.api.tile.ISEWireTile;
import simelectricity.essential.api.ISEGenericWire;
import simelectricity.essential.utils.SEUnitHelper;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BlockWire extends BlockBase implements ISimpleTexture {
    ///////////////////////////////
    /// Wire Properties
    ///////////////////////////////
    public BlockWire() {
        this("essential_wire", Material.GLASS, ItemBlockWire.class,
                new String[]{"copper_thin", "copper_medium"},
                0.1F,
                new float[]{0.1F, 0.01F},
                TileWire.class);
        setCreativeTab(SEAPI.SETab);
        setHardness(0.2F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getIconName(int damage) {
        return "essential_wire";
    }

    protected static ThreadLocal<EnumFacing> nextPlacedSide = new ThreadLocal<>();
    protected static ThreadLocal<EnumFacing> nextPlacedto = new ThreadLocal<>();

    protected static class ItemBlockWire extends ItemBlockBase {
        public ItemBlockWire(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer placer, ItemStack stack) {
            TileEntity teSelected = world.getTileEntity(pos);
            TileEntity teNew = world.getTileEntity(pos.offset(side));

            if (teSelected instanceof ISEGenericWire) {
                return true;
            } else if (teNew instanceof ISEGenericWire) {
                return true;
            }

            return super.canPlaceBlockOnSide(world, pos, side, placer, stack);
        }

        @Override
        public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            float x = hitX-facing.getFrontOffsetX()-0.5f;
            float y = hitY-facing.getFrontOffsetY()-0.5f;
            float z = hitZ-facing.getFrontOffsetZ()-0.5f;


            EnumFacing to = null;
            if (facing.getAxis() == EnumFacing.Axis.Y) {
                if (MathHelper.abs(x) > MathHelper.abs(z)) {
                    if (x>0)
                        to = EnumFacing.EAST;
                    else
                        to = EnumFacing.WEST;
                } else {
                    if (z>0)
                        to = EnumFacing.SOUTH;
                    else
                        to = EnumFacing.NORTH;
                }
            } else if (facing.getAxis() == EnumFacing.Axis.X) {
                if (MathHelper.abs(y) > MathHelper.abs(z)) {
                    if (y>0)
                        to = EnumFacing.UP;
                    else
                        to = EnumFacing.DOWN;
                } else {
                    if (z>0)
                        to = EnumFacing.SOUTH;
                    else
                        to = EnumFacing.NORTH;
                }
            } else if (facing.getAxis() == EnumFacing.Axis.Z) {
                if (MathHelper.abs(x) > MathHelper.abs(y)) {
                    if (x>0)
                        to = EnumFacing.EAST;
                    else
                        to = EnumFacing.WEST;
                } else {
                    if (y>0)
                        to = EnumFacing.UP;
                    else
                        to = EnumFacing.DOWN;
                }
            }

            ItemStack itemStack = player.getHeldItem(hand);
            boolean shrinkItem = false;
            TileEntity teSelected = Utils.getTileEntitySafely(world, pos);
            TileEntity teNew = Utils.getTileEntitySafely(world, pos.offset(facing));

            if (teSelected instanceof ISEGenericWire) {
                RayTraceResult trace = this.rayTrace(world, player, false);
                ISEGenericWire wireTile = (ISEGenericWire) teSelected;
                EnumFacing tr_side = subHit_side(trace.subHit);
                EnumFacing tr_branch = subHit_branch(trace.subHit);

                if (tr_branch == null) {
                    // Center
                    if (facing != tr_side && facing != tr_side.getOpposite()) {
                        if (!wireTile.hasBranch(tr_side, facing) && world.isSideSolid(pos.offset(tr_side), tr_side.getOpposite())) {
                            shrinkItem = true;
                            if (!world.isRemote)
                                wireTile.addBranch(tr_side, facing, itemStack);
                        }
                    }
                } else {

                    if (facing == tr_side || facing == tr_side.getOpposite()) {

                        if (!wireTile.hasBranch(to, facing.getOpposite()) &&
                                (world.isSideSolid(pos.offset(to), to.getOpposite()) ||
                                world.getTileEntity(pos.offset(to)) instanceof ISECableTile)) {
                            shrinkItem = true;
                            if (!world.isRemote)
                                wireTile.addBranch(to, facing.getOpposite(), itemStack);
                        }
                    } else {
                        if (wireTile.hasBranch(tr_side, facing)) {
                            if (teNew instanceof ISEGenericWire) {
                                // Add branch in neighbor
                                if (!((ISEGenericWire) teNew).hasBranch(tr_side, tr_branch.getOpposite()) &&
                                        world.isSideSolid(pos.offset(facing).offset(tr_side), tr_side.getOpposite())) {
                                    shrinkItem = true;
                                    if (!world.isRemote)
                                        ((ISEGenericWire) teNew).addBranch(tr_side, tr_branch.getOpposite(), itemStack);
                                }
                            } else {
                                // Block edge, try to place a new neighbor wire
                                if (world.isSideSolid(pos.offset(tr_branch).offset(tr_side), tr_side.getOpposite())) {
                                    nextPlacedSide.set(tr_side);
                                    nextPlacedto.set(tr_branch.getOpposite());

                                    return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
                                }
                            }
                        } else {
                            if (!wireTile.hasBranch(tr_side, facing) && world.isSideSolid(pos.offset(tr_side), tr_side.getOpposite())) {
                                shrinkItem = true;
                                if (!world.isRemote)
                                    wireTile.addBranch(tr_side, facing, itemStack);
                            }
                        }
                    }
                }
            } else if (teNew instanceof ISEGenericWire) {
                EnumFacing wire_side = facing.getOpposite();
                // Selecting the block after the ISEGenericWire block
                if (!((ISEGenericWire) teNew).hasBranch(wire_side, to) && world.isSideSolid(pos, wire_side.getOpposite())) {
                    shrinkItem = true;
                    if (!world.isRemote)
                        ((ISEGenericWire) teNew).addBranch(wire_side, to, itemStack);
                }
            } else {
                // Attempt to place fresh wire

                nextPlacedSide.set(facing.getOpposite());
                nextPlacedto.set(to);

                return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
            }

            if (shrinkItem) {
                if (!player.isCreative())
                    itemStack.shrink(1);

                return EnumActionResult.SUCCESS;
            } else {
                return EnumActionResult.FAIL;
            }
        }
    }

    ///////////////////////////////
    ///Block Properties
    ///////////////////////////////
    public static EnumFacing[][] corners = new EnumFacing[][] {
            {EnumFacing.DOWN, EnumFacing.NORTH},
            {EnumFacing.DOWN, EnumFacing.SOUTH},
            {EnumFacing.DOWN, EnumFacing.WEST},
            {EnumFacing.DOWN, EnumFacing.EAST},
            {EnumFacing.UP, EnumFacing.NORTH},
            {EnumFacing.UP, EnumFacing.SOUTH},
            {EnumFacing.UP, EnumFacing.WEST},
            {EnumFacing.UP, EnumFacing.EAST},
            {EnumFacing.NORTH, EnumFacing.WEST},
            {EnumFacing.NORTH, EnumFacing.EAST},
            {EnumFacing.SOUTH, EnumFacing.WEST},
            {EnumFacing.SOUTH, EnumFacing.EAST},
    };

    public static boolean isCornerIdNormal(EnumFacing f1, EnumFacing f2) {
        if (f1.getAxis() == f2.getAxis())
            return false;

        if (f1.getAxis() == EnumFacing.Axis.Y) {
            return true;
        } else if (f1.getAxis() == EnumFacing.Axis.Z) {
            return f2.getAxis() != EnumFacing.Axis.Y;
        } else {
            return false;
        }
    }

    public static int cornerIdOf(EnumFacing f1, EnumFacing f2) {
        int index = -1;

        if (f1.getAxis() == f2.getAxis())
            return -1;

        EnumFacing wire_side = null, branch = null;
        if (f1.getAxis() == EnumFacing.Axis.Y) {
            wire_side = f1;
            branch = f2;
        } else if (f1.getAxis() == EnumFacing.Axis.Z) {
            if (f2.getAxis() == EnumFacing.Axis.Y) {
                wire_side = f2;
                branch = f1;
            } else {
                wire_side = f1;
                branch = f2;
            }
        } else {
            // f1.getAxis() == EnumFacing.Axis.X
            wire_side = f2;
            branch = f1;
        }

        if (wire_side == EnumFacing.DOWN) {
            if (branch == EnumFacing.NORTH) {
                index = 0;
            } else if (branch == EnumFacing.SOUTH) {
                index = 1;
            } else if (branch == EnumFacing.WEST) {
                index = 2;
            } else if (branch == EnumFacing.EAST) {
                index = 3;
            }
        } else if (wire_side == EnumFacing.UP) {
            if (branch == EnumFacing.NORTH) {
                index = 4;
            } else if (branch == EnumFacing.SOUTH) {
                index = 5;
            } else if (branch == EnumFacing.WEST) {
                index = 6;
            } else if (branch == EnumFacing.EAST) {
                index = 7;
            }
        } else if (wire_side == EnumFacing.NORTH) {
            if (branch == EnumFacing.WEST) {
                index = 8;
            } else if (branch == EnumFacing.EAST) {
                index = 9;
            }
        } else if (wire_side == EnumFacing.SOUTH) {
            if (branch == EnumFacing.WEST) {
                index = 10;
            } else if (branch == EnumFacing.EAST) {
                index = 11;
            }
        }

        return index;
    }

    public final float thickness;
    public final float[] resistances;
    private final Class<? extends TileWire> tileEntityClass;
    protected BlockWire(String name, Material material, Class<? extends ItemBlockWire> itemBlockClass,
                         String[] cableTypes, float thickness, float[] resistanceList, Class<? extends TileWire> tileEntityClass) {
        super(name, material, itemBlockClass);
        this.thickness = thickness;
        resistances = resistanceList;
        this.tileEntityClass = tileEntityClass;

        //Calc. collision boxes and cache them
//        this.cableBoundingBoxes = new AxisAlignedBB[thicknessList.length][7];
//        for (int i=0; i<thicknessList.length; i++) {
//            float min = 0.5F - thicknessList[i] / 2F;
//            float max = 0.5F + thicknessList[i] / 2F;
//
//            for (EnumFacing side: EnumFacing.VALUES) {
//                cableBoundingBoxes[i][side.ordinal()] = RayTraceHelper.createAABB(side, min, 0, min, max, min, max);
//            }
//
//            cableBoundingBoxes[i][6] = new AxisAlignedBB(min, min, min, max, max, max);
//        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        int type = stack.getItemDamage();
        tooltip.add(I18n.translateToLocal("gui.sime:resistivity") + ": " + SEUnitHelper.getStringWithoutUnit(2F*resistances[type]) + "\u03a9/m");
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {return true;}

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        TileWire wire;
        try {
            wire = tileEntityClass.getConstructor().newInstance();
            //if (!world.isRemote)    //createTileEntity is only called by the server thread when the block is placed at the first
                //wire.setResistanceOnPlace(this.resistances[this.getMetaFromState(state)]);
            return wire;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected final BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[0],
                new IUnlistedProperty[] {UnlistedPropertyRef.propertyTile});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState retval = (IExtendedBlockState) state;

            TileEntity te = world.getTileEntity(pos);

            if (te instanceof ISEWireTile) {
                retval = retval.withProperty(UnlistedPropertyRef.propertyTile, new WeakReference<>(te));
            }

            return retval;
        }
        return state;
    }

    //////////////////////////////////
    ///CollisionBoxes
    //////////////////////////////////
    public Vec3d getBranchVecOffset(EnumFacing side) {
        double x = 0, y = 0, z = 0;
        switch (side) {
            case DOWN:
                y = thickness / 2 - 0.5F;
                break;
            case UP:
                y = 0.5F-thickness / 2;
                break;
            case NORTH:
                z = thickness / 2 - 0.5F;
                break;
            case SOUTH:
                z = 0.5F - thickness / 2;
                break;
            case WEST:
                x = thickness / 2 - 0.5F;
                break;
            case EAST:
                x = 0.5F - thickness / 2;
                break;
        }

        return new Vec3d(x, y, z);
    }

    public AxisAlignedBB getBranchBoundingBox(EnumFacing side, EnumFacing branch, boolean ignoreCorner, boolean onlyCorner) {
        float min = 0.5F - thickness / 2.0F;
        float max = 0.5F + thickness / 2.0F;

        float yMin = 0;
        float yMax = min;

        if (ignoreCorner)
            yMin = thickness;
        if (onlyCorner)
            yMax = thickness;

        return (branch == null) ?
                new AxisAlignedBB(min, min, min, max, max, max).offset(getBranchVecOffset(side)) : // Center
                RayTraceHelper.createAABB(branch, min, yMin, min, max, yMax, max).offset(getBranchVecOffset(side));
    }

    public AxisAlignedBB getCenterBoundingBox(ISEGenericWire wireTile, EnumFacing side) {
        double x1, y1, z1, x2, y2, z2;
        x1 = 0.5 - thickness / 2;
        y1 = x1;
        z1 = x1;
        x2 = 0.5 + thickness / 2;
        y2 = x2;
        z2 = x2;

        //Branches
        if (wireTile.hasBranch(side, EnumFacing.DOWN))
            y1 = 0;

        if (wireTile.hasBranch(side, EnumFacing.UP))
            y2 = 1;

        if (wireTile.hasBranch(side, EnumFacing.NORTH))
            z1 = 0;

        if (wireTile.hasBranch(side, EnumFacing.SOUTH))
            z2 = 1;

        if (wireTile.hasBranch(side, EnumFacing.WEST))
            x1 = 0;

        if (wireTile.hasBranch(side, EnumFacing.EAST))
            x2 = 1;

        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2).offset(getBranchVecOffset(side));
    }

    public AxisAlignedBB getCornerBoundingBox(ISEGenericWire wireTile, EnumFacing side1, EnumFacing side2) {
        EnumFacing e1 = null;
        EnumFacing e2 = null;
        if (side1.getAxis() == EnumFacing.Axis.Y) {
            e1 = side1;
            e2 = side2;
        } else if (side2.getAxis() == EnumFacing.Axis.Y) {
            e1 = side2;
            e2 = side1;
        } else  if (side1.getAxis() == EnumFacing.Axis.Z) {
            e1 = side1;
            e2 = side2;
        } else if (side2.getAxis() == EnumFacing.Axis.Z) {
            e1 = side2;
            e2 = side1;
        }

        double x1, y1, z1, x2, y2, z2;
        x1 = 0.5 + thickness / 2;
        y1 = x1;
        z1 = x1;
        x2 = 0.5 - thickness / 2;
        y2 = x2;
        z2 = x2;


        if (e1 == EnumFacing.DOWN) {
            y1 = 0;

            if (e2 == EnumFacing.NORTH) {
                z1 = 0;
            } else if (e2 == EnumFacing.SOUTH) {
                z2 = 1;
            } else if (e2 == EnumFacing.WEST) {
                x1 = 0;
            } else if (e2 == EnumFacing.EAST) {
                x2 = 1;
            }
        } else if (e1 == EnumFacing.UP) {
            y2 = 1;

            if (e2 == EnumFacing.NORTH) {
                z1 = 0;
            } else if (e2 == EnumFacing.SOUTH) {
                z2 = 1;
            } else if (e2 == EnumFacing.WEST) {
                x1 = 0;
            } else if (e2 == EnumFacing.EAST) {
                x2 = 1;
            }
        } else if (e1 == EnumFacing.NORTH) {
            z1 = 0;

            if (e2 == EnumFacing.WEST) {
                x1 = 0;
            } else if (e2 == EnumFacing.EAST) {
                x2 = 1;
            }
        } else if (e1 == EnumFacing.SOUTH) {
            z2 = 1;

            if (e2 == EnumFacing.WEST) {
                x1 = 0;
            } else if (e2 == EnumFacing.EAST) {
                x2 = 1;
            }
        }

        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    public static EnumFacing subHit_side(int subHit) {
        return EnumFacing.VALUES[(subHit>>4) & 0x07];
    }

    @Nullable
    public static EnumFacing subHit_branch(int subHit) {
        int to_int = subHit & 0x0F;
        return to_int > EnumFacing.VALUES.length ? null : EnumFacing.VALUES[to_int];
    }

    public static boolean subHit_isBranch(int subHit) {
        return subHit > -1 && subHit < 256;
    }

    public static boolean subHit_isCorner(int subHit) {
        return (subHit & 0x80) > 0;
    }

    @Override
    @Nullable
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        return this.rayTrace(world, pos, start, end);
    }

    @Nullable
    public RayTraceResult rayTrace(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        Vec3d start = player.getPositionVector().addVector(0, player.getEyeHeight(), 0);
        double reachDistance = 5;
        if (player instanceof EntityPlayerMP)
            reachDistance = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();

        Vec3d end = start.add(player.getLookVec().normalize().scale(reachDistance));
        return this.rayTrace(world, pos, start, end);
    }

    @Nullable
    public RayTraceResult rayTrace(IBlockAccess world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof ISEGenericWire))
            return RayTraceHelper.computeTrace(null, pos, start, end, Block.FULL_BLOCK_AABB, -1);

        ISEGenericWire wireTile = (ISEGenericWire) tile;

        RayTraceResult best = null;
        for (EnumFacing wire_side:  EnumFacing.VALUES) {
            boolean hasConnection = false;

            // Branches
            for (EnumFacing to : EnumFacing.VALUES) {
                if (wireTile.hasBranch(wire_side, to)) {
                    hasConnection = true;

                    boolean hasCorner = wireTile.hasBranch(to, wire_side);
                    best = RayTraceHelper.computeTrace(best, pos, start, end,
                            getBranchBoundingBox(wire_side, to, hasCorner, false),
                            (wire_side.ordinal() << 4) | to.ordinal());

                    if (hasCorner)
                        best = RayTraceHelper.computeTrace(best, pos, start, end,
                                getBranchBoundingBox(wire_side, to, false, true),
                                0x80 | (wire_side.ordinal() << 4) | to.ordinal());
                }
            }

            // Center
            if (hasConnection)
                best = RayTraceHelper.computeTrace(best, pos, start, end, getBranchBoundingBox(wire_side,null, false, false), (wire_side.ordinal() << 4) | 7);
        }

        return best;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB axisAlignedBB,
                                      List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isPistonMoving) {
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof ISEWireTile))
            return;

        ISEGenericWire wireTile = (ISEGenericWire) te;

        for (EnumFacing wire_side: EnumFacing.VALUES) {
            double min = 0.5 - thickness / 2;
            double max = 0.5 + thickness / 2;

            double x = 0, y = 0 , z = 0;
            switch (wire_side) {
                case DOWN:
                    y = thickness / 2 - 0.5F;
                    break;
                case UP:
                    y = 0.5F-thickness / 2;
                    break;
                case NORTH:
                    z = thickness / 2 - 0.5F;
                    break;
                case SOUTH:
                    z = 0.5F - thickness / 2;
                    break;
                case WEST:
                    x = thickness / 2 - 0.5F;
                    break;
                case EAST:
                    x = 0.5F - thickness / 2;
                    break;
            }

            boolean hasConnection = false;

            //Branches
            if (wireTile.hasBranch(wire_side, EnumFacing.DOWN)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, y, min+z, max+x, max+y, max+z));
            }

            if (wireTile.hasBranch(wire_side, EnumFacing.UP)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, min+y, min+z, max+x, 1+y, max+z));
            }

            if (wireTile.hasBranch(wire_side, EnumFacing.NORTH)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, min+y, z, max+x, max+y, max+z));
            }

            if (wireTile.hasBranch(wire_side, EnumFacing.SOUTH)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, min+y, min+z, max+x, max+y, 1+z));
            }

            if (wireTile.hasBranch(wire_side, EnumFacing.WEST)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(x, min+y, min+z, max+x, max+y, max+z));
            }

            if (wireTile.hasBranch(wire_side, EnumFacing.EAST)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, min+y, min+z, 1+x, max+y, max+z));
            }

            if (hasConnection) {
                //Center
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, min+y, min+z, max+x, max+y, max+z));
            }
        }
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof ISEGenericWire))
            return new AxisAlignedBB(0,0,0,1,1,1);  //This is not supposed to happen

        ISEGenericWire wireTile = (ISEGenericWire) te;
        RayTraceResult trace = Minecraft.getMinecraft().objectMouseOver;

        if (trace == null || trace.subHit < 0 || !pos.equals(trace.getBlockPos())) {
            // Perhaps we aren't the object the mouse is over
            return new AxisAlignedBB(0,0,0,1,1,1);
        }

        AxisAlignedBB aabb = null;
        if (subHit_isBranch(trace.subHit)) {    //Center, corner or branches
            boolean isCorner = subHit_isCorner(trace.subHit);
            EnumFacing wire_side = subHit_side(trace.subHit);
            EnumFacing to = subHit_branch(trace.subHit);

            if (isCorner) {
                // Corner
                aabb = getCornerBoundingBox(wireTile, wire_side, to);
            } else {
                if (to == null) {
                    // Center
                    aabb = getCenterBoundingBox(wireTile, wire_side);
                } else {
                    aabb = getBranchBoundingBox(wire_side, to, wireTile.hasBranch(to, wire_side), false);
                }
            }

            aabb = aabb.offset(pos).expand(0.01, 0.01, 0.01);
        }

        return aabb;
    }

    @Nullable
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0,0,0,0,0,0);
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof ISEGenericWire))
            return false;        //Normally this could not happen, but just in case!




        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISEGenericWire) {
            ISEGenericWire wireTile = (ISEGenericWire) te;
            wireTile.onRenderingUpdateRequested();

            int x = fromPos.getX() - pos.getX();
            int y = fromPos.getY() - pos.getY();
            int z = fromPos.getZ() - pos.getZ();
            if (x*x+y*y+z*z != 1)
                return; // the change is not send from neighbor block!!!

            EnumFacing side = EnumFacing.getFacingFromVector(x, y, z);

            if (wireTile.hasBranch(side, null) && !world.isSideSolid(fromPos, side.getOpposite())) {
                // The opposite side is no longer solid

                // Drop wires as items
                LinkedList<ItemStack> drops = new LinkedList<>();
                wireTile.removeBranch(side, null, drops);
                for (ItemStack stack: drops)
                    Utils.dropItemIntoWorld(world, pos, stack);

                for (EnumFacing wire_side: EnumFacing.VALUES) {
                    if (wireTile.hasBranch(wire_side, null))
                        return;
                }

                // All branches have been removed, set the wire block to air.
                world.setBlockToAir(pos);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericWire))
            return;

        ISEGenericWire wireTile = (ISEGenericWire) te;
        wireTile.addBranch(nextPlacedSide.get(), nextPlacedto.get(), stack);
        wireTile.onRenderingUpdateRequested();
    }

    ///////////////////////
    /// Item drops
    ///////////////////////
    ThreadLocal<List<ItemStack>> itemDrops = new ThreadLocal<>();
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player,
                                   boolean willHarvest) {
        if (world.isRemote) {
            return false;
        }

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericWire))
            return super.removedByPlayer(state, world, pos, player, willHarvest);

        ISEGenericWire wireTile = (ISEGenericWire) te;

        RayTraceResult trace = this.rayTrace(world, pos, player);
        if (trace == null)
            return super.removedByPlayer(state, world, pos, player, willHarvest);

        if (subHit_isBranch(trace.subHit)) {
            // Remove cable branch
            LinkedList<ItemStack> drops = new LinkedList<>();
            boolean isCorner = subHit_isCorner(trace.subHit);
            EnumFacing wire_side = subHit_side(trace.subHit);
            EnumFacing to = subHit_branch(trace.subHit);
            wireTile.removeBranch(wire_side, to, drops);

            if (isCorner && wireTile.hasBranch(to, wire_side)) {
                // Corner removed, so remove neighbor branches
                wireTile.removeBranch(to, wire_side, drops);
            }

            if (!player.isCreative())
                for (ItemStack stack: drops)
                    Utils.dropItemIntoWorld(world, pos, stack);

            for (EnumFacing side: EnumFacing.VALUES) {
                if (wireTile.hasBranch(side, null))
                        return false;
            }

            return super.removedByPlayer(state, world, pos, player, willHarvest);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    public int quantityDropped(Random random) {
        return 0;
    }
}
