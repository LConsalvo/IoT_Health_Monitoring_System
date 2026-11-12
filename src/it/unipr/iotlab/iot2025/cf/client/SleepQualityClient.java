package it.unipr.iotlab.iot2025.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

/**
 * Retrieves the sleep quality score on a scale of 100 through synchronous CoAP GET requests.
 */
public class SleepQualityClient extends CoapClient {

    private volatile int sleepQuality = -1;

    /**
     * Creates a polling client for the specified resource.
     *
     * @param uri the complete CoAP URI of the resource
     */
    public SleepQualityClient(String uri) {
        super(uri);
    }

    /**
     * Requests and caches the sleep quality score.
     * Retries requests with no response up to a total of three attempts.
     * An exception ends the current polling operation and leaves the cached value unchanged.
     *
     * @return {@code true} if a response payload was parsed and cached; {@code false} otherwise
     */
    public boolean poll() {
        int tries = 0;
        try {
            while (tries < 3){
                CoapResponse response = get();
                if (response != null) {
                    sleepQuality = Integer.parseInt(response.getResponseText().trim());
                    System.out.println("Sleep Quality: " + sleepQuality + "/100");
                    return true;
                }
                else{
                    tries++;
                }
            }
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
        return false;
    }

    /**
     * Returns the last successfully parsed value.
     *
     * @return the sleep quality score on a scale of 100, or {@code -1} before the first successful read
     */
    public int getSleepQuality() {
        return sleepQuality;
    }
}
