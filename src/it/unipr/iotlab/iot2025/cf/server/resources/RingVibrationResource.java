package it.unipr.iotlab.iot2025.cf.server.resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * CoAP actuator resource that simulates ring vibration by storing its state and printing changes.
 */
public class RingVibrationResource extends CoapResource {
	private boolean vibration_on = false;
	private int vibrationPower = 0;
	
	/**
	 * Creates the vibration resource in the off state.
	 *
	 * @param name the resource name used as its CoAP path segment
	 */
	public RingVibrationResource(String name) {
		super(name);
	}
	
	/**
	 * Reads the {@code power} property of the JSON request and updates the vibration state.
	 * A power of {@code 0} switches vibration off; a different nonzero power activates or adjusts it.
	 * Responds with CoAP CHANGED after handling the command.
	 * The payload is expected to be a JSON object containing a numeric {@code power} property.
	 *
	 * @param exchange the incoming POST exchange
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
	    String payload = exchange.getRequestText();
		Gson gson = new Gson();
		JsonObject json = gson.fromJson( payload, JsonObject.class);
		int power = json.get("power").getAsInt();

	    if (power == 0 && vibration_on) {
	        turnOffVibration();
	    }
	    else if (power != vibrationPower) {
	        activateVibration(power);
	    }

	    exchange.respond(CoAP.ResponseCode.CHANGED);
	}
	
	/**
	 * Activates vibration if necessary and stores the requested power.
	 *
	 * @param cmd the vibration power to apply
	 */
	private void activateVibration(int cmd) {
		if (!vibration_on) {
			vibration_on = true;
			System.out.println("**VIBRATION ON**");
		}
		vibrationPower = cmd;
		System.out.println("**VIBRATION POWER = " + cmd + "**");
	}
	
	/**
	 * Clears the active vibration flag and resets the stored power to zero.
	 */
	private void turnOffVibration() {
		vibration_on = false;
		vibrationPower = 0;
		System.out.println("**VIBRATION OFF**");
	}
}
