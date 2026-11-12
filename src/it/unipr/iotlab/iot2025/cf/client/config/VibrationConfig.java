package it.unipr.iotlab.iot2025.cf.client.config;

/**
 * Connection and power settings for the ring vibration actuator.
 */
public class VibrationConfig {
    /**
     * Complete CoAP URI of the ring vibration actuator.
     */
    public String uri;
    /**
     * Upper bound applied by the mobile application to the requested vibration power.
     */
    public int maxPower;
}
