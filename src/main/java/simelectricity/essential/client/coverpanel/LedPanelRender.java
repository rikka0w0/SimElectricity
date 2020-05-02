package simelectricity.essential.client.coverpanel;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LedPanelRender extends GenericCoverPanelRender {
    public final static LedPanelRender instance = new LedPanelRender();

    private LedPanelRender() {
        super("ledpanel");
    }
}
