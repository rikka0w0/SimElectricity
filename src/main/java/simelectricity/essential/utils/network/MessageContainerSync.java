package simelectricity.essential.utils.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import rikka.librikka.ByteSerializer;
import rikka.librikka.container.ContainerSynchronizer;
import rikka.librikka.network.ICustomContainerEventClientHanlder;
import rikka.librikka.network.ICustomContainerEventServerHandler;

public record MessageContainerSync(int windowID, byte eventType, Object[] data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MessageContainerSync> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("librikka", "container_sync"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final byte EVENT_CUSTOM = 0;
    public static final byte EVENT_SYNC = 1;
    public static final byte EVENT_BUTTON_CLICK = 2;
    public static final byte EVENT_DIRECTION_SELECT = 3;

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageContainerSync> STREAM_CODEC = StreamCodec.of(
        (buf, msg) -> {
            buf.writeInt(msg.windowID());
            buf.writeByte(msg.eventType());
            buf.writeByte(msg.data().length);
            for (Object obj : msg.data()) {
                ByteSerializer.instance.packData(buf, obj);
            }
        },
        buf -> {
            int windowID = buf.readInt();
            byte eventType = buf.readByte();
            int length = buf.readByte();
            Object[] data = new Object[length];
            for (int i = 0; i < length; i++) {
                data[i] = ByteSerializer.instance.unpackData(buf);
            }
            return new MessageContainerSync(windowID, eventType, data);
        }
    );

    public static void syncToClient(ServerPlayer player, Object[] changeList) {
        PacketDistributor.sendToPlayer(player, new MessageContainerSync(player.containerMenu.containerId, EVENT_SYNC, changeList));
    }

    public static void sendToClient(ServerPlayer player, Object... data) {
        PacketDistributor.sendToPlayer(player, new MessageContainerSync(player.containerMenu.containerId, EVENT_CUSTOM, data));
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendButtonClickEventToSever(AbstractContainerMenu clientContainer, int buttonID, boolean isCtrlPressed) {
        PacketDistributor.sendToServer(new MessageContainerSync(
            clientContainer.containerId,
            EVENT_BUTTON_CLICK,
            new Object[]{buttonID, isCtrlPressed}
        ));
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendDirectionSelectorClickEventToSever(AbstractContainerMenu clientContainer, Direction direction, int mouseButton) {
        PacketDistributor.sendToServer(new MessageContainerSync(
            clientContainer.containerId,
            EVENT_DIRECTION_SELECT,
            new Object[]{direction, mouseButton}
        ));
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(AbstractContainerMenu clientContainer, Object... data) {
        PacketDistributor.sendToServer(new MessageContainerSync(
            clientContainer.containerId,
            EVENT_CUSTOM,
            data
        ));
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (player != null) {
                AbstractContainerMenu container = player.containerMenu;
                if (container != null && container.containerId == this.windowID) {
                    if (ctx.flow().getReceptionSide().isServer()) {
                        processServer(container);
                    } else {
                        processClient(container);
                    }
                }
            }
        });
    }

    private void processServer(AbstractContainerMenu container) {
        switch (eventType) {
            case EVENT_DIRECTION_SELECT:
                if (container instanceof ISEDirectionSelectorEventHandler) {
                    ((ISEDirectionSelectorEventHandler) container).onDirectionSelected((Direction) data[0], (Integer) data[1]);
                }
                break;
            case EVENT_BUTTON_CLICK:
                if (container instanceof ISEButtonEventHandler) {
                    ((ISEButtonEventHandler) container).onButtonPressed((Integer) data[0], (Boolean) data[1]);
                }
                break;
            case EVENT_CUSTOM:
                if (container instanceof ICustomContainerEventServerHandler) {
                    ((ICustomContainerEventServerHandler) container).onDataArrivedFromClient(data);
                }
                break;
        }
    }

    private void processClient(AbstractContainerMenu container) {
        switch (eventType) {
            case EVENT_SYNC:
                ContainerSynchronizer.syncClientFields(data, container);
                break;
            case EVENT_CUSTOM:
                if (container instanceof ICustomContainerEventClientHanlder) {
                    ((ICustomContainerEventClientHanlder) container).onDataArrivedFromServer(data);
                }
                break;
        }
    }
}
