import java.io.FileReader;

import com.google.gson.Gson;

import it.unipr.iotlab.iot2025.cf.client.MobileApp;
import it.unipr.iotlab.iot2025.cf.client.config.Config;
import it.unipr.iotlab.iot2025.cf.server.ChestBandServer;
import it.unipr.iotlab.iot2025.cf.server.GlucoseSensorServer;
import it.unipr.iotlab.iot2025.cf.server.SmartRingServer;
import it.unipr.iotlab.iot2025.cf.server.SmartwatchServer;

/**
 * Launches the four simulated device servers and the mobile application in one JVM.
 */
public class StartEverything {
	private static final String CONFIG_PATH = "appConfig.json";
	/**
	 * Loads the configuration, starts each server on a separate thread, waits one second,
	 * and launches the mobile application.
	 *
	 * @param args arguments forwarded to the mobile application
	 * @throws Exception if configuration loading, the startup wait, or application initialization fails
	 */
	public static void main(String[] args) throws Exception {
		Config config = loadConfig();

		String glucosePort = config.resources.glucoseLevel.port;
		String heartRatePort = config.resources.heartRate.port;
		String smartRingPort = config.resources.skinTemperature.port;
		String smartwatchPort = config.resources.stressLevel.port;

		startServer("Glucose sensor server", () -> GlucoseSensorServer.main(new String[] {glucosePort}));
		startServer("Chest band server", () -> ChestBandServer.main(new String[] {heartRatePort}));
		startServer("Smart ring server", () -> SmartRingServer.main(new String[] {smartRingPort}));
		startServer("Smartwatch server", () -> SmartwatchServer.main(new String[] {smartwatchPort}, config.simulatedDayDuration ));

		Thread.sleep(1000);
		MobileApp.main(args);
	}

	/**
	 * Reads the launcher configuration from {@code appConfig.json} in the working directory.
	 *
	 * @return the deserialized application configuration
	 * @throws Exception if the configuration cannot be read
	 */
	private static Config loadConfig() throws Exception {
		try (FileReader reader = new FileReader(CONFIG_PATH)) {
			return new Gson().fromJson(reader, Config.class);
		}
	}

	

	/**
	 * Starts a device server through the supplied action on a named thread.
	 *
	 * @param name the thread name and label used in the startup message
	 * @param serverMain the action that constructs and starts the server
	 */
	private static void startServer(String name, Runnable serverMain) {
		Thread serverThread = new Thread(() -> {
			System.out.println("Starting " + name + "...");
			serverMain.run();
		}, name);

		serverThread.start();
	}
}
