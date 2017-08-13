package simelectricity.essential.common;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * @author BuildCraft Lib
 */
public class UnlistedNonNullProperty<V> implements IUnlistedProperty<V> {
    public final String name;

    public UnlistedNonNullProperty(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(V value) {
        return value != null;
    }

    @Override
    public Class getType() {
        return Object.class;
    }

    @Override
    public String valueToString(V value) {
        return value.toString();
    }
}