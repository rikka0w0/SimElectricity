package simelectricity.essential.grid.transformer;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import rikka.librikka.IMetaProvider;
import rikka.librikka.ITileMeta;
import rikka.librikka.multiblock.BlockMapping;
import rikka.librikka.multiblock.IMultiBlockTile;
import rikka.librikka.multiblock.MultiBlockStructure;
import rikka.librikka.multiblock.MultiBlockTileInfo;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.BlockRegistry;
import simelectricity.essential.api.ISEHVCableConnector;

public class BlockPowerTransformer extends BlockAbstractTransformer implements IMetaProvider<ITileMeta>, ISEHVCableConnector {
    private static MultiBlockStructure blueprint;
    private static Object[][][] collisionBoxes;
	public final EnumPowerTransformerBlockType blockType;
    private BlockPowerTransformer(EnumPowerTransformerBlockType type) {
        super("transformer_35kv_10kv_" + type.getSerializedName(), Material.METAL, type.formed ? null : SEAPI.SETab);
        this.blockType = type;
    }

    public static BlockPowerTransformer[] create() {
    	BlockPowerTransformer[] ret = new BlockPowerTransformer[EnumPowerTransformerBlockType.values().length];
    	for (EnumPowerTransformerBlockType type: EnumPowerTransformerBlockType.rawStructure) {
    		ret[type.ordinal()] = new RawStructureBlock(type);
    	}
    	for (EnumPowerTransformerBlockType type: EnumPowerTransformerBlockType.rawStructure) {
    		ret[type.ordinal()] = new FormedStructureBlock(type);
    	}
    	return ret;
    }

    private static class RawStructureBlock extends BlockPowerTransformer {
    	private RawStructureBlock(EnumPowerTransformerBlockType type) {
    		super(type);
    	}
    }

    private static class FormedStructureBlock extends BlockPowerTransformer implements EntityBlock {
    	private FormedStructureBlock(EnumPowerTransformerBlockType type) {
    		super(type);
    	}

        ///////////////////////////////
        /// BlockEntity
        ///////////////////////////////
		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
	        if (!blockType.formed)
	            return null;

	    	try {
				return blockType.teCls().getConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
    }

	@Override
	public ITileMeta meta() {
		return this.blockType;
	}

	@Override
	protected MultiBlockStructure getBlueprint() {
		return blueprint;
	}

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    private static BlockState stateFromType(EnumPowerTransformerBlockType type) {
    	BlockPowerTransformer block = BlockRegistry.powerTransformer[type.ordinal()];
    	return block.defaultBlockState();
    }

	@Override
	protected ItemStack getItemToDrop(BlockState state) {
        if (blockType.formed)
        	return ItemStack.EMPTY;

        return new ItemStack(this);
	}

    public static void createBluePrint() {
        //y,z,x facing NORTH(Z-), do not change
		BlockMapping[][][] configuration = new BlockMapping[5][][];

        BlockMapping core2PH = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.IronCore), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        BlockMapping coil2PH = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.Winding), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        BlockMapping support2PH = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.OilTankSupport), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        BlockMapping pipe2PH = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.OilPipe), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        BlockMapping tank2PH = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.OilTank), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        BlockMapping casing2PH = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.Placeholder));
        BlockMapping casing2PHpri = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.PlaceholderPrimary));
        BlockMapping casing2PHsec = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.PlaceholderSecondary));
        BlockMapping casing2pri = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.Primary));
        BlockMapping casing2sec = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.Secondary));
        BlockMapping casing2render = new BlockMapping(stateFromType(EnumPowerTransformerBlockType.Casing), stateFromType(EnumPowerTransformerBlockType.Render));


        //  .-->x+ (East)
        //  |                           Facing/Looking at North(x-)
        // \|/
        //  z+ (South)
        configuration[0] = new BlockMapping[][]{
                {null, casing2PHpri, casing2PHpri, null, casing2PHpri, casing2PHpri, null},
                {casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri},
                {casing2PH, casing2PH, casing2PH, casing2PH, casing2PH, casing2PH, casing2PH},
                {casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec},
                {null, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, null}
        };

        configuration[1] = new BlockMapping[][]{
                {null, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, casing2PHpri, null},
                {casing2PHpri, coil2PH, coil2PH, coil2PH, coil2PH, coil2PH, casing2PHpri},
                {casing2PH, coil2PH, core2PH, core2PH, core2PH, coil2PH, casing2PH},
                {casing2PHsec, coil2PH, coil2PH, coil2PH, coil2PH, coil2PH, casing2PHsec},
                {null, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, null}
        };

        configuration[2] = new BlockMapping[][]{
                {null, casing2PHpri, casing2PHpri, null, casing2PHpri, casing2PHpri, null},
                {casing2PHpri, casing2PHpri, casing2PHpri, casing2pri, casing2PHpri, casing2PHpri, casing2PHpri},
                {casing2PH, casing2PH, casing2PH, casing2render, casing2PH, casing2PH, casing2PH},
                {casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2sec, casing2PHsec, casing2PHsec},
                {support2PH, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, casing2PHsec, null}
        };

        configuration[3] = new BlockMapping[][]{
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {support2PH, null, null, null, null, null, null},
                {null, pipe2PH, null, null, null, null, null},
                {support2PH, null, null, null, null, null, null}
        };

        configuration[4] = new BlockMapping[][]{
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {tank2PH, null, null, null, null, null, null},
                {tank2PH, pipe2PH, null, null, null, null, null},
                {tank2PH, null, null, null, null, null, null}
        };

        blueprint = new MultiBlockStructure(configuration);

    	Object f = Shapes.block();
    	Object O = Shapes.empty();

    	AABB w = new AABB(0, 0, 0.2, 0.8, 1, 1);
    	AABB d = new AABB(0, 0, 0, 0.8, 1, 0.8);
		AABB a = new AABB(0.2, 0, 0, 1, 1, 0.8);
		AABB q = new AABB(0.2, 0, 0.2, 1, 1, 1);
		AABB l = new AABB(0.2, 0, 0, 1, 1, 1);
		AABB r = new AABB(0, 0, 0, 0.8, 1, 1);

		AABB t = new AABB(0, 0.9, 0, 1, 1, 1);
		AABB[] wt = new AABB[] {t, w};
		AABB[] dt = new AABB[] {t, d};
		AABB[] at = new AABB[] {t, a};
		AABB[] qt = new AABB[] {t, q};
		AABB[] lt = new AABB[] {t, l};
		AABB[] rt = new AABB[] {t, r};

		VoxelShape b = Shapes.box(0, 0.4, 0, 1, 1, 1);
		VoxelShape p = Shapes.box(0.425, 0, 0.425, 0.575, 1, 0.575);
		AABB[] p2 = new AABB[] {
				new AABB(0.425, 0, 0.425, 0.575, 0.5, 0.575),
				new AABB(0, 0.425, 0.425, 0.575, 0.575, 0.575),
		};

        collisionBoxes = new Object[configuration.length][][];
        collisionBoxes[0] = new Object[][]{
            {O, b, b, O, b, b, O},
            {q, f, f, f, f, f, w},
            {l, f, f, f, f, f, r},
            {a, f, f, f, f, f, d},
            {O, b, b, b, b, b, O}
	    };

	    collisionBoxes[1] = new Object[][]{
	        {O, f, f, f, f, f, O},
	        {q, f, f, f, f, f, w},
	        {l, f, f, f, f, f, r},
	        {a, f, f, f, f, f, d},
	        {O, f, f, f, f, f, O}
	    };

	    collisionBoxes[2] = new Object[][]{
	        {O , f, f, O, f, f, O},
	        {qt, f, f, f, f, f, wt},
	        {lt, f, f, f, f, f, rt},
	        {at, f, f, f, f, f, dt},
	        {f , f, f, f, f, f, O}
	    };

	    collisionBoxes[3] = new Object[][]{
	        {O, O, O, O, O, O, O},
	        {O, O, O, O, O, O, O},
	        {f, O, O, O, O, O, O},
	        {O, p, O, O, O, O, O},
	        {f, O, O, O, O, O, O},
	    };

	    collisionBoxes[4] = new Object[][]{
	        {O, O, O, O, O, O, O},
	        {O, O, O, O, O, O, O},
	        {f, O, O, O, O, O, O},
	        {f, p2, O, O, O, O, O},
	        {f, O, O, O, O, O, O},
	    };
    }

    //////////////////////////////////////
    /// ISEHVCableConnector
    //////////////////////////////////////
    @Override
    public ISEGridTile getGridTile(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof TilePowerTransformerPlaceHolder.Primary)
            return ((TilePowerTransformerPlaceHolder.Primary) te).getWinding();
        else if (te instanceof TilePowerTransformerPlaceHolder.Secondary)
            return ((TilePowerTransformerPlaceHolder.Secondary) te).getWinding();
        else if (te instanceof TilePowerTransformerWinding)
            return (TilePowerTransformerWinding) te;

        return null;
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return blockType.formed ? RenderShape.INVISIBLE : RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (!this.blockType.formed)
			return Shapes.block();

		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof IMultiBlockTile))
			return Shapes.empty();

		MultiBlockTileInfo mbInfo = ((IMultiBlockTile) te).getMultiBlockTileInfo();
		if (mbInfo == null)
			return Shapes.empty();

		Object collBoxes = mbInfo.lookup(collisionBoxes);
		if (collBoxes instanceof VoxelShape)
			return (VoxelShape) collBoxes;
		else if (collBoxes instanceof AABB)
			return Shapes.create(blueprint.createAABB(mbInfo, (AABB) collBoxes));
		else if (collBoxes instanceof AABB[]) {
			VoxelShape ret = Shapes.empty();
			for (AABB part: (AABB[]) collBoxes) {
				ret = Shapes.or(ret,
						Shapes.create(blueprint.createAABB(mbInfo, part)));
			}
			return ret;
		}

		return Shapes.block();
	}
}
