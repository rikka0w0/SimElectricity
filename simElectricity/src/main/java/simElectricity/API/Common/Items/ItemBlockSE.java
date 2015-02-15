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

package simElectricity.API.Common.Items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Basic SimElectricity ItemBlock
 *
 * @author <Meow J>
 */
public class ItemBlockSE extends ItemBlock {
    public ItemBlockSE(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack itemStack) {
        return super.getUnlocalizedNameInefficiently(itemStack).replaceAll("tile.", "tile.sime:");
    }
}
