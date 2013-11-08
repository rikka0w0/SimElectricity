package SimElectricity;

import java.sql.SQLException;
import java.util.EnumSet;

import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import SimElectricity.Samples.*;
import SimElectricity.components.Node;
import SimElectricity.components.Resistance;
import SimElectricity.components.Supply;
import SimElectricity.components.Resistance.ResistanceType;
import SimElectricity.simulator.GaussianElimination;
import SimElectricity.simulator.Grid;
import SimElectricity.sqlite.Db;
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

@Mod( modid = "mod_SimElectricity", name="SimElectricity", version="0.1")
@NetworkMod(channels = { "mod_SimElectricity" },clientSideRequired = true)
public class mod_SimElectricity implements ITickHandler{
	/**Server and Client Proxy*/
	@SidedProxy(clientSide = "SimElectricity.ClientProxy", serverSide = "SimElectricity.mod_SimElectricity")
	public static mod_SimElectricity proxy;
    public void registerTileEntitySpecialRenderer(/**/){}
    public World getClientWorld(){return null;}
	
    
	/**implements ITickHandler*/
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		World world = (World)tickData[0];
		EnergyNet.onTick(world);
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}
	@Override
	public EnumSet<TickType> ticks() {return EnumSet.of(TickType.WORLD);}
	@Override
	public String getLabel() {return "SE";}
	
	
	@ForgeSubscribe
	public void onWorldUnload(WorldEvent.Unload event) {
		WorldData.onWorldUnload(event.world);
	}
	
	/**Initialize*/
	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {	
		TickRegistry.registerTickHandler(this, Side.SERVER);
		EnergyNet.initialize();
		GameRegistry.registerBlock(new BlockSample(555),ItemBlockSample.class);
		GameRegistry.registerTileEntity(TileSampleBattery.class, "Battery");
		GameRegistry.registerTileEntity(TileSampleConductor.class, "Conductor");	
		GameRegistry.registerTileEntity(TileSampleResistor.class, "Resistor");	
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		Db.init();
		
//		Grid grid = new Grid();
//		
//		
//		Supply s = new Supply(12);
//		Resistance R2 = new Resistance(500, ResistanceType.CABLE);
//		Resistance R1 = new Resistance(10000, ResistanceType.LOAD);
//		Resistance R3 = new Resistance(500, ResistanceType.CABLE);
//		Resistance R4 = new Resistance(10000, ResistanceType.LOAD);
//		
//		Node n1 = new Node();
//		n1.add(s);
//		n1.add(s);
//		n1.add(R2);
//		grid.add(n1);
//		
//		Node n2=new Node();
//		n2.add(R2);
//		n2.add(R1);
//		n2.add(R3);
//		grid.add(n2);
//		
//		Node n3 = new Node();	
//		n3.add(R3);
//		n3.add(R4);
//		grid.add(n3);
//
//		double[][] matrix = grid.getMatrix();		
//		for (double[] ds : matrix) {
//			for (double d : ds) {
//				System.out.print(d);
//				System.out.print(" ");
//			}
//			System.out.println("");
//		}
//		
//		double[][] A = new double[matrix.length][matrix.length];
//		for (int i = 0; i < A.length; i++) {
//			for (int j = 0; j < A.length; j++) {
//				A[i][j] = matrix[i][j];
//			}
//		}
//		
//		double[] b = new double[matrix.length];		
//		for (int i = 0; i < b.length; i++) {
//			b[i] = matrix[i][matrix.length];			
//		}
//
//		double[] x = GaussianElimination.lsolve(A, b);		
//		for (int i = 0; i < x.length; i++) {
//			System.out.println(x[i]);
//		}
	}
	

}
