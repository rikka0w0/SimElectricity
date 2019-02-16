package simelectricity.extension.facades;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import simelectricity.essential.Essential;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.SEEAPI;

import java.lang.reflect.Constructor;

@Mod(modid = Facades.modID, name = Facades.modName, version = Facades.version, dependencies = "required-after:"+Essential.MODID)
public class Facades {
    public static final String modID = "sime_thermalexpansion";
    public static final String modName = "SimElectricity ThermalExpansion Extension";
    public static final String version = "1.0";

    @SidedProxy(clientSide="simelectricity.extension.facades.ClientProxy", serverSide="simelectricity.extension.facades.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(modID)
    public static Facades instance;

    public static boolean teThermalDynamicLoaded;
    public static boolean bcTransportLoaded;

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Loader loader = Loader.instance();
        teThermalDynamicLoaded = loader.isModLoaded("thermaldynamics");
        bcTransportLoaded = loader.isModLoaded("buildcrafttransport");

        //Attempt to load extension class
        if (teThermalDynamicLoaded){
            try {
                Class<?> clsBCCF = Class.forName("simelectricity.extension.facades.TECoverFactory");
                Constructor<?> constructor = clsBCCF.getConstructor();
                ISECoverPanelFactory bcCoverPanelFactory = (ISECoverPanelFactory) constructor.newInstance();

                SEEAPI.coverPanelRegistry.registerCoverPanelFactory(bcCoverPanelFactory);
                proxy.RegisterBlockColorHandlers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Attempt to load extension class
        if (bcTransportLoaded){
            try {
                Class<?> clsBCCF = Class.forName("simelectricity.extension.facades.BCCoverFactory");
                Constructor<?>  constructor = clsBCCF.getConstructor();
                ISECoverPanelFactory bcCoverPanelFactory = (ISECoverPanelFactory) constructor.newInstance();

                SEEAPI.coverPanelRegistry.registerCoverPanelFactory(bcCoverPanelFactory);
                proxy.RegisterBlockColorHandlers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
