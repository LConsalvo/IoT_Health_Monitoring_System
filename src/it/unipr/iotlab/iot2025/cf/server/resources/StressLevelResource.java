package it.unipr.iotlab.iot2025.cf.server.resources;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;


/**
 * CoAP resource providing simulated stress level as a plain-text numeric value.
 */
public class StressLevelResource extends CoapResource {
	private int stressLevel = 0;
	
	/**
	 * Creates the stress level resource and starts its simulation timer.
	 * Schedules an immediate update and a new random stress level every 10 real seconds.
	 *
	 * @param name the resource name used as its CoAP path segment
	 */
	public StressLevelResource (String name) {
		super(name);
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0, 10000);
	}
	
	/**
	 * Directly replaces the stored stress level.
	 * This method does not validate the value.
	 *
	 * @param newStressLv the new value in the simulated scale from 0 to 4
	 */
	public void setStressLevel(int newStressLv) {
		stressLevel = newStressLv;
	}
	
	/**
	 * Returns the current simulated stress level.
	 *
	 * @return the stored value in the simulated scale from 0 to 4
	 */
	public int getStressLevel() {
		return stressLevel;
	}
	
	/**
	 * Periodically updates the simulated stress level.
	 */
	private class UpdateTask extends TimerTask { 
		/**
		 * Selects and stores a random stress level from 0 to 4, inclusive.
		 */
		@Override 
		public void run() { 
			//Every 10 seconds the stress level can change in one of the 5 levels (0 to 4) 
			stressLevel = new Random().nextInt(5); 
		} 
	}
	
	/**
	 * Responds with the current stress level using CoAP CONTENT and a plain-text payload.
	 *
	 * @param exchange the incoming GET exchange
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT,
				stressLevel + "",
				MediaTypeRegistry.TEXT_PLAIN);
	}
}
