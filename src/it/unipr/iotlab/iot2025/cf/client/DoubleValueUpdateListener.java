package it.unipr.iotlab.iot2025.cf.client;

/**
 * Receives floating-point sensor values delivered by an observing client.
 */
public interface DoubleValueUpdateListener {
    /**
     * Handles a newly received sensor value.
     *
     * @param value the measured value in the units used by the source sensor
     */
    void onValueUpdate(double value);
}
