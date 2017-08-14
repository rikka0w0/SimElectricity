package simelectricity.essential.common;

import java.lang.ref.WeakReference;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.property.IUnlistedProperty;
import simelectricity.api.tile.ISEGridTile;
import simelectricity.essential.api.ISEGenericCable;
/**
 * @author BuildCraft Lib
 */
public class UnlistedNonNullProperty<V> implements IUnlistedProperty<V> {
	public static final IUnlistedProperty<WeakReference<TileEntity>> propertyTile = new UnlistedNonNullProperty<>("tile");
    public static final IUnlistedProperty<WeakReference<ISEGenericCable>> propertyCable = new UnlistedNonNullProperty<>("tile");
    public static final IUnlistedProperty<WeakReference<ISEGridTile>> propertyGridTile = new UnlistedNonNullProperty<>("tile");
    
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