package it.unipr.iotlab.iot2025.cf.server.resources;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * CoAP resource providing simulated daily step count as a plain-text numeric value.
 */
public class StepsCountResource extends CoapResource {
	private int stepsCount = 0;
	
	/**
	 * Creates the daily step count resource and starts its simulation timer.
	 * Schedules possible increments every 500 real milliseconds and resets the count
	 * immediately and once per simulated day.
	 *
	 * @param name the resource name used as its CoAP path segment
	 * @param simulatedDayDuration the duration of one simulated day in real milliseconds
	 */
	public StepsCountResource (String name, long simulatedDayDuration) {
		super(name);
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0, 500);
		timer.schedule(new MidnightResetTask(), 0, simulatedDayDuration);
	}
	
	/**
	 * Directly replaces the stored daily step count.
	 * This method does not validate the value.
	 *
	 * @param count the new value in steps
	 */
	public void setStepsCount(int count) {
		stepsCount = count;
	}
	
	/**
	 * Returns the current simulated daily step count.
	 *
	 * @return the stored value in steps
	 */
	public int getStepsCount() {
		return stepsCount;
	}
	
	/**
	 * Periodically updates the simulated daily step count.
	 */
	private class UpdateTask extends TimerTask { 
		/**
		 * Increments the step count with a probability of 50 percent.
		 */
		@Override 
		public void run() { 
			//There is a 50% chance to increment the steps count every 0.5 seconds to simulate a person walking and stopping during the day
			int stepsProbability = new Random().nextInt(20);
			if (stepsProbability >= 10) {
				stepsCount ++;
			}
		} 
	} 
	
    /**
     * Resets the step count at the start of each simulated day.
     */
    private class MidnightResetTask extends TimerTask {
        /**
         * Sets the daily step count to zero.
         */
        @Override
        public void run() {
        	//Steps count resets every midnight
            stepsCount = 0;
        }
    }
	
	/**
	 * Responds with the current daily step count using CoAP CONTENT and a plain-text payload.
	 *
	 * @param exchange the incoming GET exchange
	 */
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT,
				stepsCount + "",
				MediaTypeRegistry.TEXT_PLAIN);
	}
}
