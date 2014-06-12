package simElectricity;

import simElectricity.API.Util;
import simElectricity.Blocks.*;
import simElectricity.Blocks.WindMill.BlockWindMillTop;
import simElectricity.Blocks.WindMill.TileWindMillTop;
import simElectricity.Items.*;
import simElectricity.Network.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "mod_SimElectricity", name = "SimElectricity", version = "0.1")
public class mod_SimElectricity{
	/** Server and Client Proxy */
	@SidedProxy(clientSide = "simElectricity.ClientProxy", serverSide = "simElectricity.CommonProxy")
	public static CommonProxy proxy;
    @Instance("mod_SimElectricity")
    public static mod_SimElectricity instance;

	public PacketPipeline packetPipeline=new PacketPipeline();
	private GlobalEventHandler globalEventHandler=new GlobalEventHandler();
	
	/** PreInitialize */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		//Add to event bus
		MinecraftForge.EVENT_BUS.register(globalEventHandler);
		FMLCommonHandler.instance().bus().register(globalEventHandler);
		
		//CreativeTab
		final BlockQuantumGenerator QuantumGenerator=new BlockQuantumGenerator();
		Util.SETab= new CreativeTabs("SimElectricity") {
			@Override
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem() {return Item.getItemFromBlock(QuantumGenerator);}
		};
		QuantumGenerator.setCreativeTab(Util.SETab);
		
		//Register Blocks
		GameRegistry.registerBlock(QuantumGenerator, "sime:QuantumGenerator");
		GameRegistry.registerBlock(new BlockSolarPanel(), "sime:SolarPanel");
		GameRegistry.registerBlock(new BlockSimpleGenerator(), "sime:SimpleGenerator");		
		GameRegistry.registerBlock(new BlockVoltageMeter(), "sime:VoltageMeter");
		GameRegistry.registerBlock(new BlockElectricFurnace(), "sime:ElectricFurnace");
		GameRegistry.registerBlock(new BlockAdjustableResistor(), "sime:AdjustableResistor");		
		GameRegistry.registerBlock(new BlockWire(), ItemBlockWire.class, "sime:Wire");
		GameRegistry.registerBlock(new BlockWindMillTop(), "sime:WindMillTop");	
		GameRegistry.registerBlock(new BlockComplexTile(), "sime:ComplexTile");
		
		GameRegistry.registerBlock(new BlockIC2Emitter(), "sime:IC2Emitter");			
		
		//Register Items
		GameRegistry.registerItem(new Item_UltimateMultimeter(), "sime:Item_UltimateMultimeter");
		GameRegistry.registerItem(new Item_Glove(), "sime:Item_Glove");
		GameRegistry.registerItem(new Item_Wrench(), "sime:Item_Wrench");
		GameRegistry.registerItem(new Item_Fan(), "sime:Item_Fan");
	}

	/** Initialize */
	@EventHandler
	public void load(FMLInitializationEvent event) {
		//Register GUI handler
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		//Initialize network proxy
		packetPipeline.initialise();
		proxy.registerTileEntitySpecialRenderer();
		
		//Register TileEntities
		GameRegistry.registerTileEntity(TileQuantumGenerator.class, "TileQuantumGenerator");
		GameRegistry.registerTileEntity(TileSolarPanel.class, "TileSolarPanel");
		GameRegistry.registerTileEntity(TileSimpleGenerator.class, "TileSimpleGenerator");
		GameRegistry.registerTileEntity(TileVoltageMeter.class, "TileVoltageMeter");	
		GameRegistry.registerTileEntity(TileElectricFurnace.class, "TileElectricFurnace");	
		GameRegistry.registerTileEntity(TileWire.class, "TileWire");
		GameRegistry.registerTileEntity(TileAdjustableResistor.class, "TileAdjustableResistor");		
		GameRegistry.registerTileEntity(TileWindMillTop.class, "TileWindMillTop");
		GameRegistry.registerTileEntity(TileIC2Emitter.class, "TileIC2Emitter");	
		GameRegistry.registerTileEntity(ComplexTile.class, "ComplexTile");	
	}

	/** PostInitialize */
	@EventHandler
	public void postInitialise(FMLPostInitializationEvent evt) {
		//Register network packets
	    packetPipeline.registerPacket(PacketTileEntityFieldUpdate.class);
	    packetPipeline.registerPacket(PacketTileEntitySideUpdate.class);
	    packetPipeline.postInitialise();

	}
	

}
