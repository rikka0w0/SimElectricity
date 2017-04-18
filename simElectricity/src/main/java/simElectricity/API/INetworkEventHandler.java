package simElectricity.API;

import java.util.List;

@Deprecated
public interface INetworkEventHandler {
    /**
     * When the updating packet is processed on the target side, this function is fired
     * <p/>
     * fields[] and values[] are corresponding
     * <p/>
     * use worldObj.isRemote to check the side of the handler (true for client)
     */
    void onFieldUpdate(String[] fields, Object[] values);

    /**
     * Add network fields!
     *
     * @param fields A list of fields need to be frequently updated
     */
    void addNetworkFields(List fields);
}
