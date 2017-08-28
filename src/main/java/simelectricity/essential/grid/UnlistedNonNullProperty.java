package simelectricity.essential.grid;

import net.minecraftforge.common.property.IUnlistedProperty;
import rikka.librikka.UnlistedPropertyRef;
import simelectricity.api.tile.ISEGridTile;

import java.lang.ref.WeakReference;

public final class UnlistedNonNullProperty{
    public static final IUnlistedProperty<WeakReference<ISEGridTile>> propertyGridTile = new UnlistedPropertyRef<>("tile");
}