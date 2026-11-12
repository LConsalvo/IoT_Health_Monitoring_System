package it.unipr.iotlab.iot2025.cf.client.config;

/**
 * Connection and threshold settings for a sensor resource.
 */
public class ResourceConfig {
    /**
     * Optional descriptive name of the resource.
     */
    public String name;
    /**
     * UDP port number represented as a string.
     */
    public String port;
    /**
     * Complete CoAP URI used by the client to access the resource.
     */
    public String uri;
    /**
     * Whether Observe is enabled when this resource is used as a critical sensor.
     */
    public boolean observable;
    /**
     * Lower threshold expressed in the resource's measurement units.
     */
    public double lowerThreshold;
    /**
     * Upper threshold or goal expressed in the resource's measurement units.
     */
    public double upperThreshold;
}
