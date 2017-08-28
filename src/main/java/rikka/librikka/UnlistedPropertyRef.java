package rikka.librikka;

import java.lang.ref.WeakReference;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyRef<V> implements IUnlistedProperty<V> {
	public static final IUnlistedProperty<WeakReference<TileEntity>> propertyTile = new UnlistedPropertyRef<>("tile");
    public final String name;

    public UnlistedPropertyRef(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
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
