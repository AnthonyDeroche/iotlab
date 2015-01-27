package network;

import javax.json.JsonObject;

import collect.SerialConnection;

public class CommandHandler {

	private SerialConnection serialConnection;

	public CommandHandler(SerialConnection serialConnection) {
		this.serialConnection = serialConnection;
	}

	public void handleIncomingCommand(JsonObject object) {
		this.serialConnection.writeSerialData(object.getString("command"));
	}

	public SerialConnection getSerialConnection() {
		return serialConnection;
	}

	public void setSerialConnection(SerialConnection serialConnection) {
		this.serialConnection = serialConnection;
	}

}
