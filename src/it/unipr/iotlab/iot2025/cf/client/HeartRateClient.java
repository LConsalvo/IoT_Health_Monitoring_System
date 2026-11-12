package it.unipr.iotlab.iot2025.cf.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

/**
 * Observes a CoAP resource and forwards each received heart rate in beats per minute to a listener.
 */
public class HeartRateClient extends CoapClient {
    private ValueUpdateListener listener;
	private volatile int value = -1;
	
	public HeartRateClient(String uri, ValueUpdateListener listener) {
		super(uri);
		this.listener = listener;
	}

	public void startObserve(){

        observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
            	value = Integer.parseInt(response.getResponseText().trim());                
                if(listener != null){
                    listener.onValueUpdate(value);
                }
            }

            @Override
            public void onError() {
                System.err.println("Observe error");
            }
        });
	}

	public int getValue() {
		return value;
	}
}
