package simelectricity.essential.cable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.RayTraceHelper;
import rikka.librikka.block.MetaBlock;
import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.item.ItemBlockBase;
import rikka.librikka.properties.UnlistedPropertyRef;
import simelectricity.api.SEAPI;
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISECoverPanelHost;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.SEEAPI;
import simelectricity.essential.api.coverpanel.ISECoverPanel;
import simelectricity.essential.api.coverpanel.ISEGuiCoverPanel;
import simelectricity.essential.api.coverpanel.ISERedstoneEmitterCoverPanel;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * RayTrace is inspired by BuildCraft
 *
 * @author Rikka0_0
 */
public class BlockCable extends MetaBlock implements ITileEntityProvider, ISimpleTexture {
    ///////////////////////////////
    /// Cable Properties
    ///////////////////////////////
    public final float[] thickness;
    public final float[] resistances;
    private final Class<? extends TileCable> tileEntityClass;
    public BlockCable() {
        this("essential_cable", Material.GLASS, ItemBlockBase.class,
                new String[]{"copper_thin", "copper_medium", "copper_thick"},
                new float[]{0.22F, 0.32F, 0.42F},
                new float[]{0.1F, 0.01F, 0.001F},
                TileCable.class);
		setHardness(0.2F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }
        
    @Override
    @SideOnly(Side.CLIENT)
    public String getIconName(int damage) {
        return "essential_cable_" + getSubBlockUnlocalizedNames()[damage] + "_inventory";
    }
    ///////////////////////////////
    ///Block Properties
    ///////////////////////////////
    protected BlockCable(String name, Material material, Class<? extends ItemBlockBase> itemBlockClass,
                         String[] cableTypes, float[] thicknessList, float[] resistanceList, Class<? extends TileCable> tileEntityClass) {
        super(name, cableTypes, material, itemBlockClass);
		thickness = thicknessList;
		resistances = resistanceList;
        this.tileEntityClass = tileEntityClass;
        
        //Calc. collision boxes and cache them
        this.cableBoundingBoxes = new AxisAlignedBB[thicknessList.length][7];
        for (int i=0; i<thicknessList.length; i++) {
            float min = 0.5F - thicknessList[i] / 2F;
            float max = 0.5F + thicknessList[i] / 2F;

            for (EnumFacing side: EnumFacing.VALUES) {
            	cableBoundingBoxes[i][side.ordinal()] = RayTraceHelper.createAABB(side, min, 0, min, max, min, max);
            }
            
            cableBoundingBoxes[i][6] = new AxisAlignedBB(min, min, min, max, max, max);
        }
    }

    @Override
    public void beforeRegister() {
		isBlockContainer = true;
		setCreativeTab(SEAPI.SETab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        TileCable cable;
        try {
            cable = tileEntityClass.getConstructor().newInstance();
            if (!world.isRemote)    //createNewTileEntity is only called by server when the block is firstly placed
                cable.setResistanceOnPlace(this.resistances[meta]);
            return cable;
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

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);

        return tile instanceof ISEGenericCable && ((ISEGenericCable) tile).getCoverPanelOnSide(side) != null;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileCable) {
            return ((TileCable) te).lightLevel;
        }
        return 0;
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
    protected void createProperties(ArrayList<IProperty> properties, ArrayList<IUnlistedProperty> unlisted) {
        super.createProperties(properties, unlisted);
        unlisted.add(UnlistedPropertyRef.propertyTile);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState retval = (IExtendedBlockState) state;

            TileEntity te = world.getTileEntity(pos);

            if (te instanceof ISEGenericCable) {
                retval = retval.withProperty(UnlistedPropertyRef.propertyTile, new WeakReference<>(te));
            }

            return retval;
        }
        return state;
    }
    
    //////////////////////////////////
    ///CollisionBoxes
    //////////////////////////////////
    protected final static AxisAlignedBB[] coverPanelBoundingBoxes;
    
    static {
    	coverPanelBoundingBoxes = new AxisAlignedBB[6];
    	for (EnumFacing side: EnumFacing.VALUES){
    		coverPanelBoundingBoxes[side.ordinal()] = RayTraceHelper.createAABB(side, 0, 0, 0, 1, ISECoverPanel.thickness, 1);
    	}
    }
    
    //Meta, side
    protected final AxisAlignedBB[][] cableBoundingBoxes;
    //Custom ray trace
    protected AxisAlignedBB getCableBoundingBox(EnumFacing side, int meta) {
    	return (side==null) ?
    			cableBoundingBoxes[meta][6]:	//Center
    			cableBoundingBoxes[meta][side.ordinal()];
    }
    
    @Override
    @Nullable
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        return this.rayTrace(world, pos, start, end);
    }

    @Nullable
    public RayTraceResult rayTrace(World world, BlockPos pos, EntityPlayer player) {
        Vec3d start = player.getPositionVector().addVector(0, player.getEyeHeight(), 0);
        double reachDistance = 5;
        if (player instanceof EntityPlayerMP)
            reachDistance = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();

        Vec3d end = start.add(player.getLookVec().normalize().scale(reachDistance));
        return this.rayTrace(world, pos, start, end);
    }

    @Nullable
    public RayTraceResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof ISEGenericCable))
            return RayTraceHelper.computeTrace(null, pos, start, end, Block.FULL_BLOCK_AABB, 400);

        ISEGenericCable cable = (ISEGenericCable) tile;
        int meta = world.getBlockState(pos).getValue(this.propertyMeta);

        RayTraceResult best = null;
        //Cable center & branches
        //Start form center
        best = RayTraceHelper.computeTrace(best, pos, start, end, getCableBoundingBox(null, meta), 0);
        for (EnumFacing side : EnumFacing.VALUES) {
            if (cable.connectedOnSide(side))
                best = RayTraceHelper.computeTrace(best, pos, start, end, getCableBoundingBox(side, meta), side.ordinal() + 1);
        }

        //CoverPanel
        for (EnumFacing side : EnumFacing.VALUES) {
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
            if (coverPanel != null) {
                best = RayTraceHelper.computeTrace(best, pos, start, end, coverPanelBoundingBoxes[side.ordinal()], side.ordinal() + 1 + 6);
            }
        }

        //if (best == null) {
        //    return RayTraceHelper.computeTrace(null, pos, start, end, Block.FULL_BLOCK_AABB, 400);
        //}

        //subhit: 0: center, 123456 branches, 789 10 11 12 coverpanel 
        return best;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB axisAlignedBB,
                                      List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isPistonMoving) {

        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof ISEGenericCable))
            return;

        int meta = state.getBlock().getMetaFromState(state);
        ISEGenericCable cable = (ISEGenericCable) te;

        double min = 0.5 - this.thickness[meta] / 2;
        double max = 0.5 + this.thickness[meta] / 2;

        //Center
		Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min, min, min, max, max, max));

        //Branches
        if (cable.connectedOnSide(EnumFacing.DOWN))
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min, 0, min, max, max, max));

        if (cable.connectedOnSide(EnumFacing.UP))
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min, min, min, max, 1, max));

        if (cable.connectedOnSide(EnumFacing.NORTH))
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min, min, 0, max, max, max));

        if (cable.connectedOnSide(EnumFacing.SOUTH))
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min, min, min, max, max, 1));

        if (cable.connectedOnSide(EnumFacing.WEST))
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(0, min, min, max, max, max));

        if (cable.connectedOnSide(EnumFacing.EAST))
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(min, min, min, 1, max, max));

        //Cover panel
        if (cable.getCoverPanelOnSide(EnumFacing.DOWN) != null)
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(0, 0, 0, 1, ISECoverPanel.thickness, 1));

        if (cable.getCoverPanelOnSide(EnumFacing.UP) != null)
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(0, 1 - ISECoverPanel.thickness, 0, 1, 1, 1));

        if (cable.getCoverPanelOnSide(EnumFacing.NORTH) != null)
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(0, 0, 0, 1, 1, ISECoverPanel.thickness));

        if (cable.getCoverPanelOnSide(EnumFacing.SOUTH) != null)
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(0, 0, 1 - ISECoverPanel.thickness, 1, 1, 1));

        if (cable.getCoverPanelOnSide(EnumFacing.WEST) != null)
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(0, 0, 0, ISECoverPanel.thickness, 1, 1));

        if (cable.getCoverPanelOnSide(EnumFacing.EAST) != null)
			Block.addCollisionBoxToList(pos, axisAlignedBB, collidingBoxes, new AxisAlignedBB(1 - ISECoverPanel.thickness, 0, 0, 1, 1, 1));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return Block.FULL_BLOCK_AABB;        //This should never happen but just in case

        RayTraceResult trace = Minecraft.getMinecraft().objectMouseOver;    //Not sure what this does!
        //trace = rayTrace(world, pos, Minecraft.getMinecraft().player);	//Was
        if (trace == null || trace.subHit < 0 || !pos.equals(trace.getBlockPos())) {
            // Perhaps we aren't the object the mouse is over
            return Block.FULL_BLOCK_AABB;
        }

        if (trace.subHit > 6 && trace.subHit < 13) {    //CoverPanel
            return coverPanelBoundingBoxes[trace.subHit - 7].offset(pos).expand(0.01, 0.01, 0.01);
        } else if (trace.subHit > -1 && trace.subHit < 7) {    //Center or branches
            int meta = state.getValue(this.propertyMeta);
            ISEGenericCable cable = (ISEGenericCable) te;

            double x1, y1, z1, x2, y2, z2;
            x1 = 0.5 - this.thickness[meta] / 2;
            y1 = x1;
            z1 = x1;
            x2 = 0.5 + this.thickness[meta] / 2;
            y2 = x2;
            z2 = x2;

            if (cable.connectedOnSide(EnumFacing.DOWN))
                y1 = 0;

            if (cable.connectedOnSide(EnumFacing.UP))
                y2 = 1;

            if (cable.connectedOnSide(EnumFacing.NORTH))
                z1 = 0;

            if (cable.connectedOnSide(EnumFacing.SOUTH))
                z2 = 1;

            if (cable.connectedOnSide(EnumFacing.WEST))
                x1 = 0;

            if (cable.connectedOnSide(EnumFacing.EAST))
                x2 = 1;

            return new AxisAlignedBB(x1, y1, z1, x2, y2, z2).offset(pos).expand(0.01, 0.01, 0.01);
        }

        return null;
    }


    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    private boolean attemptOpenCoverPanelGui(World world, BlockPos pos, EntityPlayer player) {
        if (player.isSneaking())
            return false;

        RayTraceResult trace = this.rayTrace(world, pos, player);


        if (trace == null)
            return false;    //This is not suppose to happen, but just in case!

        if (trace.subHit < 7)
            return false;    //The player is looking at the cable

        if (trace.subHit > 12)
            return false;    //The player is looking at somewhere else

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISECoverPanelHost) {
            ISECoverPanelHost host = (ISECoverPanelHost) te;
            EnumFacing panelSide = EnumFacing.getFront(trace.subHit - 7);
            ISECoverPanel coverPanel = host.getCoverPanelOnSide(panelSide);

            if (coverPanel instanceof ISEGuiCoverPanel) {
                player.openGui(Essential.instance, panelSide.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);

        if (!(te instanceof ISEGenericCable))
            return false;        //Normally this could not happen, but just in case!

        ItemStack itemStack = player.getHeldItemMainhand();
        if (itemStack == null)
            return this.attemptOpenCoverPanelGui(world, pos, player);

        if (itemStack.isEmpty())
            return this.attemptOpenCoverPanelGui(world, pos, player);

        ISEGenericCable cable = (ISEGenericCable) te;

        ISECoverPanel coverPanel = SEEAPI.coverPanelRegistry.fromItemStack(itemStack);
        if (coverPanel == null)
            return this.attemptOpenCoverPanelGui(world, pos, player);

        //Attempt to install cover panel
        if (cable.canInstallCoverPanelOnSide(side, coverPanel)) {
            if (!player.capabilities.isCreativeMode) {
                itemStack.shrink(1);
            }

            if (!world.isRemote) {    //Handle on server side
                cable.installCoverPanel(side, coverPanel);

                if (!coverPanel.isHollow())
                    world.neighborChanged(pos.offset(side), this, pos);
            }
            return true;
        }

        return this.attemptOpenCoverPanelGui(world, pos, player);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return;        //Normally this could not happen, but just in case!

        ISEGenericCable cable = (ISEGenericCable) te;
        cable.onCableRenderingUpdateRequested();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return;        //Normally this could not happen, but just in case!

        ISEGenericCable cable = (ISEGenericCable) te;
        cable.onCableRenderingUpdateRequested();
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
        if (!(te instanceof ISEGenericCable))
            return super.removedByPlayer(state, world, pos, player, willHarvest);

        ISEGenericCable cable = (ISEGenericCable) te;

        RayTraceResult trace = this.rayTrace(world, pos, player);
        if (trace == null)
            return super.removedByPlayer(state, world, pos, player, willHarvest);

        if (trace.subHit > 6 && trace.subHit < 13) {
            //Remove the selected cover panel
            EnumFacing side = EnumFacing.getFront(trace.subHit - 7);
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
            cable.removeCoverPanel(coverPanel, !player.capabilities.isCreativeMode);
            return false;
        } else {
            for (EnumFacing side : EnumFacing.VALUES) {
                ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
                cable.removeCoverPanel(coverPanel, !player.capabilities.isCreativeMode);
            }

            return super.removedByPlayer(state, world, pos, player, willHarvest);
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ISEGenericCable))
            return ItemStack.EMPTY;

        ISEGenericCable cable = (ISEGenericCable) te;

        RayTraceResult trace = this.rayTrace(world, pos, player);

        if (trace.subHit > 6 && trace.subHit < 13) {
            EnumFacing side = EnumFacing.getFront(trace.subHit - 7);
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side);
            return coverPanel.getDroppedItemStack();
        } else {
            return new ItemStack(Item.getItemFromBlock(this), 1, damageDropped(state));
        }
    }

    ///////////////////////
    ///Redstone
    ///////////////////////
    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof ISEGenericCable) {
            ISEGenericCable cable = (ISEGenericCable) te;
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side.getOpposite());

            return coverPanel instanceof ISERedstoneEmitterCoverPanel;
        }

        return false;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof ISEGenericCable) {
            ISEGenericCable cable = (ISEGenericCable) te;
            ISECoverPanel coverPanel = cable.getCoverPanelOnSide(side.getOpposite());

            return coverPanel instanceof ISERedstoneEmitterCoverPanel
                    ? ((ISERedstoneEmitterCoverPanel) coverPanel).isProvidingWeakPower() ? 15 : 0
                    : 0;
        }

        return 0;
    }
}
