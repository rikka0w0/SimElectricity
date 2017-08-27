package simelectricity.api.components;


public interface ISETransformer extends ISEComponentParameter {
    double getRatio();

    /**
     * The winding resistance of the transformer
     * <p/>
     * REFER TO SECONDARY!!!
     */
    double getInternalResistance();
}
