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

package simElectricity.Common.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simElectricity.API.Common.Blocks.AutoFacing;
import simElectricity.API.Common.Blocks.BlockContainerSE;
import simElectricity.API.Energy;
import simElectricity.API.EnergyTile.IConductor;
import simElectricity.API.Network;
import simElectricity.Common.Blocks.TileEntity.TileWire;
import simElectricity.Common.Items.ItemBlocks.ItemBlockWire;

import java.util.List;

public class BlockWire extends BlockContainerSE {

    public static final PropertyEnum VARIANT = PropertyEnum.create("variant", WireType.class);
    public static final String[] subNames = {WireType.THIN.getName(), WireType.MEDIUM.getName(), WireType.THICK.getName()};
    public static final float[] resistanceList = {0.27F, 0.09F, 0.03F};
    public static final float[] collisionWidthList = {0.12F, 0.22F, 0.32F};
    public static final float[] renderingWidthList = {0.1F, 0.2F, 0.3F};


    //Initialize Block
    public BlockWire() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, WireType.THIN));
        setHardness(0.2F);
        setUnlocalizedName("wire");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking())
            return false;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileWire))
            return false;

        TileWire wire = (TileWire) tileEntity;

        if (player.getCurrentEquippedItem() != null) {
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack.getItem() == Items.dye) {
                if (!world.isRemote) {
                    wire.color = stack.getItemDamage() + 1;           //Set the color
                    Energy.postTileRejoinEvent(tileEntity);           //Reconnect the wire to the energy network
                    Network.updateTileEntityFields(tileEntity, "color");  //Update the field color to every client within the dimension
                    onBlockPlacedBy(world, pos, state, player, null);    //Update rests to clients
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!world.isRemote) {
            TileWire te = (TileWire) world.getTileEntity(pos);
            te.needsUpdate = true;
        }
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote)
            return;

        TileWire te = (TileWire) world.getTileEntity(pos);
        te.needsUpdate = true;
        updateRenderSides(te);

        for (EnumFacing direction : AutoFacing.VALID_DIRECTIONS) { //Update neighbors
            updateRenderSides(world.getTileEntity(pos.add(direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ())));
        }
    }

    void updateRenderSides(TileEntity te) {
        if (te instanceof IConductor) {
            Network.updateNetworkFields(te);
        }
    }

    // Rendering
    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(1F, 0.6F, 1F, 0.0F, 0.6F, 0.0F);
    }

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisAlignedBB, List list, Entity entity) {
        super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);

        if (!(world.getTileEntity(pos) instanceof TileWire))
            return;
        TileWire wire = (TileWire) world.getTileEntity(pos);

        float WIDTH = collisionWidthList[world.getBlockState(pos).getBlock().getMetaFromState(state)];

        float minPos = 0.5F - WIDTH, maxPos = 0.5F + WIDTH;

        if (wire.isConnected(EnumFacing.WEST)) {
            setBlockBounds(0F, minPos, minPos, maxPos, maxPos, maxPos);
            super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);
        }

        if (wire.isConnected(EnumFacing.EAST)) {
            setBlockBounds(minPos, minPos, minPos, 1F, maxPos, maxPos);
            super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);
        }

        if (wire.isConnected(EnumFacing.NORTH)) {
            setBlockBounds(minPos, minPos, 0F, maxPos, maxPos, maxPos);
            super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);
        }

        if (wire.isConnected(EnumFacing.SOUTH)) {
            setBlockBounds(minPos, minPos, minPos, maxPos, maxPos, 1F);
            super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);
        }

        if (wire.isConnected(EnumFacing.UP)) {
            setBlockBounds(minPos, minPos, minPos, maxPos, 1F, maxPos);
            super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);
        }

        if (wire.isConnected(EnumFacing.DOWN)) {
            setBlockBounds(minPos, 0F, minPos, maxPos, maxPos, maxPos);
            super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);
        }
    }


    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        if (!(world.getTileEntity(pos) instanceof TileWire))
            return;
        float WIDTH = collisionWidthList[world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))];

        TileWire wire = (TileWire) world.getTileEntity(pos);

        minX = 0.5 - WIDTH;
        minY = 0.5 - WIDTH;
        minZ = 0.5 - WIDTH;
        maxX = 0.5 + WIDTH;
        maxY = 0.5 + WIDTH;
        maxZ = 0.5 + WIDTH;

        if (wire.isConnected(EnumFacing.DOWN))
            minY = 0;

        if (wire.isConnected(EnumFacing.UP))
            maxY = 1;

        if (wire.isConnected(EnumFacing.NORTH))
            minZ = 0;

        if (wire.isConnected(EnumFacing.SOUTH))
            maxZ = 1;

        if (wire.isConnected(EnumFacing.WEST))
            minX = 0;

        if (wire.isConnected(EnumFacing.EAST))
            maxX = 1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        if (!(world.getTileEntity(pos) instanceof TileWire))
            return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        float WIDTH = collisionWidthList[world.getBlockState(pos).getBlock().getMetaFromState(state)];

        TileWire wire = (TileWire) world.getTileEntity(pos);

        double minX = 0.5 - WIDTH,
                minY = 0.5 - WIDTH,
                minZ = 0.5 - WIDTH,
                maxX = 0.5 + WIDTH,
                maxY = 0.5 + WIDTH,
                maxZ = 0.5 + WIDTH;

        if (wire.isConnected(EnumFacing.DOWN))
            minY = 0;

        if (wire.isConnected(EnumFacing.UP))
            maxY = 1;

        if (wire.isConnected(EnumFacing.NORTH))
            minZ = 0;

        if (wire.isConnected(EnumFacing.SOUTH))
            maxZ = 1;

        if (wire.isConnected(EnumFacing.WEST))
            minX = 0;

        if (wire.isConnected(EnumFacing.EAST))
            maxX = 1;

        return new AxisAlignedBB((double) pos.getX() + minX, (double) pos.getY() + minY, (double) pos.getZ() + minZ,
                (double) pos.getX() + maxX, (double) pos.getY() + maxY, (double) pos.getZ() + maxZ);
    }

    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List list) {
        list.add(new ItemStack(item, 1, WireType.THIN.getMetadata()));
        list.add(new ItemStack(item, 1, WireType.MEDIUM.getMetadata()));
        list.add(new ItemStack(item, 1, WireType.THICK.getMetadata()));
    }

    @Override
    public Block setUnlocalizedName(String name) {
        GameRegistry.registerBlock(this, ItemBlockWire.class, name);
        return super.setUnlocalizedName(name);
    }

    @Override
    public boolean shouldRegister() {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, WireType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((WireType) state.getValue(VARIANT)).getMetadata();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[]{VARIANT});
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileWire(meta);
    }

    public static enum WireType implements IStringSerializable {
        THIN(0, "thin"), MEDIUM(1, "medium"), THICK(2, "thick");

        private final int meta;
        private final String name;

        private WireType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

    }
}
