package simelectricity.essential;


import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import simelectricity.essential.cable.BlockCable;
import simelectricity.essential.cable.TileCable;
import simelectricity.essential.grid.*;
import simelectricity.essential.grid.TilePowerPole3.Pole10KvType0;
import simelectricity.essential.grid.TilePowerPole3.Pole10KvType1;
import simelectricity.essential.grid.TilePowerPole3.Pole415vType0;
import simelectricity.essential.grid.transformer.BlockPowerTransformer;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder;
import simelectricity.essential.grid.transformer.TilePowerTransformerPlaceHolder.Render;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Primary;
import simelectricity.essential.grid.transformer.TilePowerTransformerWinding.Secondary;
import simelectricity.essential.machines.BlockElectronics;
import simelectricity.essential.machines.BlockTwoPortElectronics;
import simelectricity.essential.machines.tile.*;

//@GameRegistry.ObjectHolder(SETemplate.MODID)
public class BlockRegistry {
    public static BlockCable blockCable;

    public static BlockPowerPoleTop powerPoleTop;
    public static BlockPowerPoleBottom powerPoleBottom;
    public static BlockPowerPoleCollisionBox powerPoleCollisionBox;
    public static BlockCableJoint cableJoint;
    public static BlockPowerPole2 powerPole2;
    public static BlockPowerPole3 powerPole3;
    public static BlockPowerTransformer powerTransformer;

    public static BlockElectronics blockElectronics;
    public static BlockTwoPortElectronics blockTwoPortElectronics;

    public static void registerBlocks() {
        BlockRegistry.blockCable = new BlockCable();

        BlockRegistry.powerPoleTop = new BlockPowerPoleTop();
        BlockRegistry.powerPoleBottom = new BlockPowerPoleBottom();
        BlockRegistry.powerPoleCollisionBox = new BlockPowerPoleCollisionBox();
        BlockRegistry.cableJoint = new BlockCableJoint();
        BlockRegistry.powerPole2 = new BlockPowerPole2();
        BlockRegistry.powerPole3 = new BlockPowerPole3();
        BlockRegistry.powerTransformer = new BlockPowerTransformer();

        BlockRegistry.blockElectronics = new BlockElectronics();
        BlockRegistry.blockTwoPortElectronics = new BlockTwoPortElectronics();
    }

    public static void registerTileEntities() {
        BlockRegistry.registerTile(TileCable.class);
        BlockRegistry.registerTile(TilePowerPole.class);
        BlockRegistry.registerTile(TileCableJoint.class);
        BlockRegistry.registerTile(TilePowerPole2.class);
        BlockRegistry.registerTile(TilePowerTransformerPlaceHolder.class);
        BlockRegistry.registerTile(TilePowerTransformerPlaceHolder.Primary.class);
        BlockRegistry.registerTile(TilePowerTransformerPlaceHolder.Secondary.class);
        BlockRegistry.registerTile(Render.class);
        BlockRegistry.registerTile(Primary.class);
        BlockRegistry.registerTile(Secondary.class);
        BlockRegistry.registerTile(Pole10KvType0.class);
        BlockRegistry.registerTile(Pole10KvType1.class);
        BlockRegistry.registerTile(Pole415vType0.class);

        BlockRegistry.registerTile(TileVoltageMeter.class);
        BlockRegistry.registerTile(TileQuantumGenerator.class);
        BlockRegistry.registerTile(TileAdjustableResistor.class);
        BlockRegistry.registerTile(TileIncandescentLamp.class);
        BlockRegistry.registerTile(TileSolarPanel.class);

        BlockRegistry.registerTile(TileAdjustableTransformer.class);
        BlockRegistry.registerTile(TileCurrentSensor.class);
        BlockRegistry.registerTile(TileDiode.class);
        BlockRegistry.registerTile(TileSwitch.class);
    }

    private static void registerTile(Class<? extends TileEntity> teClass) {
        String registryName = teClass.getName();
        registryName = registryName.substring(registryName.lastIndexOf(".") + 1);
        registryName = Essential.modID + ":" + registryName;
        GameRegistry.registerTileEntity(teClass, registryName);
    }
}
