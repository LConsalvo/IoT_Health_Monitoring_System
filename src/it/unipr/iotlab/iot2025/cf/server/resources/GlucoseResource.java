package it.unipr.iotlab.iot2025.cf.server.resources;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;


/**
 * CoAP resource providing simulated blood glucose as a plain-text numeric value.
 * The hosting server enables Observe; periodic changes notify registered observers.
 */
public class GlucoseResource extends CoapResource {
	private static final int LOWER_LIMIT = 40; 
	private static final int UPPER_LIMIT = 350;
	private int glucoseLevel = 100;

	public GlucoseResource(String name) {
		super(name);
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(this), 0, 1000);
	}
	
	public void setGlucoseLevel(int newGlucoseLevel) {
		glucoseLevel = newGlucoseLevel;
	}
	
	public int getGlucoseLevel() {
		return glucoseLevel;
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT,
				glucoseLevel + "",
				MediaTypeRegistry.TEXT_PLAIN);
	}
	/**
	 * Periodically updates the simulated blood glucose.
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
		 * Applies a random integer change from -20 to 19, clamps the result to 40-350 mg/dL,
		 * and notifies observers when the value changes.
		 */
		@Override 
		public void run() { 
			// Every second the glucose level changes by a random integer from -20 to 19.
			int glucoseLevelChange = new Random().nextInt(40) - 20; 
			if (glucoseLevelChange != 0) {
				if (glucoseLevel + glucoseLevelChange >= UPPER_LIMIT) {
					if (glucoseLevel != UPPER_LIMIT) {
						glucoseLevel = UPPER_LIMIT;
						mCoapRes.changed();
					}
				}
				else if(glucoseLevel + glucoseLevelChange <= LOWER_LIMIT) {
					if (glucoseLevel != LOWER_LIMIT) {
						glucoseLevel = LOWER_LIMIT;
						mCoapRes.changed();
					}
				}
				else {
					glucoseLevel += glucoseLevelChange;
					mCoapRes.changed();
				}
			}
		} 
	}

}
