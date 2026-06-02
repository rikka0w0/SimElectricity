package simelectricity.essential.client.coverpanel;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import simelectricity.essential.coverpanel.LedPanel;

@OnlyIn(Dist.CLIENT)
public class LedPanelRender extends GenericCoverPanelRender<LedPanel> {
    public final static LedPanelRender instance = new LedPanelRender();

    private LedPanelRender() {
        super("ledpanel");
    }
}
