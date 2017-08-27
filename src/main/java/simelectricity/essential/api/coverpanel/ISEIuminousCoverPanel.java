package simelectricity.essential.api.coverpanel;

/**
 * Implement this interface indicates that the cover panel can cause the block to glow
 */
public interface ISEIuminousCoverPanel extends ISECoverPanel {
    byte getLightValue();
}
