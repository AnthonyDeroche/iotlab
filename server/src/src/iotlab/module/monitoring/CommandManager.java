/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.monitoring;

import iotlab.apps.api.WebSocket;

import javax.json.Json;

/**
 * 
 * @author Thierry Duhal
 *
 */
public class CommandManager {

	private static int commandVersionNumber = 1;
	public static final int TYPE = 30;
	private static final String RESET_COMMAND = "RESET_CVN";
	private static final String SINK_INFO_COMMAND = "SINK_ID_DVN";

	public CommandManager() {

	}

	public static int getCommandVersionNumber() {
		return commandVersionNumber;
	}

	public static boolean resetCommandVersionNumber() {
		commandVersionNumber = 65535;
		boolean success = CommandManager.sendCommand(RESET_COMMAND, true);
		commandVersionNumber = 1;
		return success;
	}

	public static boolean sendCommandSinkInfo() {
		return CommandManager.sendCommand(SINK_INFO_COMMAND, false);
	}

	public synchronized static boolean sendCommand(String command) {
		return sendCommand(command, false);
	}

	public synchronized static boolean sendCommand(String command, boolean reset) {

		if (WebSocket.getConnectedGatewayNumber() > 0) {
			if (commandVersionNumber >= 65535 && !reset) {
				resetCommandVersionNumber();
			}
			WebSocket.send(
					TYPE,
					Json.createObjectBuilder()
							.add("command",
									commandVersionNumber + " " + command)
							.build());
			commandVersionNumber++;
			return true;
		} else {
			return false;
		}

	}

}
