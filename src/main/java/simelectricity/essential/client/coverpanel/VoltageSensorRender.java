package simelectricity.essential.client.coverpanel;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.api.client.ISECoverPanelRender;

@SideOnly(Side.CLIENT)
public class VoltageSensorRender extends GenericCoverPanelRender {
    public static ISECoverPanelRender instance;

    public VoltageSensorRender() {
        super("voltagesensor");
        VoltageSensorRender.VoltageSensorRender.instance = this;
    }
}
