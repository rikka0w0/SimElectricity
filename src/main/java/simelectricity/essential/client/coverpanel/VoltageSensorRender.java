package simelectricity.essential.client.coverpanel;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoltageSensorRender extends GenericCoverPanelRender {
    public final static VoltageSensorRender instance = new VoltageSensorRender();

    private VoltageSensorRender() {
        super("voltagesensor");
    }
}
