package simelectricity.extension.facades;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import simelectricity.essential.api.ISECoverPanelFactory;
import simelectricity.essential.api.SEEAPI;

import java.lang.reflect.Constructor;

@Mod(Facades.MODID)
public class Facades {
    public static final String MODID = "sime_facades";
    
    public static boolean teThermalDynamicLoaded;
    public static boolean bcTransportLoaded;

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public final static class ModEventBusHandler {
    	@SubscribeEvent
    	public static void onCommonSetup(FMLCommonSetupEvent event) {
            teThermalDynamicLoaded = ModList.get().isLoaded("thermaldynamics");
            bcTransportLoaded = ModList.get().isLoaded("buildcrafttransport");

            // Attempt to load extension class
            if (teThermalDynamicLoaded) {
                try {
                    Class<?> clsTECF = Class.forName("simelectricity.extension.facades.TECoverFactory");
                    Constructor<?> constructor = clsTECF.getConstructor();
                    ISECoverPanelFactory teCoverPanelFactory = (ISECoverPanelFactory) constructor.newInstance();

                    SEEAPI.coverPanelRegistry.register(teCoverPanelFactory, TEFacadePanel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Attempt to load extension class
            if (bcTransportLoaded) {
                try {
                    Class<?> clsBCCF = Class.forName("simelectricity.extension.facades.BCCoverFactory");
                    Constructor<?>  constructor = clsBCCF.getConstructor();
                    ISECoverPanelFactory bcCoverPanelFactory = (ISECoverPanelFactory) constructor.newInstance();

                    SEEAPI.coverPanelRegistry.register(bcCoverPanelFactory, BCFacadePanel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    	}
    }
}
