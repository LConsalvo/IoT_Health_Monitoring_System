package it.unipr.iotlab.iot2025.cf.client.config;

/**
 * Groups the configuration of the simulated sensors and ring actuator.
 */
public class ResourcesConfig {
    /**
     * Connection settings and thresholds for blood glucose.
     */
    public ResourceConfig glucoseLevel;
    /**
     * Connection settings and thresholds for heart rate.
     */
    public ResourceConfig heartRate;
    /**
     * Connection settings and thresholds for skin temperature.
     */
    public ResourceConfig skinTemperature;
    /**
     * Connection settings and warning threshold for stress.
     */
    public ResourceConfig stressLevel;
    /**
     * Connection settings and daily step goal.
     */
    public ResourceConfig stepsCount;
    /**
     * Connection settings for the sleep quality score.
     */
    public ResourceConfig sleepQuality;
    /**
     * Connection settings and maximum requested power for ring vibration.
     */
    public VibrationConfig ringVibration;
}
