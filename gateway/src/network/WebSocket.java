package network;

import java.io.IOException;
import java.io.StringReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import collect.Gateway;

public class WebSocket extends Endpoint implements Runnable,
		MessageHandler.Whole<String> {

	private static String commandFlow;
	private static Queue<Session> queue;
	private ClientEndpointConfig config;
	private static boolean tls;
	private final int DELAY_RECONNECT = 10000;
	
	private static String uri;
	private static String tlsUri;

	public WebSocket() {

	}

	public static void init(Config config) {
		config.validate();
		config.validateCommands();
		WebSocket.commandFlow = "";
		String cmdUrl = config.get(Config.COMMAND_WS_URL);
		if (cmdUrl != null) {
			WebSocket.commandFlow = cmdUrl;
		}
		WebSocket.uri = config.getUri();
		WebSocket.tlsUri = config.getTlsUri();
		WebSocket.tls = config.getTLS();
	}

	public void reconnect(final int delay) {

		System.out.println("Trying to reconnect on " + commandFlow + " in "
				+ DELAY_RECONNECT / 1000 + "s");

		try {
			Thread.sleep(delay);
			connect();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
	}

	public void connect() {
		queue = new ConcurrentLinkedQueue<>();
		WebSocketContainer container;
		Session session;
		ClientEndpointConfig.Builder configBuilder = ClientEndpointConfig.Builder
				.create();
		List<HttpCookie> cookies = ((CookieManager) CookieHandler.getDefault())
				.getCookieStore().getCookies();
		configBuilder.configurator(new SessionAwareConfig(cookies));
		config = configBuilder.build();
		String protocol = WebSocket.tls ? "wss://" : "ws://";
		String uri = WebSocket.tls ? WebSocket.tlsUri : WebSocket.uri;
		try {

			container = ContainerProvider.getWebSocketContainer();
			session = container.connectToServer(WebSocket.class, config,
					URI.create(protocol + uri + commandFlow));
			queue.add(session);
			session.addMessageHandler(this);

			System.out.println("WebSocket connected on " + protocol + uri
					+ commandFlow);
			// wait4TerminateSignal();
		} catch (DeploymentException | IOException e) {
			System.err.println("Websocket - trying to connect to " + protocol +uri
					+ commandFlow + " : " + e.getMessage());
			this.reconnect(DELAY_RECONNECT);
		}
	}

	public void run() {
		this.connect();
	}

	/*
	 * private void wait4TerminateSignal() { synchronized (this) { try {
	 * this.wait(); } catch (InterruptedException e) { } } }
	 */

	/*
	 * @Override
	 * 
	 * @OnOpen public void onOpen(Session session, EndpointConfig config) {
	 * 
	 * 
	 * }
	 */

	public static void send(String data) {
		try {
			for (Session session : queue) {
				if (session.isOpen()) {
					session.getBasicRemote().sendObject(data);
				}
			}
		} catch (IOException | EncodeException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void onMessage(String message) {

		JsonReader jsonReader = Json.createReader(new StringReader(message));
		JsonObject object = jsonReader.readObject();
		jsonReader.close();
		if (object.get("unauthorized") != null) {
			System.out.println("You need authorization to access this stream");
			this.reconnect(DELAY_RECONNECT);
		} else if (object.get("command") != null) {
			System.out.println("Received command: " + message);
			Gateway.commandHandler.handleIncomingCommand(object);
		}
	}

	@Override
	public void onClose(final Session session, CloseReason closeReason) {
		System.out.println(String.format(
				"Websocket : Session %s closed because of %s", session.getId(),
				closeReason));
		this.reconnect(DELAY_RECONNECT);
	}

	@Override
	public void onOpen(final Session session, EndpointConfig arg1) {

	}
}