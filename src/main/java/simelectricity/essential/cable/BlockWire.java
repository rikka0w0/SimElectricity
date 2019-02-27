package simelectricity.essential.cable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.RayTraceHelper;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.MetaBlock;
import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.properties.UnlistedPropertyRef;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEWireTile;
import simelectricity.essential.api.ISEChunkWatchSensitiveTile;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.ISEGenericWire;
import simelectricity.essential.utils.SEUnitHelper;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BlockWire extends BlockBase implements ISimpleTexture {
    ///////////////////////////////
    /// Wire Properties
    ///////////////////////////////
    public BlockWire() {
        this("essential_wire", Material.GLASS, ItemBlockBase.class,
                new String[]{"copper_thin", "copper_medium"},
                new float[]{0.22F, 0.32F},
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

    ///////////////////////////////
    ///Block Properties
    ///////////////////////////////
    public final float[] thickness;
    public final float[] resistances;
    private final Class<? extends TileWire> tileEntityClass;
    protected BlockWire(String name, Material material, Class<? extends ItemBlockBase> itemBlockClass,
                         String[] cableTypes, float[] thicknessList, float[] resistanceList, Class<? extends TileWire> tileEntityClass) {
        super(name, material, itemBlockClass);
        thickness = thicknessList;
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
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
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
    public AxisAlignedBB getBranchBoundingBox(EnumFacing side, EnumFacing branch, float thickness) {
        float min = 0.5F - thickness / 2F;
        float max = 0.5F + thickness / 2F;

        float x = 0, y = 0, z = 0;

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

        return (branch == null) ?
                new AxisAlignedBB(min+x, min+y, min+z, max+x, max+y, max+z) :
                RayTraceHelper.createAABB(branch, min, 0, min, max, min, max).offset(x,y,z);
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
            float thickness = wireTile.getWireThickness(wire_side);
            boolean hasConnection = false;

            // Branches
            for (EnumFacing to : EnumFacing.VALUES) {
                if (wireTile.connectedOnSide(wire_side, to)) {
                    hasConnection = true;
                    best = RayTraceHelper.computeTrace(best, pos, start, end,
                            getBranchBoundingBox(wire_side, to, thickness),
                            (wire_side.ordinal() << 4) | to.ordinal());
                }
            }

            // Center
            if (hasConnection)
                best = RayTraceHelper.computeTrace(best, pos, start, end, getBranchBoundingBox(wire_side,null, thickness), (wire_side.ordinal() << 4) | 7);
        }


        //CoverPanel
//        for (EnumFacing side : EnumFacing.VALUES) {
//            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
//            if (coverPanel != null) {
//                best = RayTraceHelper.computeTrace(best, pos, start, end, coverPanelBoundingBoxes[side.ordinal()], side.ordinal() + 1 + 6);
//            }
//        }

        //if (best == null) {
        //    return RayTraceHelper.computeTrace(null, pos, start, end, Block.FULL_BLOCK_AABB, 400);
        //}

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
            float thickness = wireTile.getWireThickness(wire_side);
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
            if (wireTile.connectedOnSide(wire_side, EnumFacing.DOWN)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, y, min+z, max+x, max+y, max+z));
            }

            if (wireTile.connectedOnSide(wire_side, EnumFacing.UP)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, min+y, min+z, max+x, 1+y, max+z));
            }

            if (wireTile.connectedOnSide(wire_side, EnumFacing.NORTH)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, min+y, z, max+x, max+y, max+z));
            }

            if (wireTile.connectedOnSide(wire_side, EnumFacing.SOUTH)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min+x, min+y, min+z, max+x, max+y, 1+z));
            }

            if (wireTile.connectedOnSide(wire_side, EnumFacing.WEST)) {
                hasConnection = true;
                Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(x, min+y, min+z, max+x, max+y, max+z));
            }

            if (wireTile.connectedOnSide(wire_side, EnumFacing.EAST)) {
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
        if (trace.subHit > 256) {    //CoverPanel
            // aabb = coverPanelBoundingBoxes[trace.subHit - 7].offset(pos).expand(0.01, 0.01, 0.01);
        } else if (trace.subHit > -1 && trace.subHit < 256) {    //Center or branches
            EnumFacing wire_side = EnumFacing.VALUES[(trace.subHit>>4) & 0x0F];
            int to_int = trace.subHit & 0x0F;
            EnumFacing to = to_int > EnumFacing.VALUES.length ? null : EnumFacing.VALUES[to_int];
            aabb = getBranchBoundingBox(wire_side, to, wireTile.getWireThickness(wire_side)).offset(pos).expand(0.01, 0.01, 0.01);
        }

        return aabb;
    }

    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISEChunkWatchSensitiveTile)
            ((ISEChunkWatchSensitiveTile) te).onRenderingUpdateRequested();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISEChunkWatchSensitiveTile)
            ((ISEChunkWatchSensitiveTile) te).onRenderingUpdateRequested();
    }

    ///////////////////////
    /// Item drops
    ///////////////////////
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

        if (trace.subHit > -1 && trace.subHit < 256) {
            // Remove cable branch
            EnumFacing wire_side = EnumFacing.VALUES[(trace.subHit>>4) & 0x0F];
            int to_int = trace.subHit & 0x0F;
            EnumFacing to = to_int > EnumFacing.VALUES.length ? null : EnumFacing.VALUES[to_int];
            wireTile.removeBranch(wire_side, to);

            for (EnumFacing side: EnumFacing.VALUES) {
                for (EnumFacing branch: EnumFacing.VALUES) {
                    if (wireTile.connectedOnSide(side, branch))
                        return false;
                }
            }

            return super.removedByPlayer(state, world, pos, player, willHarvest);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
}
