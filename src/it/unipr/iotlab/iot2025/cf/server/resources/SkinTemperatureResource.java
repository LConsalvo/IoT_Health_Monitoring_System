package it.unipr.iotlab.iot2025.cf.server.resources;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;


/**
 * CoAP resource providing simulated skin temperature as a plain-text numeric value.
 * The hosting server enables Observe; periodic changes notify registered observers.
 */
public class SkinTemperatureResource extends CoapResource {
	private static final double LOWER_LIMIT = 31.00; 
	private static final double UPPER_LIMIT = 44.00;
	private double skinTemperature = 36.5;
	
	/**
	 * Creates the skin temperature resource and starts its simulation timer.
	 * Schedules the first simulated update immediately and subsequent updates every real second.
	 *
	 * @param name the resource name used as its CoAP path segment
	 */
	public SkinTemperatureResource (String name) {
		super(name);
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(this), 0, 1000);
	}
	
	/**
	 * Directly replaces the stored skin temperature.
	 * This method does not validate the value or notify observers.
	 *
	 * @param newSkinTemp the new value in degrees Celsius
	 */
	public void setSkinTemperature(double newSkinTemp) {
		skinTemperature = newSkinTemp;
	}
	
	/**
	 * Returns the current simulated skin temperature.
	 *
	 * @return the stored value in degrees Celsius
	 */
	public double getSkinTemperature() {
		return skinTemperature;
	}	
	
	/**
	 * Periodically updates the simulated skin temperature.
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
		 * Applies a random change from -0.5 inclusive to 0.5 exclusive, clamps the result
		 * to 31-44 degrees Celsius, and notifies observers when the value changes.
		 */
		@Override 
		public void run() { 
			//Every second the skin temperature increases or decreases by a value between 0.5 and -0.5
			double skinTemperatureChange = new Random().nextDouble() - 0.5; 
			if (skinTemperatureChange != 0.00) {
				if (skinTemperature + skinTemperatureChange >= UPPER_LIMIT) {
					if (skinTemperature != UPPER_LIMIT) {
						skinTemperature = UPPER_LIMIT;
						mCoapRes.changed();
					}
					
				}
				else if(skinTemperature + skinTemperatureChange <= LOWER_LIMIT) {
					if (skinTemperature != LOWER_LIMIT) {
						skinTemperature = LOWER_LIMIT;
						mCoapRes.changed();
					}
					
				}
				else {
					skinTemperature += skinTemperatureChange;
					mCoapRes.changed();
				}
			}
		} 
	} 
	
	/**
	 * Responds with the current skin temperature using CoAP CONTENT and a plain-text payload.
	 *
	 * @param exchange the incoming GET exchange
	 */
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT,
				skinTemperature + "",
				MediaTypeRegistry.TEXT_PLAIN);
	}
}
