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

package simElectricity.API.Common.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import simElectricity.API.Common.Items.ItemBlockSE;
import simElectricity.API.Util;

/**
 * Basic SimElectricity Container Block
 *
 * @author <Meow J>
 */
public abstract class BlockContainerSE extends BlockContainer {
    public BlockContainerSE(Material material) {
        super(material);
        if (registerInCreativeTab())
            setCreativeTab(Util.SETab);
        setHardness(2.0F);
        setResistance(5.0F);
    }

    public BlockContainerSE() {
        this(Material.iron);
    }


    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    /**
     * If this block has its own ItemBlock, just override this method and shouldRegister(set to false).
     *
     * @param name name of this block.
     * @see simElectricity.Common.Blocks.BlockWire
     * @see simElectricity.Common.Items.ItemBlocks.ItemBlockWire
     */
    @Override
    public Block setUnlocalizedName(String name) {
        if (shouldRegister())
            GameRegistry.registerBlock(this, ItemBlockSE.class, name);
        return super.setUnlocalizedName(name);
    }

    public boolean registerInCreativeTab() {
        return true;
    }

    public boolean shouldRegister() {
        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    /**
     * <li>-1 = doesn’t render at all, eg BlockAir</li>
     * <li>1 = renders as a flowing liquid (animated texture) using BlockFluidRenderer.renderFluid – eg lava and water.</li>
     * <li>2 = doesn’t render anything in the block layers, but has an associated TileEntitySpecialRenderer which does draw something, eg BlockChest.</li>
     * <li>3 = renders using an IBakedModel, BlockModelRenderer.renderModel.  This is described in more detail below.</li>
     */
    @Override
    public int getRenderType() {
        return 3;
    }
}
