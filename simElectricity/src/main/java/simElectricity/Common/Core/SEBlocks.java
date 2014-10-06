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

import cpw.mods.fml.common.registry.GameRegistry;
import simElectricity.API.Util;
import simElectricity.Common.Blocks.*;
import simElectricity.Common.Blocks.TileEntity.*;
import simElectricity.Common.Blocks.WindMill.BlockWindMillTop;
import simElectricity.Common.Blocks.WindMill.TileWindMillTop;

@GameRegistry.ObjectHolder(Util.MODID)
public class SEBlocks {

    public static BlockAdjustableResistor adjustableResistor;
    public static BlockAdjustableTransformer adjustableTransformer;
    public static BlockBatteryBox batteryBox;
    public static BlockElectricFurnace electricFurnace;
    public static BlockIncandescentLamp incandescentLamp;
    public static BlockQuantumGenerator quantumGenerator;
    public static BlockSimpleGenerator simpleGenerator;
    public static BlockSolarPanel solarPanel;
    public static BlockSwitch blockSwitch;
    public static BlockVoltageMeter voltageMeter;
    public static BlockWindMillTop windMillTop;
    public static BlockWire wire;
    public static BlockTower tower;
    public static BlockSolarInverter solarInverter;
    public static BlockIC2Consumer ic2Consumer;
    public static BlockCableClamp cableClamp;
    public static BlockIceMachine iceMachine;

    public static void preInit() {
        adjustableResistor = new BlockAdjustableResistor();
        adjustableTransformer = new BlockAdjustableTransformer();
        batteryBox = new BlockBatteryBox();
        electricFurnace = new BlockElectricFurnace();
        incandescentLamp = new BlockIncandescentLamp();
        quantumGenerator = new BlockQuantumGenerator();
        simpleGenerator = new BlockSimpleGenerator();
        solarPanel = new BlockSolarPanel();
        blockSwitch = new BlockSwitch();
        voltageMeter = new BlockVoltageMeter();
        windMillTop = new BlockWindMillTop();
        wire = new BlockWire();
        tower = new BlockTower();
        solarInverter = new BlockSolarInverter();
        ic2Consumer = new BlockIC2Consumer();
        cableClamp = new BlockCableClamp();
        iceMachine = new BlockIceMachine();
    }

    public static void init() {
        GameRegistry.registerTileEntity(TileQuantumGenerator.class, "TileQuantumGenerator");
        GameRegistry.registerTileEntity(TileSolarPanel.class, "TileSolarPanel");
        GameRegistry.registerTileEntity(TileSimpleGenerator.class, "TileSimpleGenerator");
        GameRegistry.registerTileEntity(TileVoltageMeter.class, "TileVoltageMeter");
        GameRegistry.registerTileEntity(TileElectricFurnace.class, "TileElectricFurnace");
        GameRegistry.registerTileEntity(TileWire.class, "TileWire");
        GameRegistry.registerTileEntity(TileAdjustableResistor.class, "TileAdjustableResistor");
        GameRegistry.registerTileEntity(TileWindMillTop.class, "TileWindMillTop");
        GameRegistry.registerTileEntity(TileAdjustableTransformer.class, "TileAdjustableTransformer");
        GameRegistry.registerTileEntity(TileBatteryBox.class, "TileBatteryBox");
        GameRegistry.registerTileEntity(TileIncandescentLamp.class, "TileIncandescentLamp");
        GameRegistry.registerTileEntity(TileSwitch.class, "TileSwitch");
        GameRegistry.registerTileEntity(TileTower.class, "TileTower");
        GameRegistry.registerTileEntity(TileSolarInverter.class, "TileSolarInverter");
        GameRegistry.registerTileEntity(TileIC2Consumer.class, "TileIC2Consumer");
        GameRegistry.registerTileEntity(TileCableClamp.class, "TileCableClamp");
        GameRegistry.registerTileEntity(TileIceMachine.class, "TileIceMachine");
    }
}
