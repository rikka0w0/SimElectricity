package simelectricity.essential.items;

import rikka.librikka.item.ISimpleTexture;
import rikka.librikka.item.ItemBase;
import simelectricity.api.SEAPI;

public class ItemMisc extends ItemBase implements ISimpleTexture {
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
