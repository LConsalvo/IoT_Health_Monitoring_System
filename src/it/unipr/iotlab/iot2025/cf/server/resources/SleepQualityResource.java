package it.unipr.iotlab.iot2025.cf.server.resources;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * CoAP resource providing simulated sleep quality as a plain-text numeric value.
 */
public class SleepQualityResource extends CoapResource {
	private int sleepQuality = 0;
	
	/**
	 * Creates the sleep quality resource and starts its simulation timer.
	 * Schedules the first score after one quarter of a simulated day (06:00)
	 * and subsequent scores once per simulated day.
	 *
	 * @param name the resource name used as its CoAP path segment
	 * @param simulatedDayDuration the duration of one simulated day in real milliseconds
	 */
	public SleepQualityResource (String name, long simulatedDayDuration) {
		super(name);
		long timeToWakeUp = simulatedDayDuration/4; //Waking up every day at 6am
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), timeToWakeUp, simulatedDayDuration);
	}
	
	/**
	 * Periodically updates the simulated sleep quality.
	 */
	private class UpdateTask extends TimerTask { 
		/**
		 * Selects and stores a random sleep quality score from 0 to 99, inclusive.
		 */
		@Override 
		public void run() { 
			sleepQuality = new Random().nextInt(100); 
			System.out.println("[RESOURCE] Sleep quality:" + sleepQuality);
		} 
	} 
	
	/**
	 * Responds with the current sleep quality using CoAP CONTENT and a plain-text payload.
	 *
	 * @param exchange the incoming GET exchange
	 */
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT,
				sleepQuality + "",
				MediaTypeRegistry.TEXT_PLAIN);
	}
}
