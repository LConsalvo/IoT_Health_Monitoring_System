package it.unipr.iotlab.iot2025.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

/**
 * Observes a CoAP resource and forwards each received blood glucose level in mg/dL to a listener.
 */
public class GlucoseLevelClient extends CoapClient {
    private ValueUpdateListener listener;
	private volatile int value = -1;
	
	/**
	 * Creates a client for the specified observable sensor.
	 *
	 * @param uri the complete CoAP URI of the sensor resource
	 * @param listener the callback for received values, or {@code null} to only cache them
	 */
	public GlucoseLevelClient(String uri, ValueUpdateListener listener) {
		super(uri);
		this.listener = listener;
	}

	/**
	 * Registers a CoAP Observe subscription and returns without waiting for updates.
	 * Received text payloads are parsed and delivered to the listener on the CoAP callback thread.
	 * Communication failures are reported to standard error.
	 */
	public void startObserve(){

        observe(new CoapHandler() {
            /**
             * Parses a sensor notification and forwards the value to the registered listener.
             *
             * @param response the received CoAP response
             */
            @Override
            public void onLoad(CoapResponse response) {
                value = Integer.parseInt(response.getResponseText().trim());                
                if(listener != null){
                    listener.onValueUpdate(value);
                }
            }

            /**
             * Reports a failed Observe exchange to standard error.
             */
            @Override
            public void onError() {
                System.err.println("Observe error");
            }
        });
	}
	
	/**
	 * Returns the most recently parsed sensor value.
	 *
	 * @return the blood glucose level in mg/dL, or {@code -1} before the first value is received
	 */
	public int getValue() {
		return value;
	}
}
