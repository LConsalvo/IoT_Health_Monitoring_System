package it.unipr.iotlab.iot2025.cf.client;

import it.unipr.iotlab.iot2025.cf.client.config.Config;
import it.unipr.iotlab.iot2025.cf.client.config.ResourceConfig;
import it.unipr.iotlab.iot2025.cf.client.config.ResourcesConfig;
import it.unipr.iotlab.iot2025.cf.dashboard.Dashboard;

import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

/**
 * Coordinates the simulated health-monitoring application.
 * Observes critical sensors, polls stress and step count every ten simulated minutes,
 * reads sleep quality at 08:00 each simulated day, and controls the ring from active warnings.
 */
public class MobileApp {
	private static final String CONFIG_PATH = "appConfig.json";
	private long simulatedDayDuration;
	private long pollingPeriod ; //Polling every 10 virtual minutes
	private long wakeUpTime;


	private final ResourcesConfig resources;
	private final Dashboard dashboard;
	private final GlucoseLevelClient glucoseLevelClient;
	private final HeartRateClient heartRateClient;
	private final SkinTemperatureClient skinTemperatureClient;
	private final StressLevelClient stressLevelClient;
	private final StepsCountClient stepsCountClient;
	private final SleepQualityClient sleepQualityClient;
	private final RingVibrationClient ringVibrationClient;
	private final Timer pollingTimer = new Timer();
	private final Timer sleepQualityTimer = new Timer();

	private boolean stepsGoal;

	private boolean glucoseWarning;
	private boolean heartRateWarning;
	private boolean skinTemperatureWarning;

	/**
	 * Loads the configuration and constructs the dashboard and CoAP clients.
	 * Monitoring begins when the application is started.
	 *
	 * @throws IOException if {@code appConfig.json} cannot be opened or read
	 */
	public MobileApp() throws IOException {
		Config config = loadConfig();
		this.simulatedDayDuration = config.simulatedDayDuration;
		this.pollingPeriod = simulatedDayDuration / (24*6); // Polling period for non critical values is 10 virtual minutes
		this.wakeUpTime = simulatedDayDuration / 3; // Sleep quality collecting time is 8am
		resources = config.resources;
		dashboard = new Dashboard(simulatedDayDuration);
		stressLevelClient = new StressLevelClient(resources.stressLevel.uri);
		stepsCountClient = new StepsCountClient(resources.stepsCount.uri);
		sleepQualityClient = new SleepQualityClient(resources.sleepQuality.uri);
		ringVibrationClient = new RingVibrationClient(resources.ringVibration.uri);
		glucoseLevelClient = new GlucoseLevelClient(resources.glucoseLevel.uri, this::handleGlucoseUpdate);
		heartRateClient = new HeartRateClient(resources.heartRate.uri, this::handleHeartRateUpdate);
		skinTemperatureClient = new SkinTemperatureClient(resources.skinTemperature.uri, this::handleSkinTemperatureUpdate);
	}

	/**
	 * Constructs and starts the mobile application.
	 *
	 * @param args command-line arguments; currently unused
	 * @throws IOException if the application configuration cannot be opened or read
	 */
	public static void main(String[] args) throws IOException {
		new MobileApp().start();
	}

	/**
	 * Reads {@code appConfig.json} from the working directory.
	 *
	 * @return the deserialized application configuration
	 * @throws IOException if the configuration file cannot be opened or read
	 */
	private static Config loadConfig() throws IOException {
		try (FileReader reader = new FileReader(CONFIG_PATH)) {
			return new Gson().fromJson(reader, Config.class);
		}
	}

	/**
	 * Shows the dashboard and starts the Observe subscriptions and polling timers.
	 */
	private void start() {
		dashboard.show();
		startObservableClients();
		startPollingTimers();
	}

	/**
	 * Starts Observe for each critical sensor enabled in the configuration.
	 */
	private void startObservableClients() {
		if (resources.glucoseLevel.observable) {
			glucoseLevelClient.startObserve();
		}
		if (resources.heartRate.observable) {
			heartRateClient.startObserve();
		}
		if (resources.skinTemperature.observable) {
			skinTemperatureClient.startObserve();
		}
	}

	/**
	 * Updates the sensor row and warning state from a received value.
	 * Requests a new ring power when this sensor enters or leaves the configured safe range.
	 *
	 * @param value the received blood glucose in mg/dL
	 */
	private void handleGlucoseUpdate(int value) {
		boolean danger = isOutOfRange(value, resources.glucoseLevel);
		if(danger && !glucoseWarning){
			glucoseWarning = true;
			String message = "Dangerous glucose level!";
			System.out.println(message);
			dashboard.updateGlucose(value, danger, message);
			updateRingVibration();
		}
		else if (!danger && glucoseWarning){
			glucoseWarning = false;
			String message = "Glucose returned to normal values!";
			System.out.println(message);
			dashboard.updateGlucose(value, danger, message);
			updateRingVibration();
		}
		else {
			dashboard.updateGlucose(value, danger);
		}
	}

	/**
	 * Updates the sensor row and warning state from a received value.
	 * Requests a new ring power when this sensor enters or leaves the configured safe range.
	 *
	 * @param value the received heart rate in beats per minute
	 */
	private void handleHeartRateUpdate(int value) {
		boolean danger = isOutOfRange(value, resources.heartRate);
		if (danger && !heartRateWarning) {
			heartRateWarning = true;
			String message = "Dangerous heart rate!";
			System.out.println(message);
			dashboard.updateHeartRate(value, danger, message);
			updateRingVibration();
		}
		else if (!danger && heartRateWarning) {
			heartRateWarning = false;
			String message = "Heart rate returned to normal values!";
			System.out.println(message);
			dashboard.updateHeartRate(value, danger, message);
			updateRingVibration();
		}
		else {
			dashboard.updateHeartRate(value, danger);
		}
	}

	/**
	 * Updates the sensor row and warning state from a received value.
	 * Requests a new ring power when this sensor enters or leaves the configured safe range.
	 *
	 * @param value the received skin temperature in degrees Celsius
	 */
	private void handleSkinTemperatureUpdate(double value) {
		boolean danger = isOutOfRange(value, resources.skinTemperature);
		if (danger && !skinTemperatureWarning) {
			skinTemperatureWarning = true;
			String message = "Dangerous temperature!";
			System.out.println(message);
			dashboard.updateSkinTemperature(value, danger, message);
			updateRingVibration();
		}
		else if (!danger && skinTemperatureWarning) {
			skinTemperatureWarning = false;
			String message = "Temperature returned to normal values!";
			System.out.println(message);
			dashboard.updateSkinTemperature(value, danger, message);
			updateRingVibration();
		}
		else {
			dashboard.updateSkinTemperature(value, danger);
		}
	}

	/**
	 * Schedules stress and step polling every ten simulated minutes and sleep quality polling
	 * at 08:00 once per simulated day.
	 */
	private void startPollingTimers() {
		stepsGoal = false; //Flag to check if the daily steps goal is reached 
		pollingTimer.scheduleAtFixedRate(new PollingTask(), 0, pollingPeriod); //Polling non critical values every 10 virtual minutes
		sleepQualityTimer.scheduleAtFixedRate(new PollingSleepQualityTask(), wakeUpTime, simulatedDayDuration); //Polling sleep quality once a day at 8am
	}

	/**
	 * Counts the active warnings from glucose, heart rate, and skin temperature.
	 *
	 * @return the number of active critical warnings, from 0 to 3
	 */
	private int checkWarningLevel() {
		return (glucoseWarning ? 1 : 0)
				+ (heartRateWarning ? 1 : 0)
				+ (skinTemperatureWarning ? 1 : 0);
	}

	/**
	 * Requests an asynchronous vibration update based on the active warning count,
	 * limited by the configured maximum power. Updates the dashboard after confirmation.
	 */
	private void updateRingVibration() {
		int currentWarningLevel = checkWarningLevel();
		int power = Math.min(
			currentWarningLevel,
			resources.ringVibration.maxPower
		);

		ringVibrationClient.vibrate(power, () -> {
			dashboard.updateRingVibration(power, currentWarningLevel);
		});
	}

	/**
	 * Checks whether a value is strictly outside the configured threshold interval.
	 * Values equal to either threshold are considered within range.
	 *
	 * @param value the measurement to check
	 * @param config the thresholds for the corresponding resource
	 * @return {@code true} if the value is below the lower threshold or above the upper threshold
	 */
	private boolean isOutOfRange(double value, ResourceConfig config) {
		return value < config.lowerThreshold || value > config.upperThreshold;
	}

	/**
	 * Polls stress and step count and updates the corresponding dashboard messages.
	 */
	private class PollingTask extends TimerTask {
		/**
		 * Reads stress and step count and evaluates the stress warning and daily step goal.
		 */
		@Override
		public void run() {
			stressLevelClient.poll();
			int stressLevel = stressLevelClient.getStressLevel();
			boolean stressDanger = stressLevel >= resources.stressLevel.upperThreshold;
			if (stressDanger) {
				String message = "You are stressing out too much, take a deep breath...";
				dashboard.updateStressLevel(stressLevel, stressDanger, message);
			}
			else {
				dashboard.updateStressLevel(stressLevel, stressDanger, "");
			}

			stepsCountClient.poll();
			int steps = stepsCountClient.getSteps();
			if (steps >= resources.stepsCount.upperThreshold && !stepsGoal) {
				stepsGoal = true;
				String message = "Congratulation, you reached the daily objective of "
						+ (int) resources.stepsCount.upperThreshold + " steps!";
				dashboard.updateStepsCount(steps, false, message);
			}
			else if (steps < resources.stepsCount.upperThreshold && stepsGoal){
				stepsGoal = false;
				String message = "Steps count resetted...";
				dashboard.updateStepsCount(steps, false, message);
			}
			else {
				dashboard.updateStepsCount(steps, false, stepsGoal ? null : "");
			}
		}
	}

	/**
	 * Retrieves the daily sleep quality score and displays the result or an error message.
	 */
	private class PollingSleepQualityTask extends TimerTask {
		/**
		 * Reads sleep quality and updates the dashboard with the score or a retrieval error.
		 */
		@Override
		public void run() {
			if (sleepQualityClient.poll()){
				System.out.println("[MOBILE APP] Sleep quality taken");
				String message = "Wake up, it's a new day! Your tonight sleep quality is "
						+ sleepQualityClient.getSleepQuality() + "/100";
				dashboard.updateSleepQuality(sleepQualityClient.getSleepQuality(), false, message);
				System.out.println(message);
			}
			else {
				String message = ("Error retrieving sleep quality");
				dashboard.updateSleepQuality(-1, false, message);
			}
		}
	}
}
