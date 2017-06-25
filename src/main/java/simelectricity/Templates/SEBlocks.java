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

package simelectricity.Templates;

import cpw.mods.fml.common.registry.GameRegistry;
import simelectricity.Templates.Blocks.*;
import simelectricity.Templates.TileEntity.*;

@GameRegistry.ObjectHolder(SETemplate.MODID)
public class SEBlocks {

    public static BlockSolarPanel solarPanel;
    public static BlockSwitch blockSwitch;
    public static BlockSolarInverter solarInverter;
    public static BlockIceMachine iceMachine;
    public static BlockDiode blockDiode;

    public static void preInit() {
        solarPanel = new BlockSolarPanel();
        blockSwitch = new BlockSwitch();
        solarInverter = new BlockSolarInverter();
        iceMachine = new BlockIceMachine();
        blockDiode = new BlockDiode();
    }

    public static void init() {
        GameRegistry.registerTileEntity(TileSolarPanel.class, "TileSolarPanel");
        GameRegistry.registerTileEntity(TileSwitch.class, "TileSwitch");
       
        GameRegistry.registerTileEntity(TileSolarInverter.class, "TileSolarInverter");
        GameRegistry.registerTileEntity(TileIceMachine.class, "TileIceMachine");
        GameRegistry.registerTileEntity(TileDiode.class, "TileDiode");
    }
}
