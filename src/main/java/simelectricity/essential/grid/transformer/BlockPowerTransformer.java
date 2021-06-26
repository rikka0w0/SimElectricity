package simelectricity.essential.grid.transformer;

import net.minecraft.block.material.Material;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
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
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;

public class BlockPowerTransformer extends BlockAbstractTransformer implements IMetaProvider<ITileMeta>, ISEHVCableConnector {
    private static MultiBlockStructure blueprint;
    private static Object[][][] collisionBoxes;
	public final EnumPowerTransformerBlockType blockType;
    private BlockPowerTransformer(EnumPowerTransformerBlockType type) {
        super("transformer_35kv_10kv_" + type.getString(), Material.IRON, type.formed ? null : SEAPI.SETab);
        this.blockType = type;
    }
	
    public static BlockPowerTransformer[] create() {
    	BlockPowerTransformer[] ret = new BlockPowerTransformer[EnumPowerTransformerBlockType.values().length];
    	for (EnumPowerTransformerBlockType type: EnumPowerTransformerBlockType.values()) {
    		ret[type.ordinal()] = new BlockPowerTransformer(type);
    	}
    	return ret;    	
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
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(BlockState state) {return this.meta().teCls() != null;}

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (!blockType.formed)
            return null;
    	
    	try {
			return blockType.teCls().getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    private static BlockState stateFromType(EnumPowerTransformerBlockType type) {
    	BlockPowerTransformer block = BlockRegistry.powerTransformer[type.ordinal()];
    	return block.getDefaultState();
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
        
    	Object f = VoxelShapes.fullCube();
    	Object O = VoxelShapes.empty();
    	
    	AxisAlignedBB w = new AxisAlignedBB(0, 0, 0.2, 0.8, 1, 1);
    	AxisAlignedBB d = new AxisAlignedBB(0, 0, 0, 0.8, 1, 0.8);
		AxisAlignedBB a = new AxisAlignedBB(0.2, 0, 0, 1, 1, 0.8);
		AxisAlignedBB q = new AxisAlignedBB(0.2, 0, 0.2, 1, 1, 1);
		AxisAlignedBB l = new AxisAlignedBB(0.2, 0, 0, 1, 1, 1);
		AxisAlignedBB r = new AxisAlignedBB(0, 0, 0, 0.8, 1, 1);
		
		AxisAlignedBB t = new AxisAlignedBB(0, 0.9, 0, 1, 1, 1);
		AxisAlignedBB[] wt = new AxisAlignedBB[] {t, w};
		AxisAlignedBB[] dt = new AxisAlignedBB[] {t, d};
		AxisAlignedBB[] at = new AxisAlignedBB[] {t, a};
		AxisAlignedBB[] qt = new AxisAlignedBB[] {t, q};
		AxisAlignedBB[] lt = new AxisAlignedBB[] {t, l};
		AxisAlignedBB[] rt = new AxisAlignedBB[] {t, r};
		
		VoxelShape b = VoxelShapes.create(0, 0.4, 0, 1, 1, 1);
		VoxelShape p = VoxelShapes.create(0.425, 0, 0.425, 0.575, 1, 0.575);
		AxisAlignedBB[] p2 = new AxisAlignedBB[] {
				new AxisAlignedBB(0.425, 0, 0.425, 0.575, 0.5, 0.575),
				new AxisAlignedBB(0, 0.425, 0.425, 0.575, 0.575, 0.575),
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
    public ISEGridTile getGridTile(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);

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
	public BlockRenderType getRenderType(BlockState state) {
		return blockType.formed ? BlockRenderType.INVISIBLE : BlockRenderType.MODEL;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		if (!this.blockType.formed)
			return VoxelShapes.fullCube();
		
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof IMultiBlockTile))
			return VoxelShapes.empty();
		
		MultiBlockTileInfo mbInfo = ((IMultiBlockTile) te).getMultiBlockTileInfo();
		if (mbInfo == null)
			return VoxelShapes.empty();
		
		Object collBoxes = mbInfo.lookup(collisionBoxes);
		if (collBoxes instanceof VoxelShape)
			return (VoxelShape) collBoxes;
		else if (collBoxes instanceof AxisAlignedBB)
			return VoxelShapes.create(blueprint.createAABB(mbInfo, (AxisAlignedBB) collBoxes));
		else if (collBoxes instanceof AxisAlignedBB[]) {
			VoxelShape ret = VoxelShapes.empty();
			for (AxisAlignedBB part: (AxisAlignedBB[]) collBoxes) {
				ret = VoxelShapes.or(ret, 
						VoxelShapes.create(blueprint.createAABB(mbInfo, part)));
			}
			return ret;
		}

		return VoxelShapes.fullCube();
	}
}
