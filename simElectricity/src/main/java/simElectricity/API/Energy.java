package simElectricity.API;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import simElectricity.API.EnergyTile.IBaseComponent;
import simElectricity.API.EnergyTile.ICircuitComponent;
import simElectricity.API.EnergyTile.IEnergyTile;
import simElectricity.API.EnergyTile.ITransformer.ITransformerWinding;
import simElectricity.API.Events.TileAttachEvent;
import simElectricity.API.Events.TileChangeEvent;
import simElectricity.API.Events.TileDetachEvent;
import simElectricity.API.Events.TileRejoinEvent;
import simElectricity.Common.EnergyNet.EnergyNet;

public class Energy {
    //Energy net-------------------------------------------------------------------------------------------------------------------------------

    /**
     * Post a TileAttachEvent for a tileEntity
     */
    public static void postTileAttachEvent(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileAttachEvent(te));
    }

    /**
     * Post a TileChangeEvent for a tileEntity
     */
    public static void postTileChangeEvent(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileChangeEvent(te));
    }

    /**
     * Post a TileDetachEvent for a tileEntity
     */
    public static void postTileDetachEvent(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileDetachEvent(te));
    }

    /**
     * Post a TileRejoinEvent for a tileEntity
     */
    public static void postTileRejoinEvent(TileEntity te) {
        MinecraftForge.EVENT_BUS.post(new TileRejoinEvent(te));
    }

    /**
     * Calculate the energy output from a IEnergyTile in one tick (1/20 second)
     * For IEnergyTile Only!
     */
    public static float getWorkDonePerTick(IEnergyTile Tile) {
        return getWorkDonePerTick(Tile, ((TileEntity) Tile).getWorldObj());
    }

    /**
     * Calculate the consumed power for a given EnergyTile
     * For IEnergyTile and IConductor Only!
     */
    public static float getPower(IEnergyTile Tile) {
        return getPower(Tile, ((TileEntity) Tile).getWorldObj());
    }

    /**
     * Calculate the input/output for a given EnergyTile
     * For IEnergyTile and IConductor Only!
     */
    public static float getCurrent(IEnergyTile Tile) {
        return getCurrent(Tile, ((TileEntity) Tile).getWorldObj());
    }

    /**
     * Calculate the voltage of a given EnergyTile RELATIVE TO GROUND!
     * For IEnergyTile and IConductor Only!
     */
    public static float getVoltage(IBaseComponent Tile) {
        if (Tile instanceof ITransformerWinding) {
            return getVoltage(Tile, ((TileEntity) (((ITransformerWinding) Tile).getCore())).getWorldObj());
        } else {
            return getVoltage(Tile, ((TileEntity) Tile).getWorldObj());
        }
    }

    /**
     * Calculate the energy output from a IEnergyTile in one tick (1/20 second)
     */
    public static float getWorkDonePerTick(ICircuitComponent Tile, World world) {
        if (Tile.getOutputVoltage() > 0) {//Energy Source
            return (float) (0.05 * getVoltage(Tile, world) * getCurrent(Tile, world));
        } else {//Energy Sink
            return 0;
        }
    }

    /**
     * Calculate the consumed power for a given EnergyTile
     */
    public static float getPower(ICircuitComponent Tile, World world) {
        if (Tile.getOutputVoltage() > 0) {//Energy Source
            return ((Tile.getOutputVoltage() - getVoltage(Tile, world)) * (Tile.getOutputVoltage() - getVoltage(Tile, world))) / Tile.getResistance();
        } else {//Energy Sink
            return getVoltage(Tile, world) * getVoltage(Tile, world) / Tile.getResistance();
        }
    }

    /**
     * Calculate the input/output for a given EnergyTile
     */
    public static float getCurrent(ICircuitComponent Tile, World world) {
        if (Tile.getOutputVoltage() > 0) {            //Energy Source
            return (Tile.getOutputVoltage() - getVoltage(Tile, world)) / Tile.getResistance();
        } else {                                    //Energy Sink
            return getVoltage(Tile, world) / Tile.getResistance();
        }
    }

    /**
     * Calculate the voltage of a given EnergyTile RELATIVE TO GROUND!
     */
    public static float getVoltage(IBaseComponent Tile, World world) {
        if (EnergyNet.getForWorld(world).voltageCache.containsKey(Tile))
            return EnergyNet.getForWorld(world).voltageCache.get(Tile);
        else
            return 0;
    }
}
