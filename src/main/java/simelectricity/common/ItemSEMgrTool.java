package simelectricity.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;

public class ItemSEMgrTool extends Item {
    public static final String name = "semanagementtool";

    public ItemSEMgrTool() {
        super((new Item.Properties())
                .stacksTo(1)
                .durability(0));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }
}
