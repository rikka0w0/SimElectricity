package simElectricity.Common.Core;

import simElectricity.Common.Items.ItemFan;
import simElectricity.Common.Items.ItemGlove;
import simElectricity.Common.Items.ItemUltimateMultimeter;
import simElectricity.Common.Items.ItemWrench;

public class SEItems {

    public static ItemFan fan;
    public static ItemGlove glove;
    public static ItemUltimateMultimeter ultimateMultimeter;
    public static ItemWrench wrench;

    public static void init() {
        fan = new ItemFan();
        glove = new ItemGlove();
        ultimateMultimeter = new ItemUltimateMultimeter();
        wrench = new ItemWrench();
    }
}
