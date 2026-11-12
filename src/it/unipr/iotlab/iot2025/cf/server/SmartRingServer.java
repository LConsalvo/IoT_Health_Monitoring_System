package it.unipr.iotlab.iot2025.cf.server;

import org.eclipse.californium.core.CoapServer;

import it.unipr.iotlab.iot2025.cf.server.resources.RingVibrationResource;
import it.unipr.iotlab.iot2025.cf.server.resources.SkinTemperatureResource;

/**
 * CoAP server for a simulated smart ring exposing observable skin temperature and a vibration actuator.
 */
public class SmartRingServer extends CoapServer {
	
	/**
	 * Creates the device server on the specified UDP port.
	 * Resources are added and the server is started by {@code main}.
	 *
	 * @param port the local UDP port to bind when the server starts
	 */
	public SmartRingServer(int port) {
		super(port);
	}

	/**
	 * Creates the device resources and starts the CoAP server.
	 *
	 * @param args optional arguments; the first argument overrides the default UDP port 5685
	 * @throws NumberFormatException if the supplied port is not a valid integer
	 */
	public static void main(String[] args) {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 5685;
		SmartRingServer smartRingServer = new SmartRingServer(port);
		SkinTemperatureResource skinTempRes = new SkinTemperatureResource("skin-temperature");
		RingVibrationResource ringVibrationRes = new RingVibrationResource("ring-vibration");
		skinTempRes.setObservable(true);
		skinTempRes.getAttributes().setObservable();
		smartRingServer.add(skinTempRes);
		smartRingServer.add(ringVibrationRes);
		smartRingServer.start();
	}

}
