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

package simElectricity.Templates.Common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import simElectricity.API.SEAPI;

/**
 * Basic SimElectricity Item
 *
 * @author <Meow J>
 */
public class ItemSE extends Item {
    public ItemSE() {
        super();
        if (registerInCreativeTab())
            setCreativeTab(SEAPI.SETab);
    }

    /**
     * @param name name of this item.
     */
    @Override
    public Item setUnlocalizedName(String name) {
        if (shouldRegister())
            GameRegistry.registerItem(this, name);
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack itemStack) {
        return super.getUnlocalizedNameInefficiently(itemStack).replaceAll("item.", "item.sime:");
    }

    public boolean registerInCreativeTab() {
        return true;
    }

    public boolean shouldRegister() {
        return true;
    }
}
