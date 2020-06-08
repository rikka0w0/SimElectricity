package simelectricity.essential.client.coverpanel;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.essential.coverpanel.VoltageSensorPanel;

@OnlyIn(Dist.CLIENT)
public class VoltageSensorRender extends GenericCoverPanelRender<VoltageSensorPanel> {
    public final static VoltageSensorRender instance = new VoltageSensorRender();

    private VoltageSensorRender() {
        super("voltagesensor");
    }
}
