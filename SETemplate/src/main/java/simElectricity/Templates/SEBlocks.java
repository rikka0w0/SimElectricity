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

package simElectricity.Templates;

import cpw.mods.fml.common.registry.GameRegistry;
import simElectricity.Templates.Blocks.*;
import simElectricity.Templates.TileEntity.*;

@GameRegistry.ObjectHolder(SETemplate.MODID)
public class SEBlocks {

    public static BlockAdjustableResistor adjustableResistor;
    public static BlockAdjustableTransformer adjustableTransformer;
    public static BlockIncandescentLamp incandescentLamp;
    public static BlockQuantumGenerator quantumGenerator;
    public static BlockSolarPanel solarPanel;
    public static BlockSwitch blockSwitch;
    public static BlockVoltageMeter voltageMeter;
    public static BlockWire wire;
    public static BlockTransmissionTower transmissionTower;
    public static BlockSolarInverter solarInverter;
    public static BlockIceMachine iceMachine;
    public static BlockIC2Generator ic2Generator;
    public static BlockDiode blockDiode;
    public static BlockCableClamp cableClamp;

    public static void preInit() {
        adjustableResistor = new BlockAdjustableResistor();
        adjustableTransformer = new BlockAdjustableTransformer();
        incandescentLamp = new BlockIncandescentLamp();
        quantumGenerator = new BlockQuantumGenerator();
        solarPanel = new BlockSolarPanel();
        blockSwitch = new BlockSwitch();
        voltageMeter = new BlockVoltageMeter();
        wire = new BlockWire();
        transmissionTower = new BlockTransmissionTower();
        solarInverter = new BlockSolarInverter();
        iceMachine = new BlockIceMachine();
        ic2Generator = new BlockIC2Generator();
        blockDiode = new BlockDiode();
        cableClamp = new BlockCableClamp();
    }

    public static void init() {
        GameRegistry.registerTileEntity(TileQuantumGenerator.class, "TileQuantumGenerator");
        GameRegistry.registerTileEntity(TileSolarPanel.class, "TileSolarPanel");
        GameRegistry.registerTileEntity(TileVoltageMeter.class, "TileVoltageMeter");
        GameRegistry.registerTileEntity(TileWire.class, "TileWire");
        GameRegistry.registerTileEntity(TileAdjustableResistor.class, "TileAdjustableResistor");
        GameRegistry.registerTileEntity(TileAdjustableTransformer.class, "TileAdjustableTransformer");
        GameRegistry.registerTileEntity(TileIncandescentLamp.class, "TileIncandescentLamp");
        GameRegistry.registerTileEntity(TileSwitch.class, "TileSwitch");
        GameRegistry.registerTileEntity(TileTransmissionTower.class, "TileTransmissionTower");
        GameRegistry.registerTileEntity(TileSolarInverter.class, "TileSolarInverter");
        GameRegistry.registerTileEntity(TileIceMachine.class, "TileIceMachine");
        GameRegistry.registerTileEntity(TileIC2Generator.class, "TileIC2Generator");
        GameRegistry.registerTileEntity(TileDiode.class, "TileDiode");
        GameRegistry.registerTileEntity(TileCableClamp.class, "TileCableClamp");
    }
}
