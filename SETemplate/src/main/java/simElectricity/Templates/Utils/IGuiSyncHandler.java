package simElectricity.Templates.Utils;

public interface IGuiSyncHandler {
	//C->S only
	public final static byte EVENT_FIELD_UPDATE = 0;	
	public final static byte EVENT_BUTTON_CLICK = 1;	//data contains button id
	public final static byte EVENT_FACING_CHANGE = 2;
	
	void onGuiEvent(byte eventID, Object[] data);
}
