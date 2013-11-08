package SimElectricity;

import java.util.HashMap;
import java.util.Map;

import SimElectricity.API.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

public final class EnergyNet {
	private final Map<ChunkCoordinates, TileEntity> registeredTiles = new HashMap();
	
	
	public static void onTick(World world) {
		EnergyNet energyNet = getForWorld(world);
		
	}
	
	public void addTileEntity(TileEntity te){
		if (!(te instanceof IBaseComponent)) {
			System.out.println("Invalid tileentity "+te+" is trying to attach to energy network, aborting");
			return;
		}
		
		System.out.println("Tileentity "+te+" is attached to energy network!");
		
		ChunkCoordinates coords = new ChunkCoordinates(te.xCoord,te.yCoord,te.zCoord);
		if (registeredTiles.containsKey(coords)) {
			System.out.println("Tileentity " + te + " is already added, aborting");
			return;
		}
		
		registeredTiles.put(coords, te);		
	}
	
	public void removeTileEntity(TileEntity te){
		
	}
	
	public static EnergyNet getForWorld(World world) {
		WorldData worldData = WorldData.get(world);
		return worldData.energyNet;
	}
	
	/**Initialize the energy network, basically register some forge events*/
	public static void initialize(){
		new EventHandler();
	}

	/**Responce to forge events*/
	public static class EventHandler{
		public EventHandler(){
			MinecraftForge.EVENT_BUS.register(this);
		}

		@ForgeSubscribe
		public void onAttachEvent(TileAttachEvent event) {
			EnergyNet.getForWorld(event.energyTile.worldObj).addTileEntity((TileEntity)event.energyTile);
		}
		
		@ForgeSubscribe
		public void onTileDetach(TileDetachEvent event) {
			EnergyNet.getForWorld(event.energyTile.worldObj).removeTileEntity((TileEntity)event.energyTile);
		}

		@ForgeSubscribe
		public void onTileChange(TileChangeEvent event) {
			//event.amount = EnergyNet.getForWorld(event.world).emitEnergyFrom((IEnergySource)event.energyTile, event.amount);
		}
	}
}
