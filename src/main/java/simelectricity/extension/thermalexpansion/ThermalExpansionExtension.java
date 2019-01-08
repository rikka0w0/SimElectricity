package simelectricity.extension.thermalexpansion;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.SEEAPI;

import java.lang.reflect.Constructor;

@Mod(modid = ThermalExpansionExtension.modID, name = ThermalExpansionExtension.modName, version = ThermalExpansionExtension.version)
public class ThermalExpansionExtension {
    public static final String modID = "sime_thermalexpansion";
    public static final String modName = "SimElectricity ThermalExpansion Extension";
    public static final String version = "1.0";

    @SidedProxy(clientSide="simelectricity.extension.thermalexpansion.ClientProxy", serverSide="simelectricity.extension.thermalexpansion.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(modID)
    public static ThermalExpansionExtension instance;

    public static boolean teThermalDynamicLoaded;

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Loader loader = Loader.instance();
        teThermalDynamicLoaded = loader.isModLoaded("thermaldynamics");

        //Attempt to load extension class
        if (teThermalDynamicLoaded){
            try {
                Class<?> clsBCCF = Class.forName("simelectricity.extension.thermalexpansion.TECoverFactory");
                Constructor<?> constructor = clsBCCF.getConstructor();
                ISECoverPanelFactory bcCoverPanelFactory = (ISECoverPanelFactory) constructor.newInstance();

                SEEAPI.coverPanelRegistry.registerCoverPanelFactory(bcCoverPanelFactory);
                proxy.RegisterBlockColorHandlers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
