/*
 * Copyright (c) 2008, Swedish Institute of Computer Science.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * -----------------------------------------------------------------
 *
 * CollectServer
 *
 * Authors : Joakim Eriksson, Niclas Finne
 * Created : 3 jul 2008
 */

package collect;

import java.awt.event.ActionEvent;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.AbstractAction;

import network.CommandHandler;
import network.Config;
import network.LocalConfig;
import network.LoginException;
import network.Router;
import network.WebSocket;

/**
 *
 */
public class Gateway implements SerialConnectionListener {

	public static final String STDIN_COMMAND = "<STDIN>";

	public static final String CONFIG_FILE = "collect.conf";
	public static final String SENSORDATA_FILE = "sensordata.log";
	public static final String CONFIG_DATA_FILE = "collect-data.conf";
	public static final String INIT_SCRIPT = "collect-init.script";
	public static final String FIRMWARE_FILE = "collect-view-shell.ihex";

	private Properties configTable = new Properties();

	private ArrayList<SensorData> sensorDataList = new ArrayList<SensorData>();
	private PrintWriter sensorDataOutput;
	private SerialConnection serialConnection;

	/* Do not auto send init script at startup */
	private boolean doSendInitAtStartup = false;

	private boolean hasStarted = false;

	public void start(SerialConnection connection) {
		if (hasStarted) {
			throw new IllegalStateException("already started");
		}
		hasStarted = true;
		this.serialConnection = connection;
		connectToSerial();
	}

	protected void connectToSerial() {
		if (serialConnection != null && !serialConnection.isOpen()) {
			String comPort = null;
			boolean first = true;
			do {
				comPort = serialConnection.getComPort();
				if (comPort == null
						&& serialConnection.isMultiplePortsSupported()) {
					comPort = MoteFinder.selectComPort();
				}
				if (comPort != null
						|| !serialConnection.isMultiplePortsSupported()) {
					serialConnection.open(comPort);
				}

				if (comPort == null && first) {
					System.err.print("Could not find any connected motes.");
					first = false;
				}

				int delay = 1;

				try {
					System.err.print(".");
					System.err.flush();
					Thread.sleep(delay * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} while (comPort == null);
		}
	}

	public void stop() {
		if (serialConnection != null) {
			serialConnection.close();
		}
		PrintWriter output = this.sensorDataOutput;
		if (output != null) {
			output.close();
		}

	}

	public boolean sendToNode(String data) {
		if (serialConnection != null && serialConnection.isOpen()
				&& serialConnection.isSerialOutputSupported()) {
			serialConnection.writeSerialData(data);
			return true;
		}
		return false;
	}

	private static int data_cnt = 0;

	public void handleIncomingData(long systemTime, String line) {
		if (line.length() == 0 || line.charAt(0) == '#') {
			// Ignore empty lines, comments, and annotations.
			return;
		}
		SensorData sensorData = SensorData.parseSensorData(this, line,
				systemTime);
		if (sensorData != null) {
			// Sensor data received
			if (data_cnt == 0)
				System.out
						.println("\n**********************   DATA   **********************\n");
			data_cnt++;
			handleSensorData(sensorData);
			return;
		} else {
			System.out.println("SERIAL: " + line);
		}

	}

	// -------------------------------------------------------------------
	// SensorData handling
	// -------------------------------------------------------------------

	public int getSensorDataCount() {
		return sensorDataList.size();
	}

	public SensorData getSensorData(int i) {
		return sensorDataList.get(i);
	}

	private void handleSensorData(final SensorData sensorData) {

		(new Thread() {
			public void run() {
				try {
					router.forward(sensorData);
				} catch (SocketTimeoutException e) {
					System.err.println("Timeout : cannot connect to server");

				} catch (IOException e) {
					// e.printStackTrace();
					System.err.println("An error occured : " + e.getClass()
							+ " : " + e.getMessage());
				} catch (LoginException e) {
					System.err.println("Trying to connect to "
							+ router.getUri()
							+ router.getConfig().get(Config.LOGIN_URL) + " : "
							+ e.getMessage());
					System.exit(1);
				}
			}
		}).start();
	}

	protected class ConnectSerialAction extends AbstractAction implements
			Runnable {

		private static final long serialVersionUID = 1L;

		private boolean isRunning;

		public ConnectSerialAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			if (!isRunning) {
				isRunning = true;
				new Thread(this, "serial").start();
			}
		}

		public void run() {
			try {
				if (serialConnection != null) {
					if (serialConnection.isOpen()) {
						serialConnection.close();
					} else {
						connectToSerial();
					}
				} else {
					System.err.println("No serial connection configured");
				}
			} finally {
				isRunning = false;
			}
		}

	}

	protected class MoteProgramAction extends AbstractAction implements
			Runnable {

		private static final long serialVersionUID = 1L;

		private boolean isRunning = false;

		public MoteProgramAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			if (!isRunning) {
				isRunning = true;
				new Thread(this, "program thread").start();
			}
		}

		@Override
		public void run() {
			try {
				MoteProgrammer mp = new MoteProgrammer();
				mp.setFirmwareFile(FIRMWARE_FILE);
				mp.searchForMotes();
				String[] motes = mp.getMotes();
				if (motes == null || motes.length == 0) {
					System.err.println("Could not find any connected nodes");
					return;
				}
				System.out
						.println("Found " + motes.length + " connected nodes");

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Programming failed: " + e);
			} finally {
				isRunning = false;
			}
		}

	}

	// -------------------------------------------------------------------
	// SerialConnection Listener
	// -------------------------------------------------------------------

	@Override
	public void serialData(SerialConnection connection, String line) {
		handleIncomingData(System.currentTimeMillis(), line);
	}

	@Override
	public void serialOpened(SerialConnection connection) {
		String connectionName = connection.getConnectionName();
		System.out.println("*** Serial console listening on " + connectionName
				+ " ***");
		if (connection.isMultiplePortsSupported()) {
			String comPort = connection.getComPort();
			// Remember the last selected serial port
			configTable.put("collect.serialport", comPort);
		}
		System.out.println("connected to " + connectionName);

		if (!connection.isSerialOutputSupported()) {
			System.out.println("*** Serial output not supported ***");
		} else if (doSendInitAtStartup) {
			// Send any initial commands
			doSendInitAtStartup = false;
		}
	}

	@Override
	public void serialClosed(SerialConnection connection) {
		/*
		 * String prefix; if (hasSerialOpened) {
		 * System.out.println("*** Serial connection terminated ***"); prefix =
		 * "Serial connection terminated.\n"; hasSerialOpened = false; } else {
		 * prefix = "Failed to connect to " + connection.getConnectionName() +
		 * '\n'; } System.out.println(prefix);
		 */
	}

	// -------------------------------------------------------------------
	// Main
	// -------------------------------------------------------------------

	private static Router router;
	public static CommandHandler commandHandler;

	public static void main(String[] args) {

		SerialConnection serialConnection;
		Gateway server = new Gateway();
		serialConnection = new SerialDumpConnection(server);
		Config config = new LocalConfig("config.properties");
		router = new Router(config);

		boolean command = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-login")) {
				router.setLogin(true);
			} else if (args[i].equals("-command")) {
				command = true;
			} else if (args[i].equals("-u")) {
				if ((++i) < args.length) {
					router.getConfig().put(Config.USERNAME, args[i]);
				} else {
					System.err.println("Missing username after -u");
					System.exit(1);
				}

				Console console = System.console();
				System.out.println("Password : ");
				String password;
				if (console != null) {
					password = new String(console.readPassword());
				} else {
					Scanner sc = new Scanner(System.in);
					password = sc.nextLine();
					sc.close();
				}
				router.getConfig().put(Config.PASSWORD, password);
			}
		}

		router.init();
		printInfos();
		
		if (command) {
			commandHandler = new CommandHandler(serialConnection);
			WebSocket ws = new WebSocket();
			WebSocket.init(config);
			(new Thread(ws)).start();
		}

		server.start(serialConnection);

		

	}

	private static void printInfos() {
		System.out.println("-------------------------------------");
		System.out.println("Welcome !");
		System.out
				.println("This gateway links a sink plugged in a USB port and a server by sending HTTP requests.");
		System.out
				.println("This software is using parts of collect-view, a Contiki app.");
		System.out
				.println("For further information, feel free to contact the contributors (anthony.deroche@telecomnancy.net, thierry.duhal@telecomnancy.net)");
		System.out.println("-------------------------------------");
	}
}
