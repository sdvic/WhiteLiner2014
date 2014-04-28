package org.wintrisstech.sensors;

import android.os.SystemClock;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PulseInput.PulseMode;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * An UltraSonicSensors instance is used to access three ultrasonic sensors
 * (leftInput, frontInput, and rightInput) and read the measurements from these
 * sensors.
 * 
 * @author Erik Colban
 */
public class UltraSonicSensors {

	private static final String TAG = "UltraSonicSensor";
	private static final float CONVERSION_FACTOR = 17280.0F; // cm / s
	private static int LEFT_ULTRASONIC_INPUT_PIN = 35;
	private static int FRONT_ULTRASONIC_INPUT_PIN = 36;
	private static int RIGHT_ULTRASONIC_INPUT_PIN = 37;
	private static final int LEFT_STROBE_ULTRASONIC_OUTPUT_PIN = 15;
	private static final int FRONT_STROBE_ULTRASONIC_OUTPUT_PIN = 16;
	private static final int RIGHT_STROBE_ULTRASONIC_OUTPUT_PIN = 17;
	private PulseInput leftInput;
	private PulseInput frontInput;
	private PulseInput rightInput;
	private DigitalOutput leftStrobe;
	private DigitalOutput frontStrobe;
	private DigitalOutput righttStrobe;
	private volatile int leftDistance;
	private volatile int frontDistance = 10;
	private volatile int rightDistance;
	private IOIO ioio;

	/**
	 * Constructor of a UltraSonicSensors instance.
	 * 
	 * @param ioio
	 *            the IOIO instance used to communicate with the sensor
	 * @throws ConnectionLostException
	 * 
	 */
	public UltraSonicSensors(IOIO ioio) throws ConnectionLostException {
		this.ioio = ioio;
		this.leftStrobe = ioio
				.openDigitalOutput(LEFT_STROBE_ULTRASONIC_OUTPUT_PIN);// *******
		this.righttStrobe = ioio
				.openDigitalOutput(RIGHT_STROBE_ULTRASONIC_OUTPUT_PIN);// *******
		this.frontStrobe = ioio
				.openDigitalOutput(FRONT_STROBE_ULTRASONIC_OUTPUT_PIN);// *******
	}
	/**
	 * Makes a reading of the ultrasonic sensors and stores the results locally.
	 * To access these readings, use {@link #getLeftDistance()},
	 * {@link #getFrontDistance()}, and {@link #getRightDistance()}.
	 * 
	 * @throws ConnectionLostException
	 * @throws InterruptedException
	 */
	public void read() throws ConnectionLostException, InterruptedException {
		leftDistance = read(leftStrobe, leftInput, LEFT_ULTRASONIC_INPUT_PIN);
		// frontDistance = read(frontStrobe, frontInput, FRONT_ULTRASONIC_INPUT_PIN );
		rightDistance = read(righttStrobe, rightInput, RIGHT_ULTRASONIC_INPUT_PIN);
	}

	private int read(DigitalOutput strobe, PulseInput input, int inputPin)
			throws ConnectionLostException, InterruptedException {
		int distance = 0;
		ioio.beginBatch();
		strobe.write(true);
		input = ioio.openPulseInput(inputPin, PulseMode.POSITIVE);
		ioio.endBatch();
		SystemClock.sleep(20);
		strobe.write(false);
		distance += (int) (input.getDuration() * CONVERSION_FACTOR);
		input.close();
		return distance;
	}

	/**
	 * Gets the last read distance in cm of the leftInput sensor
	 * 
	 * @return the leftInput distance in cm
	 */
	public synchronized int getLeftDistance() {
		return leftDistance;
	}

	/**
	 * Gets the last read distance in cm of the frontInput sensor
	 * 
	 * @return the frontInput distance in cm
	 */
	public synchronized int getFrontDistance() {
		return frontDistance;
	}

	/**
	 * Gets the last read distance in cm of the rightInput sensor
	 * 
	 * @return the rightInput distance in cm
	 */
	public synchronized int getRightDistance() {
		return rightDistance;
	}

	/**
	 * Closes all the connections to the used pins
	 */
	public void closeConnection() {
		leftInput.close();
		frontInput.close();
		rightInput.close();
		leftStrobe.close();
	}
}
