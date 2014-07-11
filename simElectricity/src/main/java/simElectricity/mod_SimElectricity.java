package simElectricity;

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
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import simElectricity.API.Util;
import simElectricity.Blocks.*;
import simElectricity.Blocks.WindMill.BlockWindMillTop;
import simElectricity.Blocks.WindMill.TileWindMillTop;
import simElectricity.EnergyNet.EnergyNetEventHandler;
import simElectricity.Items.Item_Fan;
import simElectricity.Items.Item_Glove;
import simElectricity.Items.Item_UltimateMultimeter;
import simElectricity.Items.Item_Wrench;
import simElectricity.Network.PacketPipeline;
import simElectricity.Network.PacketTileEntityFieldUpdate;
import simElectricity.Network.PacketTileEntitySideUpdate;

@Mod(modid = mod_SimElectricity.MODID, name = mod_SimElectricity.NAME, version = "0.1", guiFactory = "simElectricity.Client.SimEGuiFactory", dependencies = "required-after:Forge@[10.12.2.1147,)")
public class mod_SimElectricity{

    public static final String MODID = "mod_SimElectricity";
    public static final String NAME = "SimElectricity";

	/** Server and Client Proxy */
	@SidedProxy(clientSide = "simElectricity.ClientProxy", serverSide = "simElectricity.CommonProxy")
	public static CommonProxy proxy;

    @Instance(mod_SimElectricity.MODID)
    public static mod_SimElectricity instance;

	public PacketPipeline packetPipeline = new PacketPipeline();

	/** PreInitialize */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		//Load Configs
        FMLCommonHandler.instance().bus().register(new ConfigManager());
        ConfigManager.init(event);

		//Add to event bus
		new GlobalEventHandler();
		new EnergyNetEventHandler();

		//CreativeTab
		final BlockQuantumGenerator QuantumGenerator = new BlockQuantumGenerator();
		Util.SETab= new CreativeTabs("SimElectricity") {
			@Override
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem() {return Item.getItemFromBlock(QuantumGenerator);}
		};
		QuantumGenerator.setCreativeTab(Util.SETab);

		//Register Blocks
		registerBlock(QuantumGenerator);
		registerBlock(new BlockSolarPanel());
		registerBlock(new BlockSimpleGenerator());
		registerBlock(new BlockVoltageMeter());
		registerBlock(new BlockElectricFurnace());
		registerBlock(new BlockAdjustableResistor());
		registerBlock(new BlockWire(), ItemBlockWire.class);
		registerBlock(new BlockWindMillTop());
		registerBlock(new BlockTransformer());


		//Register Items
		registerItem(new Item_UltimateMultimeter());
		registerItem(new Item_Glove());
		registerItem(new Item_Wrench());
		registerItem(new Item_Fan());
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
		GameRegistry.registerTileEntity(TileTransformer.class, "TileTransformer");
	}

	/** PostInitialize */
	@EventHandler
	public void postInitialise(FMLPostInitializationEvent evt) {
		//Register network packets
	    packetPipeline.registerPacket(PacketTileEntityFieldUpdate.class);
	    packetPipeline.registerPacket(PacketTileEntitySideUpdate.class);
	    packetPipeline.postInitialise();
	}

    private static void registerBlock(Block block) {
        GameRegistry.registerBlock(block, block.getUnlocalizedName().replace("tile.", ""));
    }

    private static void registerBlock(Block block, Class<? extends ItemBlock> itemBlockClass) {
        GameRegistry.registerBlock(block, itemBlockClass, block.getUnlocalizedName().replace("tile.", ""));
    }

    private static void registerItem(Item item) {
        GameRegistry.registerItem(item, item.getUnlocalizedName().replace("item.", ""));
    }
}
