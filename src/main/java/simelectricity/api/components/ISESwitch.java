package simelectricity.api.components;


public interface ISESwitch extends ISEComponentParameter, ISESwitchable {
    /**
     * @return the contact resistance
     */
    double getResistance();
}
