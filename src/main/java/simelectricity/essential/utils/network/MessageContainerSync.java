package simelectricity.essential.utils.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.network.MessageContainerSyncBase;
import simelectricity.essential.Essential;

public class MessageContainerSync extends MessageContainerSyncBase {		
	public static void syncToClient(EntityPlayerMP player, Object[] changeList) {
		Essential.instance.networkChannel.sendTo(new MessageContainerSync(player.openContainer.windowId, EVENT_SYNC, changeList), player);
	}

	public static void sendToClient(EntityPlayerMP player, Object... data) {
		Essential.instance.networkChannel.sendTo(new MessageContainerSync(player.openContainer.windowId, EVENT_CUSTOM, data), player);
	}

	@SideOnly(Side.CLIENT)
	public static void sendButtonClickEventToSever(Container clientContainer, int buttonID, boolean isCtrlPressed) {
		Essential.instance.networkChannel.sendToServer(new MessageContainerSync(
				clientContainer.windowId, 
				EVENT_BUTTON_CLICK,
				new Object[]{buttonID, isCtrlPressed}));
	}

	@SideOnly(Side.CLIENT)
	public static void sendDirectionSelectorClickEventToSever(Container clientContainer, EnumFacing direction, int mouseButton) {
		Essential.instance.networkChannel.sendToServer(new MessageContainerSync(clientContainer.windowId,
				EVENT_DIRECTION_SELECT,
				new Object[]{direction, mouseButton}));
	}

	@SideOnly(Side.CLIENT)
	public static void sendToServer(Container clientContainer, Object... data) {
		Essential.instance.networkChannel.sendToServer(new MessageContainerSync(
				clientContainer.windowId, 
				MessageContainerSyncBase.EVENT_CUSTOM,
				data));
	}  

	protected static final byte EVENT_BUTTON_CLICK = 2;
	protected static final byte EVENT_DIRECTION_SELECT = 3;
	
	/** 
	 * Compulsory constructor
	 */
	 public MessageContainerSync() {}

	/**
	 * Client -> Server
	 */
	 protected MessageContainerSync(int windowID, byte type, Object[] data) {
		 super(windowID, type, data);
	 }  

	 public static class HandlerServer extends MessageContainerSyncBase.HandlerServer<MessageContainerSync> {
		 @Override
		 protected void process(Container container, byte type, Object[] data) {	
			 switch (type) {
			 case EVENT_DIRECTION_SELECT:
				 if (container instanceof ISEDirectionSelectorEventHandler)
					 ((ISEDirectionSelectorEventHandler) container).onDirectionSelected((EnumFacing) data[0], (Integer) data[1]);
				 return;
			 case EVENT_BUTTON_CLICK:
				 if (container instanceof ISEButtonEventHandler)
					 ((ISEButtonEventHandler) container).onButtonPressed((Integer) data[0],
							 (Boolean) data[1]);
				 return;
			 default:
				 super.process(container, type, data);
			 }
		 }
	 }

	 public static class HandlerClient extends MessageContainerSyncBase.HandlerClient<MessageContainerSync> {}
}
