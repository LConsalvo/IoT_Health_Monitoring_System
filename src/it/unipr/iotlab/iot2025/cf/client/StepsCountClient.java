package it.unipr.iotlab.iot2025.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

/**
 * Retrieves the number of steps counted during the current simulated day through synchronous CoAP GET requests.
 */
public class StepsCountClient extends CoapClient {
    private volatile int steps = -1;

    public StepsCountClient(String uri) {
        super(uri);
    }

    public void poll() {
        try {
            CoapResponse response = get();
            if (response != null) {
                steps = Integer.parseInt(response.getResponseText().trim());
                System.out.println("Steps Today: " + response.getResponseText());
            }
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }

    public int getSteps() {
        return steps;
    }
}
