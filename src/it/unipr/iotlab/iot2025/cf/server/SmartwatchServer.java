package it.unipr.iotlab.iot2025.cf.server;

import org.eclipse.californium.core.CoapServer;

import it.unipr.iotlab.iot2025.cf.server.resources.SleepQualityResource;
import it.unipr.iotlab.iot2025.cf.server.resources.StepsCountResource;
import it.unipr.iotlab.iot2025.cf.server.resources.StressLevelResource;


/**
 * CoAP server for a simulated smartwatch exposing stress, daily step count, and sleep quality.
 */
public class SmartwatchServer extends CoapServer {
	
	/**
	 * Creates the device server on the specified UDP port.
	 * Resources are added and the server is started by {@code main}.
	 *
	 * @param port the local UDP port to bind when the server starts
	 */
	public SmartwatchServer(int port) {
		super(port);
	}

	/**
	 * Creates the device resources and starts the CoAP server.
	 * This helper is called by the project launcher with the simulated day duration.
	 *
	 * @param args optional arguments; the first argument overrides the default UDP port 5686
	 * @param simulatedDayDuration the duration of one simulated day in real milliseconds
	 * @throws NumberFormatException if the supplied port is not a valid integer
	 */
	public static void main(String[] args, long simulatedDayDuration) {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 5686;
		SmartwatchServer smartwatchServer = new SmartwatchServer(port);
		StressLevelResource stressLevelRes = new StressLevelResource("stress-level");
		StepsCountResource stepsCountRes = new StepsCountResource("steps-count", simulatedDayDuration);
		SleepQualityResource sleepQualityRes = new SleepQualityResource("sleep-quality", simulatedDayDuration);
		smartwatchServer.add(stressLevelRes);
		smartwatchServer.add(stepsCountRes);
		smartwatchServer.add(sleepQualityRes);
		smartwatchServer.start();
	}
}
