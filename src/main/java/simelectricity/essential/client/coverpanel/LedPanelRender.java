package simelectricity.essential.client.coverpanel;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import simelectricity.essential.api.client.ISECoverPanelRender;

@SideOnly(Side.CLIENT)
public class LedPanelRender extends GenericCoverPanelRender {
    public static ISECoverPanelRender instance;

    public LedPanelRender() {
        super("ledpanel");
        LedPanelRender.instance = this;
    }
}
