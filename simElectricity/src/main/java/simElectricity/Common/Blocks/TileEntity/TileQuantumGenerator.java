package simElectricity.Common.Blocks.TileEntity;

import simElectricity.API.Common.TileStandardGenerator;
import simElectricity.API.Energy;
import simElectricity.API.ISyncPacketHandler;

public class TileQuantumGenerator extends TileStandardGenerator implements ISyncPacketHandler {
    @Override
    public void onClient2ServerUpdate(String field, Object value, short type) {
        if (field.contains("outputVoltage") | field.contains("outputResistance"))
            Energy.postTileChangeEvent(this);
    }

    @Override
    public void onServer2ClientUpdate(String field, Object value, short type) {
    }
}
