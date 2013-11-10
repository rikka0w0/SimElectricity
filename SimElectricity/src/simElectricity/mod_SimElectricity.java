package simElectricity;

import java.util.EnumSet;

import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import simElectricity.Samples.BlockSample;
import simElectricity.Samples.ItemBlockSample;
import simElectricity.Samples.TileSampleBattery;
import simElectricity.Samples.TileSampleConductor;
import simElectricity.Samples.TileSampleResistor;
import simElectricity.sqlite.Db;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "mod_SimElectricity", name = "SimElectricity", version = "0.1")
@NetworkMod(channels = { "mod_SimElectricity" }, clientSideRequired = true)
public class mod_SimElectricity implements ITickHandler {
	/** Server and Client Proxy */
	@SidedProxy(clientSide = "simElectricity.ClientProxy", serverSide = "simElectricity.mod_SimElectricity")
	public static mod_SimElectricity proxy;

	public void registerTileEntitySpecialRenderer(/**/) {
	}

	public World getClientWorld() {
		return null;
	}

	/** implements ITickHandler */
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		World world = (World) tickData[0];
		EnergyNet.onTick(world);
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "SE";
	}

	@ForgeSubscribe
	public void onWorldUnload(WorldEvent.Unload event) {
		WorldData.onWorldUnload(event.world);
	}

	/** Initialize */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		TickRegistry.registerTickHandler(this, Side.SERVER);
		EnergyNet.initialize();
		GameRegistry.registerBlock(new BlockSample(555), ItemBlockSample.class);
		GameRegistry.registerTileEntity(TileSampleBattery.class, "Battery");
		GameRegistry.registerTileEntity(TileSampleConductor.class, "Conductor");
		GameRegistry.registerTileEntity(TileSampleResistor.class, "Resistor");
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		Db.init();
	}

}
