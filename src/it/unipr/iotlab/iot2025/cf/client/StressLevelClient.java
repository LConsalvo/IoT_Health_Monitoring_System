package it.unipr.iotlab.iot2025.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

/**
 * Retrieves the stress level on the simulated scale from 0 to 4 through synchronous CoAP GET requests.
 */
public class StressLevelClient extends CoapClient {

    private volatile int stressLevel = -1;

    /**
     * Creates a polling client for the specified resource.
     *
     * @param uri the complete CoAP URI of the resource
     */
    public StressLevelClient(String uri) {
        super(uri);
    }

    /**
     * Requests and caches the stress level on the simulated scale from 0 to 4.
     * If no response is received or parsing fails, the previous cached value is retained.
     */
    public void poll() {
        try {
            CoapResponse response = get();
            if (response != null) {
            	stressLevel = Integer.parseInt(response.getResponseText().trim());
                System.out.println("Stress Level: " + response.getResponseText());
            }
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }

    /**
     * Returns the last successfully parsed value.
     *
     * @return the stress level on the simulated scale from 0 to 4, or {@code -1} before the first successful read
     */
    public int getStressLevel() {
        return stressLevel;
    }
}
