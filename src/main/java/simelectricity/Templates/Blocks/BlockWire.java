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

package simelectricity.Templates.Blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import simelectricity.api.SEAPI;
import simelectricity.essential.utils.ITileRenderingInfoSyncHandler;
import simelectricity.Templates.Common.BlockContainerSE;
import simelectricity.Templates.ItemBlocks.ItemBlockWire;
import simelectricity.Templates.TileEntity.TileWire;

import java.util.List;

public class BlockWire extends BlockContainerSE {

    public static final String[] subNames = { "CopperCable_Thin", "CopperCable_Medium", "CopperCable_Thick" };
    public static final float[] resistanceList = { 0.27F, 0.09F, 0.03F };
    public static final float[] collisionWidthList = { 0.12F, 0.22F, 0.32F };
    public static final float[] renderingWidthList = { 0.1F, 0.2F, 0.3F };

    public IIcon[] iconBuffer = new IIcon[subNames.length];


    //Initialize Block
    public BlockWire() {
        super(Material.circuits);
        setHardness(0.2F);
        setBlockName("Wire");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3) {
        if (player.isSneaking())
            return false;

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TileWire))
            return false;

        TileWire wire = (TileWire) tileEntity;

        if (player.getCurrentEquippedItem() != null) {
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack.getItem() == Items.dye) {
                if (!world.isRemote) {
                    wire.color = stack.getItemDamage() + 1;           //Set the color
                    SEAPI.energyNetAgent.updateTileConnection(tileEntity);           //Reconnect the wire to the energy network
                    onBlockPlacedBy(world, x, y, z, player, null);    //Update rests to clients
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
        	ITileRenderingInfoSyncHandler te = (ITileRenderingInfoSyncHandler) world.getTileEntity(x, y, z);
            te.sendRenderingInfoToClient();
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        if (world.isRemote)
            return;

        ITileRenderingInfoSyncHandler te = (ITileRenderingInfoSyncHandler) world.getTileEntity(x, y, z);
        te.sendRenderingInfoToClient();

        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) { //Update neighbors
        	TileEntity neighbor = world.getTileEntity(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
            if (neighbor instanceof ITileRenderingInfoSyncHandler){
            	((ITileRenderingInfoSyncHandler) neighbor).sendRenderingInfoToClient();
            }
        }
    }


    // Rendering
    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(1F, 0.6F, 1F, 0.0F, 0.6F, 0.0F);
    }

    private static boolean isConnected(ForgeDirection direction, boolean[] connections) {
        return direction.ordinal() < 6 && direction.ordinal() >= 0 && connections[direction.ordinal()];
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list, Entity entity) {
        super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);

        if (!(world.getTileEntity(x, y, z) instanceof TileWire))
            return;
        
        TileWire wire = (TileWire) world.getTileEntity(x, y, z);
        boolean[] connections = wire.getConnections();

        float WIDTH = collisionWidthList[world.getBlockMetadata(x, y, z)];

        float minPos = 0.5F - WIDTH, maxPos = 0.5F + WIDTH;

        if (isConnected(ForgeDirection.WEST, connections)) {
            setBlockBounds(0F, minPos, minPos, maxPos, maxPos, maxPos);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }

        if (isConnected(ForgeDirection.EAST, connections)) {
            setBlockBounds(minPos, minPos, minPos, 1F, maxPos, maxPos);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }

        if (isConnected(ForgeDirection.NORTH, connections)) {
            setBlockBounds(minPos, minPos, 0F, maxPos, maxPos, maxPos);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }

        if (isConnected(ForgeDirection.SOUTH, connections)) {
            setBlockBounds(minPos, minPos, minPos, maxPos, maxPos, 1F);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }

        if (isConnected(ForgeDirection.UP, connections)) {
            setBlockBounds(minPos, minPos, minPos, maxPos, 1F, maxPos);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }

        if (isConnected(ForgeDirection.DOWN, connections)) {
            setBlockBounds(minPos, 0F, minPos, maxPos, maxPos, maxPos);
            super.addCollisionBoxesToList(world, x, y, z, axisAlignedBB, list, entity);
        }
    }


    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float WIDTH = collisionWidthList[world.getBlockMetadata(x, y, z)];

        if (!(world.getTileEntity(x, y, z) instanceof TileWire))
            return;

        TileWire wire = (TileWire) world.getTileEntity(x, y, z);
        boolean[] connections = wire.getConnections();

        minX = 0.5 - WIDTH;
        minY = 0.5 - WIDTH;
        minZ = 0.5 - WIDTH;
        maxX = 0.5 + WIDTH;
        maxY = 0.5 + WIDTH;
        maxZ = 0.5 + WIDTH;

        if (isConnected(ForgeDirection.DOWN, connections))
            minY = 0;

        if (isConnected(ForgeDirection.UP, connections))
            maxY = 1;

        if (isConnected(ForgeDirection.NORTH, connections))
            minZ = 0;

        if (isConnected(ForgeDirection.SOUTH, connections))
            maxZ = 1;

        if (isConnected(ForgeDirection.WEST, connections))
            minX = 0;

        if (isConnected(ForgeDirection.EAST, connections))
            maxX = 1;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float WIDTH = collisionWidthList[world.getBlockMetadata(x, y, z)];

        if (!(world.getTileEntity(x, y, z) instanceof TileWire))
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);

        TileWire wire = (TileWire) world.getTileEntity(x, y, z);
        boolean[] connections = wire.getConnections();
        
        double minX = 0.5 - WIDTH,
                minY = 0.5 - WIDTH,
                minZ = 0.5 - WIDTH,
                maxX = 0.5 + WIDTH,
                maxY = 0.5 + WIDTH,
                maxZ = 0.5 + WIDTH;

        if (isConnected(ForgeDirection.DOWN, connections))
            minY = 0;

        if (isConnected(ForgeDirection.UP, connections))
            maxY = 1;

        if (isConnected(ForgeDirection.NORTH, connections))
            minZ = 0;

        if (isConnected(ForgeDirection.SOUTH, connections))
            maxZ = 1;

        if (isConnected(ForgeDirection.WEST, connections))
            minX = 0;

        if (isConnected(ForgeDirection.EAST, connections))
            maxX = 1;

        return AxisAlignedBB.getBoundingBox(x + minX, y + minY, z + minZ,
                x + maxX, y + maxY, z + maxZ);
    }

    //This will tell minecraft not to render any side of our cube.
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    //And this tell it that you can see through this block, and neighbor blocks should be rendered.
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }


    //Multi block stuff starts

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister r) {
        for (int i = 0; i < subNames.length; i++) {
            iconBuffer[i] = r.registerIcon("simElectricity:Wiring/" + subNames[i]);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int blockMeta = world.getBlockMetadata(x, y, z);
        return iconBuffer[blockMeta];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[meta];
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List subItems) {
        for (int ix = 0; ix < subNames.length; ix++) {
            subItems.add(new ItemStack(this, 1, ix));
        }
    }

    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ItemBlockWire.class, name);
        return super.setBlockName(name);
    }

    @Override
    public boolean shouldRegister() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileWire(meta);
    }
}
