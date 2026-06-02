package simelectricity.essential.common.semachine;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class SocketProvider {  
    public static final ModelProperty<Integer> propertyDownSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertyUpSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertyNorthSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertySouthSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertyWestSocket = new ModelProperty<>();
    public static final ModelProperty<Integer> propertyEastSocket = new ModelProperty<>();  

    public static ModelData getModelData(ISESocketProvider socketProvider) {
        return ModelData.builder()
                .with(propertyDownSocket, socketProvider.getSocketIconIndex(Direction.DOWN))
                .with(propertyUpSocket, socketProvider.getSocketIconIndex(Direction.UP))
                .with(propertyNorthSocket, socketProvider.getSocketIconIndex(Direction.NORTH))
                .with(propertySouthSocket, socketProvider.getSocketIconIndex(Direction.SOUTH))
                .with(propertyWestSocket, socketProvider.getSocketIconIndex(Direction.WEST))
                .with(propertyEastSocket, socketProvider.getSocketIconIndex(Direction.EAST))
                .build();
    }
}

