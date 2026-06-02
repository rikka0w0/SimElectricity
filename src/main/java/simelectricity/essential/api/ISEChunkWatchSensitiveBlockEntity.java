package simelectricity.essential.api;

public interface ISEChunkWatchSensitiveBlockEntity {
    /**
     * Called on the server side, when it is necessary to send the client about the modified cable connection status
     */
    void onRenderingUpdateRequested();
}
