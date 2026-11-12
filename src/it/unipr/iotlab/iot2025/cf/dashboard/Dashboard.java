package it.unipr.iotlab.iot2025.cf.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Swing dashboard displaying sensor values, warning messages, ring power, and a simulated clock.
 * Public display-update methods enqueue their Swing changes on the event dispatch thread.
 */
public class Dashboard {
	private static final int INFO_MESSAGE_DURATION = 5000;
	private static final int CLOCK_REFRESH_INTERVAL = 100;
	private static final int MINUTES_PER_DAY = 24 * 60;
	private static final Color NORMAL_COLOR = new Color(38, 142, 84);
	private static final Color DANGER_COLOR = new Color(190, 49, 68);
	private static final Color BACKGROUND_COLOR = new Color(246, 248, 250);
	private static final Color ROW_COLOR = Color.WHITE;
	
	private final JFrame frame;
	private final JLabel clockLabel;
	private final JLabel warningLevelLabel;
	private final Map<String, ResourceRow> rows = new LinkedHashMap<>();
	private long clockStartTime;
	private Timer clockTimer;
	private long simulatedDayDuration = 0;
	
	/**
	 * Builds the dashboard components without making the window visible.
	 *
	 * @param simulatedDayDuration the positive duration of one simulated day in real milliseconds
	 */
	public Dashboard(long simulatedDayDuration) {
		this.simulatedDayDuration = simulatedDayDuration;
		frame = new JFrame("IoT Health Dashboard");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(940, 430);
		frame.setLocationRelativeTo(null);
		
		JPanel root = new JPanel(new BorderLayout(12, 12));
		root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		root.setBackground(BACKGROUND_COLOR);
		
		JLabel title = new JLabel("IoT Health Dashboard");
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

		clockLabel = new JLabel("00:00");
		clockLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
		clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
		headerPanel.setBackground(BACKGROUND_COLOR);
		headerPanel.add(title, BorderLayout.WEST);
		headerPanel.add(clockLabel, BorderLayout.EAST);
		root.add(headerPanel, BorderLayout.NORTH);
		
		JPanel resourcesPanel = new JPanel(new GridLayout(0, 1, 8, 8));
		resourcesPanel.setBackground(BACKGROUND_COLOR);
		root.add(resourcesPanel, BorderLayout.CENTER);
		
		addRow(resourcesPanel, "glucose", "Glucose", "mg/dL");
		addRow(resourcesPanel, "heartRate", "Heart rate", "bpm");
		addRow(resourcesPanel, "skinTemperature", "Skin temperature", "C");
		addRow(resourcesPanel, "stressLevel", "Stress level", "");
		addRow(resourcesPanel, "stepsCount", "Steps today", "steps");
		addRow(resourcesPanel, "sleepQuality", "Sleep quality", "/100");
		addRow(resourcesPanel, "ringVibration", "Ring vibration", "power");
		
		warningLevelLabel = new JLabel("Active warnings: 0", SwingConstants.CENTER);
		warningLevelLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		warningLevelLabel.setOpaque(true);
		warningLevelLabel.setBackground(NORMAL_COLOR);
		warningLevelLabel.setForeground(Color.WHITE);
		warningLevelLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		root.add(warningLevelLabel, BorderLayout.SOUTH);
		
		frame.setContentPane(root);
	}
	
	/**
	 * Schedules the window display and starts the simulated clock on the Swing event dispatch thread.
	 */
	public void show() {
		SwingUtilities.invokeLater(() -> {
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setVisible(true);
			startClock();
		});
	}
	
	/**
	 * Schedules an update of the blood glucose value and status while retaining the current message.
	 *
	 * @param value the displayed value in mg/dL
	 * @param danger whether the row should display its warning state
	 */
	public void updateGlucose(int value, boolean danger) {
		updateResource("glucose", Integer.toString(value), danger);
	}

	/**
	 * Schedules an update of the blood glucose value, status, and optional message.
	 *
	 * @param value the displayed value in mg/dL
	 * @param danger whether the row should display its warning state
	 * @param message the message to display; {@code null} retains the current message and an empty string clears it
	 */
	public void updateGlucose(int value, boolean danger, String message) {
		updateResource("glucose", Integer.toString(value), danger, message);
	}
	
	/**
	 * Schedules an update of the heart rate value and status while retaining the current message.
	 *
	 * @param value the displayed value in beats per minute
	 * @param danger whether the row should display its warning state
	 */
	public void updateHeartRate(int value, boolean danger) {
		updateResource("heartRate", Integer.toString(value), danger);
	}

	/**
	 * Schedules an update of the heart rate value, status, and optional message.
	 *
	 * @param value the displayed value in beats per minute
	 * @param danger whether the row should display its warning state
	 * @param message the message to display; {@code null} retains the current message and an empty string clears it
	 */
	public void updateHeartRate(int value, boolean danger, String message) {
		updateResource("heartRate", Integer.toString(value), danger, message);
	}
	
	/**
	 * Schedules an update of the skin temperature value and status while retaining the current message.
	 *
	 * @param value the displayed value in degrees Celsius
	 * @param danger whether the row should display its warning state
	 */
	public void updateSkinTemperature(double value, boolean danger) {
		updateResource("skinTemperature", String.format("%.1f", value), danger);
	}

	/**
	 * Schedules an update of the skin temperature value, status, and optional message.
	 *
	 * @param value the displayed value in degrees Celsius
	 * @param danger whether the row should display its warning state
	 * @param message the message to display; {@code null} retains the current message and an empty string clears it
	 */
	public void updateSkinTemperature(double value, boolean danger, String message) {
		updateResource("skinTemperature", String.format("%.1f", value), danger, message);
	}
	
	/**
	 * Schedules an update of the stress level value and status while retaining the current message.
	 *
	 * @param value the displayed value in the simulated scale from 0 to 4
	 * @param danger whether the row should display its warning state
	 */
	public void updateStressLevel(int value, boolean danger) {
		updateResource("stressLevel", Integer.toString(value), danger);
	}

	/**
	 * Schedules an update of the stress level value, status, and optional message.
	 *
	 * @param value the displayed value in the simulated scale from 0 to 4
	 * @param danger whether the row should display its warning state
	 * @param message the message to display; {@code null} retains the current message and an empty string clears it
	 */
	public void updateStressLevel(int value, boolean danger, String message) {
		updateResource("stressLevel", Integer.toString(value), danger, message);
	}
	
	/**
	 * Schedules an update of the daily step count value and status while retaining the current message.
	 *
	 * @param value the displayed value in steps
	 * @param danger whether the row should display its warning state
	 */
	public void updateStepsCount(int value, boolean danger) {
		updateResource("stepsCount", Integer.toString(value), danger);
	}

	/**
	 * Schedules an update of the daily step count value, status, and optional message.
	 *
	 * @param value the displayed value in steps
	 * @param danger whether the row should display its warning state
	 * @param message the message to display; {@code null} retains the current message and an empty string clears it
	 */
	public void updateStepsCount(int value, boolean danger, String message) {
		updateResource("stepsCount", Integer.toString(value), danger, message);
	}
	
	/**
	 * Schedules an update of the sleep quality value and status while retaining the current message.
	 *
	 * @param value the displayed value in a score out of 100
	 * @param danger whether the row should display its warning state
	 */
	public void updateSleepQuality(int value, boolean danger) {
		updateResource("sleepQuality", Integer.toString(value), danger);
	}

	/**
	 * Schedules an update of the sleep quality value, status, and optional message.
	 *
	 * @param value the displayed value in a score out of 100
	 * @param danger whether the row should display its warning state
	 * @param message the message to display; {@code null} retains the current message and an empty string clears it
	 */
	public void updateSleepQuality(int value, boolean danger, String message) {
		updateResource("sleepQuality", Integer.toString(value), danger, message);
	}
	
	/**
	 * Schedules an update of the displayed ring power and active warning count.
	 *
	 * @param power the vibration power to display
	 * @param warningLevel the number of active critical sensor warnings
	 */
	public void updateRingVibration(int power, int warningLevel) {
		boolean danger = warningLevel > 0;
		updateResource("ringVibration", Integer.toString(power), danger);
		SwingUtilities.invokeLater(() -> {
			warningLevelLabel.setText("Active warnings: " + warningLevel);
			warningLevelLabel.setBackground(danger ? DANGER_COLOR : NORMAL_COLOR);
		});
	}
	
	/**
	 * Creates and registers a display row.
	 *
	 * @param parent the panel that contains the row
	 * @param key the internal resource identifier
	 * @param name the displayed resource name
	 * @param unit the displayed measurement unit, or an empty string for no unit
	 */
	private void addRow(JPanel parent, String key, String name, String unit) {
		ResourceRow row = new ResourceRow(name, unit);
		rows.put(key, row);
		parent.add(row.panel);
	}
	
	/**
	 * Schedules a value and status update without replacing the current message.
	 *
	 * @param key the internal resource identifier
	 * @param value the formatted measurement
	 * @param danger whether to display the warning state
	 */
	private void updateResource(String key, String value, boolean danger) {
		updateResource(key, value, danger, null);
	}

	/**
	 * Schedules a row update on the Swing event dispatch thread.
	 * Unknown resource identifiers are ignored.
	 *
	 * @param key the internal resource identifier
	 * @param value the formatted measurement
	 * @param danger whether to display the warning state
	 * @param message the new message, {@code null} to retain it, or an empty string to clear it
	 */
	private void updateResource(String key, String value, boolean danger, String message) {
		ResourceRow row = rows.get(key);
		if (row == null) {
			return;
		}
		SwingUtilities.invokeLater(() -> row.update(value, danger, message));
	}

	/**
	 * Starts the simulated clock at midnight unless its refresh timer is already running.
	 */
	private void startClock() {
		if (clockTimer != null && clockTimer.isRunning()) {
			return;
		}

		clockStartTime = System.currentTimeMillis();
		clockTimer = new Timer(CLOCK_REFRESH_INTERVAL, event -> updateClock());
		clockTimer.start();
		updateClock();
	}

	/**
	 * Converts elapsed real time to simulated hours and minutes and refreshes the clock label.
	 */
	private void updateClock() {
		long elapsed = (System.currentTimeMillis() - clockStartTime) % simulatedDayDuration;
		int simulatedMinute = (int) (elapsed * MINUTES_PER_DAY / simulatedDayDuration);
		int hour = simulatedMinute / 60;
		int minute = simulatedMinute % 60;
		clockLabel.setText(String.format("%02d:%02d", hour, minute));
	}
	
	/**
	 * Groups the components and message timer for one dashboard resource.
	 */
	private static class ResourceRow {
		private final JPanel panel;
		private final JLabel statusLabel;
		private final JLabel messageLabel;
		private final JLabel valueLabel;
		private final String unit;
		private Timer clearMessageTimer;
		private int messageVersion;
		
		/**
		 * Builds a row with an initial placeholder value and a normal status.
		 *
		 * @param name the displayed resource name
		 * @param unit the measurement unit, or an empty string for no unit
		 */
		ResourceRow(String name, String unit) {
			this.unit = unit;
			panel = new JPanel(new BorderLayout(12, 0));
			panel.setBackground(Dashboard.ROW_COLOR);
			panel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(new Color(220, 224, 228)),
					BorderFactory.createEmptyBorder(10, 12, 10, 12)));
			
			JLabel nameLabel = new JLabel(name);
			nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
			panel.add(nameLabel, BorderLayout.WEST);
			
			JPanel dataPanel = new JPanel(new BorderLayout(12, 0));
			dataPanel.setBackground(Dashboard.ROW_COLOR);

			JPanel rightDataPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
			rightDataPanel.setBackground(Dashboard.ROW_COLOR);

			messageLabel = new JLabel("");
			messageLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
			messageLabel.setForeground(new Color(88, 96, 105));
			rightDataPanel.add(messageLabel);

			valueLabel = new JLabel("--" + formatUnit());
			valueLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
			valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			rightDataPanel.add(valueLabel);

			dataPanel.add(rightDataPanel, BorderLayout.EAST);
			panel.add(dataPanel, BorderLayout.CENTER);
			
			statusLabel = new JLabel("OK", SwingConstants.CENTER);
			statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
			statusLabel.setOpaque(true);
			statusLabel.setForeground(Color.WHITE);
			statusLabel.setBackground(Dashboard.NORMAL_COLOR);
			statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			panel.add(statusLabel, BorderLayout.EAST);
		}
		
		/**
		 * Updates the row components on the Swing event dispatch thread.
		 *
		 * @param value the formatted measurement
		 * @param danger whether to display the warning state
		 * @param message the new message, {@code null} to retain it, or an empty string to clear it
		 */
		void update(String value, boolean danger, String message) {
			valueLabel.setText(value + formatUnit());
			statusLabel.setText(danger ? "!" : "OK");
			statusLabel.setBackground(danger ? Dashboard.DANGER_COLOR : Dashboard.NORMAL_COLOR);

			if (message != null) {
				updateMessage(message, danger);
			}
		}

		/**
		 * Replaces the row message and cancels any previous clearing timer.
		 * Nonempty informational messages expire after five real seconds; warning messages persist.
		 *
		 * @param message the non-null message to display
		 * @param danger whether the message represents a warning
		 */
		private void updateMessage(String message, boolean danger) {
			stopClearMessageTimer();
			messageVersion++;
			messageLabel.setText(message);

			if (!message.isEmpty() && !danger) {
				int versionToClear = messageVersion;
				clearMessageTimer = new Timer(INFO_MESSAGE_DURATION, event -> {
					if (messageVersion == versionToClear) {
						messageLabel.setText("");
					}
				});
				clearMessageTimer.setRepeats(false);
				clearMessageTimer.start();
			}
		}

		/**
		 * Stops a pending message-clearing timer, if one is running.
		 */
		private void stopClearMessageTimer() {
			if (clearMessageTimer != null && clearMessageTimer.isRunning()) {
				clearMessageTimer.stop();
			}
		}
		
		/**
		 * Formats the unit suffix for display after the value.
		 *
		 * @return an empty string when no unit is configured, otherwise a space followed by the unit
		 */
		private String formatUnit() {
			return unit.isEmpty() ? "" : " " + unit;
		}
	}
}
