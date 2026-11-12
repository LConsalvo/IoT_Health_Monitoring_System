package it.unipr.iotlab.iot2025.cf.server.resources;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * CoAP resource providing simulated heart rate as a plain-text numeric value.
 * The hosting server enables Observe; periodic changes notify registered observers.
 */
public class HeartRateResource extends CoapResource {
	private static final int LOWER_LIMIT = 0;
	private static final int UPPER_LIMIT = 220;
	private int heartRate = 70;
	
	/**
	 * Creates the heart rate resource and starts its simulation timer.
	 * Schedules the first simulated update immediately and subsequent updates every real second.
	 *
	 * @param name the resource name used as its CoAP path segment
	 */
	public HeartRateResource(String name) {
		super(name);
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(this), 0, 1000);
	}
	
	/**
	 * Directly replaces the stored heart rate.
	 * This method does not validate the value or notify observers.
	 *
	 * @param newHeartRate the new value in beats per minute
	 */
	public void setHeartRate(int newHeartRate) {
		heartRate  = newHeartRate;
	}
	
	/**
	 * Returns the current simulated heart rate.
	 *
	 * @return the stored value in beats per minute
	 */
	public int getHeartRate() {
		return heartRate;
	}
	
	/**
	 * Periodically updates the simulated heart rate.
	 */
	private class UpdateTask extends TimerTask { 
		private CoapResource mCoapRes; 
		/**
		 * Creates a task that notifies the associated resource when its value changes.
		 *
		 * @param coapRes the resource whose observers should be notified
		 */
		public UpdateTask(CoapResource coapRes) { 
			mCoapRes = coapRes; 
		} 
		/**
		 * Applies a random integer change from -30 to 29, clamps the result to 0-220 beats per minute,
		 * and notifies observers when the value changes.
		 */
		@Override 
		public void run() { 
			// Every second the heart rate changes by a random integer from -30 to 29.
			int heartRateChange = new Random().nextInt(60) - 30; 
			if (heartRateChange != 0) {
				if (heartRate + heartRateChange >= UPPER_LIMIT) {
					if (heartRate != UPPER_LIMIT) {
						heartRate = UPPER_LIMIT;
						mCoapRes.changed();
					}
				}
				else if(heartRate + heartRateChange <= LOWER_LIMIT) {
					if (heartRate != LOWER_LIMIT) {
						heartRate = LOWER_LIMIT;
						mCoapRes.changed();
					}
				}
				else {
					heartRate += heartRateChange;
					mCoapRes.changed();
				}
			}
		} 
	}
	
	/**
	 * Responds with the current heart rate using CoAP CONTENT and a plain-text payload.
	 *
	 * @param exchange the incoming GET exchange
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT,
				heartRate + "",
				MediaTypeRegistry.TEXT_PLAIN);
	}
}
