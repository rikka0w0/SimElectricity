package simelectricity.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import simelectricity.api.SEAPI;

public class ItemSEMgrTool extends Item {
    public static final String name = "semanagementtool";

    public ItemSEMgrTool() {
        super((new Item.Properties())
                .maxStackSize(1)
                .maxDamage(0)
                .group(SEAPI.SETab));
        this.setRegistryName(ItemSEMgrTool.name);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return ActionResultType.PASS;
    }
}
