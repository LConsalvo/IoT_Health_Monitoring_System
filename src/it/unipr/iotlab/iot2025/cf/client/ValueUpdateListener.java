package it.unipr.iotlab.iot2025.cf.client;

/**
 * Receives integer sensor values delivered by an observing client.
 */
public interface ValueUpdateListener {
    /**
     * Handles a newly received sensor value.
     *
     * @param value the measured value in the units used by the source sensor
     */
    void onValueUpdate(int value);
}
