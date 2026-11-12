package it.unipr.iotlab.iot2025.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

/**
 * Sends asynchronous CoAP POST commands to control the simulated ring vibration.
 */
public class RingVibrationClient extends CoapClient {

    private int vibrationPower = 0;

    /**
     * Creates a client for the ring actuator.
     *
     * @param uri the complete CoAP URI of the vibration resource
     */
    public RingVibrationClient(String uri) {
        super(uri);
    }

    /**
     * Sends a JSON vibration command without blocking the caller.
     * Updates the cached power and invokes the callback only after a successful CoAP response.
     * Failures are reported to standard error.
     *
     * @param power the requested vibration intensity; {@code 0} switches vibration off
     * @param onSuccess the non-null action to execute on the CoAP callback thread after confirmation
     */
    public void vibrate(int power, Runnable onSuccess) {
        String payload = "{\"power\":" + power + "}";

        post(new CoapHandler() {
            /**
             * Stores the confirmed power and invokes the success callback after a successful response.
             *
             * @param response the response to the vibration command
             */
            @Override
            public void onLoad(CoapResponse response) {
                if (response != null && response.isSuccess()) {
                    vibrationPower = power;
                    onSuccess.run();
                } else {
                    System.err.println("Vibration command error");
                }
            }

            /**
             * Reports a communication failure without invoking the success callback.
             */
            @Override
            public void onError() {
                System.err.println("Error communicating with the ring");
            }
        }, payload, MediaTypeRegistry.APPLICATION_JSON);
    }
    
    /**
     * Returns the last power confirmed by a successful response.
     *
     * @return the confirmed vibration power, initially {@code 0}
     */
    public int getVibrationPower() {
    	return vibrationPower;
    }

}
