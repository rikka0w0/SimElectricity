package simelectricity.essential.cable.render;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import simelectricity.essential.api.ISEGenericCable;
import simelectricity.essential.api.client.ISECoverPanelRender;
import simelectricity.essential.cable.VoltageSensorPanel;

public class RenderVoltageSensorPanel extends GenericPanelRender<VoltageSensorPanel>{
	public static ISECoverPanelRender instance;
	
	public RenderVoltageSensorPanel(){
		super();
		this.instance = this;
	}
	
	private IIcon panelTexture;
	@Override
	protected void registerBlockIcon(IIconRegister r) {
		panelTexture = r.registerIcon("sime_essential:coverpanel/voltagesensor");
	}
	
	@Override
	protected IIcon getPanelTexture(ISEGenericCable cable, VoltageSensorPanel coverPanel) {
		return panelTexture;
	}
}
