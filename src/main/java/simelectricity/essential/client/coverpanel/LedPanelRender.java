package simelectricity.essential.client.coverpanel;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import simelectricity.essential.coverpanel.LedPanel;

@OnlyIn(Dist.CLIENT)
public class LedPanelRender extends GenericCoverPanelRender<LedPanel> {
    public final static LedPanelRender instance = new LedPanelRender();

    private LedPanelRender() {
        super("ledpanel");
    }
}
