package it.unipr.iotlab.iot2025.cf.client.config;

/**
 * Root configuration deserialized from {@code appConfig.json}.
 */
public class Config {
	/**
	 * Settings for all sensor resources and the ring actuator.
	 */
	public ResourcesConfig resources;
	/**
	 * Duration of one simulated day in real milliseconds.
	 */
	public long simulatedDayDuration;
}
