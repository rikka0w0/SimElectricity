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

package simElectricity.Common.Core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simElectricity.API.Util;
import simElectricity.Common.Items.*;

@GameRegistry.ObjectHolder(Util.MODID)
public class SEItems {

    public static ItemFan fan;
    public static ItemGlove glove;
    public static ItemUltimateMultimeter ultimateMultimeter;
    public static ItemWrench wrench;
    public static ItemHVWire hvWire;
    public static ItemIceIngot iceIngot;

    public static void init() {
        fan = new ItemFan();
        glove = new ItemGlove();
        ultimateMultimeter = new ItemUltimateMultimeter();
        wrench = new ItemWrench();
        hvWire = new ItemHVWire();
        iceIngot = new ItemIceIngot();
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        registerRender(fan);
        registerRender(glove);
        registerRender(ultimateMultimeter);
        registerRender(wrench);
        registerRender(hvWire);
        registerRender(iceIngot);
    }

    @SideOnly(Side.CLIENT)
    private static void registerRender(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Util.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
    }
}
