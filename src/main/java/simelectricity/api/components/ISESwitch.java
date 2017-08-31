package simelectricity.api.components;


public interface ISESwitch extends ISEComponentParameter {
    /**
     * @return true to allow current to flow though
     */
    boolean isOn();

    /**
     * @return the contact resistance
     */
    double getResistance();
}
