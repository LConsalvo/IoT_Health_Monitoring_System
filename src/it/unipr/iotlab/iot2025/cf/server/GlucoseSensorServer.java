package it.unipr.iotlab.iot2025.cf.server;

import org.eclipse.californium.core.CoapServer;

import it.unipr.iotlab.iot2025.cf.server.resources.GlucoseResource;


/**
 * CoAP server for a simulated glucose sensor exposing observable blood glucose.
 */
public class GlucoseSensorServer extends CoapServer {
	
	/**
	 * Creates the device server on the specified UDP port.
	 * Resources are added and the server is started by {@code main}.
	 *
	 * @param port the local UDP port to bind when the server starts
	 */
	public GlucoseSensorServer(int port) {
		super(port);
	}

	/**
	 * Creates the device resources and starts the CoAP server.
	 *
	 * @param args optional arguments; the first argument overrides the default UDP port 5683
	 * @throws NumberFormatException if the supplied port is not a valid integer
	 */
	public static void main(String[] args) {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 5683;
		GlucoseSensorServer glucoseSensorServer = new GlucoseSensorServer(port);
		GlucoseResource glucoseRes = new GlucoseResource("glucose-level");
		glucoseRes.setObservable(true);
		glucoseRes.getAttributes().setObservable();
		glucoseSensorServer.add(glucoseRes);
		glucoseSensorServer.start();
	}
}
