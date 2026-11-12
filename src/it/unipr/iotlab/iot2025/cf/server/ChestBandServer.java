package it.unipr.iotlab.iot2025.cf.server;

import org.eclipse.californium.core.CoapServer;

import it.unipr.iotlab.iot2025.cf.server.resources.HeartRateResource;

/**
 * CoAP server for a simulated chest band exposing observable heart rate.
 */
public class ChestBandServer extends CoapServer {
	
	/**
	 * Creates the device server on the specified UDP port.
	 * Resources are added and the server is started by {@code main}.
	 *
	 * @param port the local UDP port to bind when the server starts
	 */
	public ChestBandServer(int port) {
		super(port);
	}

	/**
	 * Creates the device resources and starts the CoAP server.
	 *
	 * @param args optional arguments; the first argument overrides the default UDP port 5684
	 * @throws NumberFormatException if the supplied port is not a valid integer
	 */
	public static void main(String[] args) {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 5684;
		ChestBandServer chestBandServer = new ChestBandServer(port);
		HeartRateResource heartRateRes = new HeartRateResource("heart-rate");
		heartRateRes.setObservable(true);
		heartRateRes.getAttributes().setObservable();
		chestBandServer.add(heartRateRes);
		chestBandServer.start();
	}
}
