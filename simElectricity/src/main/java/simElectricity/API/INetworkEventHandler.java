package simElectricity.API;

import java.util.List;

public interface INetworkEventHandler {
	/**
	 * When the updating packet is processed on the target side, this function is fired </p>
	 * fields[] and values[] are corresponding</p>
	 * 
	 * @param fields
	 * @param values
	 * @param isClient true for client handling packet(Processing on the client side)
	 */
	void onFieldUpdate(String[] fields, Object[] values, boolean isClient);
	
	/**
	 * Add network fields!</p>
	 * @param fields A list of fields need to be frequently updated
	 */
	void addNetworkFields(List fields);
}
