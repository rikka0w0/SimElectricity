package simelectricity.essential.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import simelectricity.api.SEAPI;
import simelectricity.essential.Essential;
import simelectricity.essential.common.SEItem;

public class ItemVitaTea extends SEItem{
	public ItemVitaTea() {
		super("cell_vita", false);

	}

	@Override
	public void beforeRegister() {
		this.setCreativeTab(SEAPI.SETab);
	}

    @Deprecated
	@SideOnly(Side.CLIENT)
    @Override
    protected String getIconString(){
    	return Essential.modID + ":" + this.registryName;
    }
}
