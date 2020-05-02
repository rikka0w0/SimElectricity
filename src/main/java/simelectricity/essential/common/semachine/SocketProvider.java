package simelectricity.essential.common.semachine;

import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

public class SocketProvider {  
    public static final ModelProperty<Integer> propertyDownSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertyUpSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertyNorthSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertySouthSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertyWestSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertyEastSocket = new ModelProperty<>();  

    public static IModelData getModelData(ISESocketProvider socketProvider) {
        return new ModelDataMap.Builder()
                .withInitial(propertyDownSocket, socketProvider.getSocketIconIndex(Direction.DOWN))
                .withInitial(propertyUpSocket, socketProvider.getSocketIconIndex(Direction.UP))
                .withInitial(propertyNorthSocket, socketProvider.getSocketIconIndex(Direction.NORTH))
                .withInitial(propertySouthSocket, socketProvider.getSocketIconIndex(Direction.SOUTH))
                .withInitial(propertyWestSocket, socketProvider.getSocketIconIndex(Direction.WEST))
                .withInitial(propertyEastSocket, socketProvider.getSocketIconIndex(Direction.EAST))
                .build();
    }
}
