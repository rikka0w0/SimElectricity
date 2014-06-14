package simElectricity;

import simElectricity.Test.*; 

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;


@Mod(modid = "mod_SimElectricity_Test", name = "SimElectricity_Test", version = "0.1")
public class mod_SimElectricity_Test implements IGuiHandler{
	@SidedProxy(clientSide = "simElectricity.ClientProxy_Test", serverSide = "simElectricity.mod_SimElectricity_Test")
	public static mod_SimElectricity_Test proxy;
    //@Instance("mod_SimElectricity_Test")
    //public static mod_SimElectricity_Test instance;
    
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,	int x, int y, int z){return null;}	
	public void registerTileEntitySpecialRenderer() {}
	public World getClientWorld() {return null;}
    
	/** PreInitialize */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerBlock(new BlockTransformer(), "sime:Transformer");			
		GameRegistry.registerBlock(new BlockBatteryBox(), "sime:BatteryBox");	
		
		GameRegistry.registerBlock(new BlockComplexTile(), "sime:ComplexTile");
		GameRegistry.registerBlock(new BlockIC2Emitter(), "sime:IC2Emitter");	
	}
	
	/** Initialize */
	@EventHandler
	public void load(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(BlockTransformer.TileTransformer.class, "TileTransformer");		
		GameRegistry.registerTileEntity(TileBatteryBox.class, "TileBatteryBox");
		
		GameRegistry.registerTileEntity(TileIC2Emitter.class, "TileIC2Emitter");	
		GameRegistry.registerTileEntity(ComplexTile.class, "ComplexTile");	
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		TileEntity te=world.getTileEntity(x, y, z);
		
	    if (te instanceof TileBatteryBox)
	    	return new ContainerBatteryBox(player.inventory, te);
		
		return null;
	}
	

}
