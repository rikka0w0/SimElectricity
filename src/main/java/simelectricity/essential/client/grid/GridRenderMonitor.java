package simelectricity.essential.client.grid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import simelectricity.common.SELogger;
import simelectricity.essential.client.grid.accessory.PoleAccessoryRendererDispatcher;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public enum GridRenderMonitor {
	instance;

	private final Set<ISEPowerPole> affactedTiles = new HashSet<>();
    private final Set<ISEPowerPole> processedTiles = new HashSet<>();


    public synchronized void notifyChanged(ISEPowerPole... list) {
            for (int i = 0; i < list.length; i++)
                this.affactedTiles.add(list[i]);
    }

    @SubscribeEvent
    public void tick(ClientTickEvent event) {
        if (event.phase == Phase.END)
            return;

        if (this.affactedTiles.isEmpty())
            return;

        ClientLevel theWorld = Minecraft.getInstance().level;

        if (theWorld == null)
            return;        //Not in game yet;

        synchronized (this) {
            Iterator<ISEPowerPole> iterator = this.affactedTiles.iterator();
            while (iterator.hasNext()) {
                ISEPowerPole tile = iterator.next();
                BlockEntity te = (BlockEntity) tile;

                if (te.getLevel() != theWorld || te.isRemoved()) {
                    iterator.remove();
                    continue;
                }

                PowerPoleRenderHelper helper = tile.getRenderHelper();
                if (helper != null) {
                	helper.updateRenderData(theWorld, tile.getNeighborPosArray());
                	this.processedTiles.add(tile);
                	iterator.remove();
                }
            }

            iterator = this.processedTiles.iterator();
            while (iterator.hasNext()) {
            	ISEPowerPole pole = iterator.next();
            	pole.getRenderHelper().postUpdate();
            	PoleAccessoryRendererDispatcher.render(theWorld, pole, pole.getAccessoryPos());
            	iterator.remove();
            }
            this.processedTiles.clear();

            SELogger.logInfo(SELogger.client, "Grid render updated, remaining:" + this.affactedTiles.size());
        }
    }

    public void markLoadedPowerPoleForRenderingUpdate() {
        ClientLevel theWorld = Minecraft.getInstance().level;

        if (theWorld == null)
            return;        //Not in game yet;

        // TODO: Check theWorld.blockEntityList
        /*for (BlockEntity tile: theWorld.getBlockEntity(null)) {
        	if (tile instanceof ISEPowerPole) {
        		PowerPoleRenderHelper helper = ((ISEPowerPole) tile).getRenderHelper();
        		if (helper == null)
        			continue;

        		helper.postUpdate();
        	}
        }*/
    }
}
