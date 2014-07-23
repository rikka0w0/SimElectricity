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
    }
}
