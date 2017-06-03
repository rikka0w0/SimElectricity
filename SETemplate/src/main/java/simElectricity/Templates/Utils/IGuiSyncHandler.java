package simElectricity.Templates.Utils;

public interface IGuiSyncHandler {
	//C->S only
	public final static byte EVENT_GUI_CLOSE = 0;
	public final static byte EVENT_FIELD_UPDATE = 1;	
	public final static byte EVENT_BUTTON_CLICK = 2;	//data contains button id
	public final static byte EVENT_FACING_CHANGE = 3;
	
	void onGuiEvent(byte eventID, Object[] data);
}
