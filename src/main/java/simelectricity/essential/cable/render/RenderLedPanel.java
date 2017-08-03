package simelectricity.essential.cable.render;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.cable.LedPanel;

public class RenderLedPanel extends GenericPanelRender<LedPanel>{
	public static ISECoverPanelRender instance;
	
	public RenderLedPanel(){
		super();
		this.instance = this;
	}

	private IIcon panelTexture;
	@Override
	protected void registerBlockIcon(IIconRegister r) {
		panelTexture = r.registerIcon("sime_essential:coverpanel/ledpanel");
	}
	
	@Override
	protected IIcon getPanelTexture(ISEGenericCable cable, LedPanel coverPanel) {
		return panelTexture;
	}
}
