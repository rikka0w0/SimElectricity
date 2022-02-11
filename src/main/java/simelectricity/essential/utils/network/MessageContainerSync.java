package simelectricity.essential.utils.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import rikka.librikka.network.MessageContainerSyncBase;
import simelectricity.essential.Essential;

public class MessageContainerSync extends MessageContainerSyncBase {
	public static void syncToClient(ServerPlayer player, Object[] changeList) {
		Essential.instance.networkChannel.send(
				PacketDistributor.PLAYER.with(()->player),
				new MessageContainerSync(player.containerCounter, EVENT_SYNC, changeList));
	}

	public static void sendToClient(ServerPlayer player, Object... data) {
		Essential.instance.networkChannel.send(PacketDistributor.PLAYER.with(()->player)
				, new MessageContainerSync(player.containerCounter, EVENT_CUSTOM, data));
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendButtonClickEventToSever(AbstractContainerMenu clientContainer, int buttonID, boolean isCtrlPressed) {
		Essential.instance.networkChannel.sendToServer(new MessageContainerSync(
				clientContainer.containerId,
				EVENT_BUTTON_CLICK,
				new Object[]{buttonID, isCtrlPressed}));
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendDirectionSelectorClickEventToSever(AbstractContainerMenu clientContainer, Direction direction, int mouseButton) {
		Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.containerId,
				EVENT_DIRECTION_SELECT,
				new Object[]{direction, mouseButton}));
	}

	@OnlyIn(Dist.CLIENT)
	public static void sendToServer(AbstractContainerMenu clientContainer, Object... data) {
		Essential.instance.networkChannel.sendToServer(new MessageContainerSync(
				clientContainer.containerId,
				MessageContainerSyncBase.EVENT_CUSTOM,
				data));
	}

	public static class Processor extends MessageContainerSyncBase.Processor<MessageContainerSync> {
		@Override
		protected MessageContainerSync create() {
			return new MessageContainerSync();
		}

		@Override
		protected Player getClientPlayer() {
			return Essential.proxy.getClientPlayer();
		}
	}

	public static final Processor processor = new Processor();
	protected static final byte EVENT_BUTTON_CLICK = 2;
	protected static final byte EVENT_DIRECTION_SELECT = 3;

	/**
	 * Compulsory constructor
	 */
	 protected MessageContainerSync() {}

	/**
	 * Client -> Server
	 */
	 protected MessageContainerSync(int windowID, byte type, Object[] data) {
		 super(windowID, type, data);
	 }

	 @Override
	 protected void processServer(AbstractContainerMenu container) {
		 switch (type) {
		 case EVENT_DIRECTION_SELECT:
			 if (container instanceof ISEDirectionSelectorEventHandler)
				 ((ISEDirectionSelectorEventHandler) container).onDirectionSelected((Direction) data[0], (Integer) data[1]);
			 return;
		 case EVENT_BUTTON_CLICK:
			 if (container instanceof ISEButtonEventHandler)
				 ((ISEButtonEventHandler) container).onButtonPressed((Integer) data[0],
						 (Boolean) data[1]);
			 return;
		 default:
			 super.processServer(container);
		 }
	 }
}
