package simElectricity.Common.Core;

import cpw.mods.fml.common.registry.GameRegistry;
import simElectricity.API.Util;
import simElectricity.Common.Items.*;

@GameRegistry.ObjectHolder(Util.MODID)
public class SEItems {

    public static ItemFan fan;
    public static ItemGlove glove;
    public static ItemUltimateMultimeter ultimateMultimeter;
    public static ItemWrench wrench;
    public static ItemHVWire hvWire;

    public static void init() {
        fan = new ItemFan();
        glove = new ItemGlove();
        ultimateMultimeter = new ItemUltimateMultimeter();
        wrench = new ItemWrench();
        hvWire = new ItemHVWire();
    }
}
