/*
 * Copyright (C) 2014 SimElectricity
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package simelectricity.essential.grid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.block.BlockBase;
import rikka.librikka.item.ItemBlockBase;
import simelectricity.api.SEAPI;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.BlockRegistry;

import java.util.LinkedList;

import javax.annotation.Nullable;

public class BlockPowerPoleTop extends BlockBase {
    ///////////////////////////////////////
    /// Utils
    ///////////////////////////////////////
    //facing ID
    private static final int[][] collisionBoxCoordOffsetMatrix = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8},    //facing=0
            {0, 2, 3, 4, 1, 6, 7, 8, 5},    //facing=1
            {0, 3, 4, 1, 2, 7, 8, 5, 6},    //facing=2
            {0, 4, 1, 2, 3, 8, 5, 6, 7}        //facing=3
    };

    ///////////////////
    /// Initialize
    ///////////////////
    public final int type;
    private BlockPowerPoleTop(int type) {
        super("essential_powerpole_" + String.valueOf(type), 
        		Block.Properties.create(Material.ROCK)
        		.hardnessAndResistance(3.0F, 10.0F)
        		.sound(SoundType.METAL)
        		, BlockPowerPoleTop.ItemBlock.class
        		, (new Item.Properties()).group(SEAPI.SETab));
        this.type = type;
    }
    
    public static BlockPowerPoleTop[] create() {
    	return new BlockPowerPoleTop[] {new BlockPowerPoleTop(0), new BlockPowerPoleTop(1)};
    }

    private static BlockInfo createCollisionBoxCoordOffset(int facing, int x, int y, int z, int part) {
        return new BlockInfo(BlockPowerPoleBottom.rotateCoord(facing, x, y, z), 
        		BlockRegistry.powerPoleCollisionBox.forPart(collisionBoxCoordOffsetMatrix[facing][part]));
    }

    public static LinkedList<BlockInfo> getCollisionBoxBlockOffsets(BlockState state) {
        LinkedList<BlockInfo> list = new LinkedList();

        int facing = getFacingInt(state);
        if ((facing & 1) == 0) {    // 90 x n
            facing = facing >> 1;

            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 1, 0, 0, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 2, 0, 0, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 3, 0, 0, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 4, 0, 0, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -1, 0, 0, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -2, 0, 0, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -3, 0, 0, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -4, 0, 0, 0));

            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 0, 0, 1, 3));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 1, 0, 1, 3));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 2, 0, 1, 3));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 3, 0, 1, 3));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 4, 0, 1, 3));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -1, 0, 1, 3));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -2, 0, 1, 3));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -3, 0, 1, 3));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -4, 0, 1, 3));

            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 0, 0, -1, 1));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 1, 0, -1, 1));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 2, 0, -1, 1));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 3, 0, -1, 1));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 4, 0, -1, 1));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -1, 0, -1, 1));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -2, 0, -1, 1));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -3, 0, -1, 1));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -4, 0, -1, 1));

            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 5, 0, 1, 8));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 5, 0, -1, 5));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -5, 0, -1, 6));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -5, 0, 1, 7));
        } else {    //45 x n
            facing = facing >> 1;

            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 4, 0, -3, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 3, 0, -2, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 2, 0, -1, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 1, 0, 0, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 0, 0, 1, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -1, 0, 2, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -2, 0, 3, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -3, 0, 4, 0));


            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 3, 0, -4, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 2, 0, -3, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 1, 0, -2, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 0, 0, -1, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -1, 0, 0, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -2, 0, 1, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -3, 0, 2, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -4, 0, 3, 0));

            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 3, 0, -3, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 2, 0, -2, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, 1, 0, -1, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -1, 0, 1, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -2, 0, 2, 0));
            list.add(BlockPowerPoleTop.createCollisionBoxCoordOffset(facing, -3, 0, 3, 0));
        }

        return list;
    }
    
    ///////////////////////////////
    /// TileEntity
    ///////////////////////////////
	@Override
	public boolean hasTileEntity(BlockState state) {return true;}
	
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TilePowerPole();
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    	return VoxelShapes.create(0.0, 0.0, 0.0, 1, 0.05, 1);
    }

    ///////////////////////////////
    ///BlockStates
    ///////////////////////////////
    @Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    	builder.add(DirHorizontal8.prop);
	}
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	PlayerEntity placer = context.getPlayer();
    	return this.getDefaultState().with(DirHorizontal8.prop, DirHorizontal8.fromSight(placer));
    }
    
    public static int getFacingInt(BlockState blockstate) {
    	return (8 - blockstate.get(DirHorizontal8.prop).ordinal()) & 7;
    }
    
    public static DirHorizontal8 toDir8(int facingId) {
		return DirHorizontal8.values()[(8 - facingId) & 7];
    }
    
    //////////////////////////////////////
    /////Item drops and Block activities
    //////////////////////////////////////
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (world.isRemote)
            return;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ISEGridTile)
            SEAPI.energyNetAgent.attachGridNode(world, SEAPI.energyNetAgent.newGridNode(pos, 3));
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        LinkedList<BlockInfo> list = BlockPowerPoleTop.getCollisionBoxBlockOffsets(state);

        //Attempt to remove any collision box block and bottom(base) block

        for (BlockInfo info : list) {
            BlockPos realPos = info.getRealPos(pos);
            if (world.getBlockState(realPos).getBlock() == BlockRegistry.powerPoleCollisionBox)
                world.removeBlock(realPos, false);
        }

        list = BlockRegistry.powerPoleBottom.getBaseBlockCoordOffsets(state);
        for (BlockInfo info : list) {
            BlockPos realPos = info.getRealPos(pos).add(0, -18, 0);
            if (world.getBlockState(realPos).getBlock() == BlockRegistry.powerPoleBottom)
                world.removeBlock(realPos, false);
        }

        TileEntity te = world.getTileEntity(pos);    //Do this before the tileEntity is removed!
        if (te instanceof ISEGridTile)
            SEAPI.energyNetAgent.detachGridNode(world, ((ISEGridTile) te).getGridNode());

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    ////////////////////////////////////
    /// Rendering
    ////////////////////////////////////
    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    public static class ItemBlock extends ItemBlockBase {
    	public ItemBlock(Block block, Item.Properties props) {
    		super(block, props);
    	}

        @Override
        public boolean placeBlock(BlockItemUseContext context, BlockState newState) {
        	World world = context.getWorld();
        	BlockPos pos = context.getPos();
        	
            //Place center block
            BlockPos centerPos = pos.add(0, 18, 0);

            for (BlockInfo info : BlockPowerPoleTop.getCollisionBoxBlockOffsets(newState)) {
                BlockPos realPos = info.getRealPos(centerPos);
                world.setBlockState(realPos, info.state);
            }

            boolean ret = super.placeBlock(context, newState);

            //Place base blocks
            for (BlockInfo info : BlockRegistry.powerPoleBottom.getBaseBlockCoordOffsets(newState)) {
                BlockPos realPos = info.getRealPos(pos);
                world.setBlockState(realPos, info.state);
            }

            return ret;
        }
    }
}
