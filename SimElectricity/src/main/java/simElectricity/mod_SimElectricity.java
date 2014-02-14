package simElectricity;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import simElectricity.Network.PacketPipeline;
import simElectricity.Network.PacketTileEntityFieldUpdate;
import simElectricity.Samples.BlockSample;
import simElectricity.Samples.ItemBlockSample;
import simElectricity.Samples.TileSampleBattery;
import simElectricity.Samples.TileSampleConductor;
import simElectricity.Samples.TileSampleResistor;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "mod_SimElectricity", name = "SimElectricity", version = "0.1")
public class mod_SimElectricity{
	/** Server and Client Proxy */
	@SidedProxy(clientSide = "simElectricity.ClientProxy", serverSide = "simElectricity.mod_SimElectricity")
	public static mod_SimElectricity proxy;
    @Instance("mod_SimElectricity")
    public static mod_SimElectricity instance;

	public PacketPipeline packetPipeline=new PacketPipeline();
	
	public void registerTileEntitySpecialRenderer(/**/) {
	}

	public World getClientWorld() {
		return null;
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		WorldData.onWorldUnload(event.world);
	}

	/** Initialize */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		EnergyNet.initialize();
		GameRegistry.registerBlock(new BlockSample(), ItemBlockSample.class, "Sample");
		GameRegistry.registerTileEntity(TileSampleBattery.class, "Battery");
		GameRegistry.registerTileEntity(TileSampleConductor.class, "Conductor");
		GameRegistry.registerTileEntity(TileSampleResistor.class, "Resistor");
		GameRegistry.registerItem(new Item_UltimateMultimeter(), "Item_UltimateMultimeter");
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		packetPipeline.initialise();
		// Db.init();
		//NetHandler.addChannel(NetHandler.Net_ID_TileEntitySync, new TileEntityFieldUpdatePacket());
	}

	@EventHandler
	public void postInitialise(FMLPostInitializationEvent evt) {
	    packetPipeline.registerPacket(PacketTileEntityFieldUpdate.class);
	    packetPipeline.postInitialise();

	}
	
	@SubscribeEvent
	public void tick(WorldTickEvent event){
		if(event.phase!=Phase.START)
			return;
		if(event.side!=Side.SERVER)
			return;	
		
		EnergyNet.onTick(event.world);
	}

}
