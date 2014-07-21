package simElectricity.EnergyNet;

import net.minecraft.world.World;

import java.util.Map;
import java.util.WeakHashMap;

public class WorldData {
    public static Map<World, WorldData> mapping = new WeakHashMap();

    public EnergyNet energyNet = new EnergyNet();

    public static WorldData get(World world) {
        if (world == null)
            throw new IllegalArgumentException("world is null");

        WorldData ret = mapping.get(world);

        if (ret == null) {
            ret = new WorldData();
            mapping.put(world, ret);
        }

        return ret;
    }

    public static void onWorldUnload(World world) {
        mapping.remove(world);
    }
}