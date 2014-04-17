package simElectricity;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import simElectricity.API.Util;
import simElectricity.Blocks.*;
import simElectricity.Items.*;
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
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "mod_SimElectricity", name = "SimElectricity", version = "0.1")
public class mod_SimElectricity{
	/** Server and Client Proxy */
	@SidedProxy(clientSide = "simElectricity.ClientProxy", serverSide = "simElectricity.mod_SimElectricity")
	public static mod_SimElectricity proxy;
    @Instance("mod_SimElectricity")
    public static mod_SimElectricity instance;

	public PacketPipeline packetPipeline=new PacketPipeline();
	
	//Proxy
	public void registerTileEntitySpecialRenderer() {}
	public World getClientWorld() {return null;}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		WorldData.onWorldUnload(event.world);
	}

	/** Initialize */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		//Add to event bus
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		
		//Initialize energy network
		EnergyNet.initialize();
		
		final BlockQuantumGenerator QuantumGenerator=new BlockQuantumGenerator();
		//CreativeTab
		Util.SETab= new CreativeTabs("SimElectricity") {
			@Override
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem() {return Item.getItemFromBlock(QuantumGenerator);}
		};
		QuantumGenerator.setCreativeTab(Util.SETab);
		
		//Register Blocks
		GameRegistry.registerBlock(QuantumGenerator, "sime:QuantumGenerator");
		GameRegistry.registerBlock(new BlockWire(), ItemBlockWire.class, "sime:Wire");
		
		//Register Items
		GameRegistry.registerItem(new Item_UltimateMultimeter(), "sime:Item_UltimateMultimeter");

		GameRegistry.registerBlock(new BlockSample(), ItemBlockSample.class, "Sample");
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		//Initialize network proxy
		packetPipeline.initialise();
		proxy.registerTileEntitySpecialRenderer();
		
		//Register TileEntities
		GameRegistry.registerTileEntity(TileQuantumGenerator.class, "Tile_QuantumGenerator");	
		GameRegistry.registerTileEntity(TileWire.class, "Tile_CopperWire");

		GameRegistry.registerTileEntity(TileSampleBattery.class, "Battery");
		GameRegistry.registerTileEntity(TileSampleConductor.class, "Conductor");
		GameRegistry.registerTileEntity(TileSampleResistor.class, "Resistor");
	}

	@EventHandler
	public void postInitialise(FMLPostInitializationEvent evt) {
		//Register network packets
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
