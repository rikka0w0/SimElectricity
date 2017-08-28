package simelectricity.essential.client.grid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GridRenderMonitor {
    public static GridRenderMonitor instance;
    private final Set<ISEPowerPole> affactedTiles = new HashSet();

    public GridRenderMonitor() {
        MinecraftForge.EVENT_BUS.register(this);
        GridRenderMonitor.instance = this;
    }

    public void notifyChanged(ISEPowerPole... list) {
        synchronized (this) {
            for (int i = 0; i < list.length; i++)
                this.affactedTiles.add(list[i]);
        }
    }

    @SubscribeEvent
    public void tick(ClientTickEvent event) {
        if (event.phase == Phase.END)
            return;

        if (this.affactedTiles.isEmpty())
            return;

        WorldClient theWorld = Minecraft.getMinecraft().world;

        if (theWorld == null)
            return;        //Not in game yet;

        synchronized (this) {
            Iterator<ISEPowerPole> iterator = this.affactedTiles.iterator();
            while (iterator.hasNext()) {
                ISEPowerPole tile = iterator.next();
                TileEntity te = (TileEntity) tile;

                if (te.getWorld() != theWorld || te.isInvalid()) {
                    iterator.remove();
                }

                PowerPoleRenderHelper helper = tile.getRenderHelper();
                if (helper != null) {
                    tile.updateRenderInfo();
                    iterator.remove();
                }
            }

            System.out.println("Grid render updated, remaining:" + this.affactedTiles.size());
        }
    }


}
