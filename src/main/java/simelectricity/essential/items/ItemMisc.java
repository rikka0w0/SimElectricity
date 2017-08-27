package simelectricity.essential.items;

import simelectricity.api.SEAPI;
import simelectricity.essential.client.ISESimpleTextureItem;
import simelectricity.essential.common.SEItem;

public class ItemMisc extends SEItem implements ISESimpleTextureItem {
    private static final String[] subNames = {"ledpanel", "voltagesensor"};

    public ItemMisc() {
        super("essential_item", true);
    }

    @Override
    public void beforeRegister() {
        setCreativeTab(SEAPI.SETab);
    }

    @Override
    public String[] getSubItemUnlocalizedNames() {
        return ItemMisc.subNames;
    }

    @Override
    public String getIconName(int damage) {
        return "item_" + ItemMisc.subNames[damage];
    }
}
