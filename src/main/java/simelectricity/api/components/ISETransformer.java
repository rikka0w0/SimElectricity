package simelectricity.api.components;


public interface ISETransformer extends ISEComponentParameter {
    /**
     * @return turns of secondary on turns of primary
     * </p>
     * >1 for step-up, <1 for step-down
     */
    double getRatio();

    /**
     * The total effective winding resistance
     * <p/>
     * REFER TO SECONDARY!!!
     */
    double getInternalResistance();
}
