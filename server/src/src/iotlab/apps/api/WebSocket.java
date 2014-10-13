/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps.api;

import iotlab.core.authentification.AccountManager;
import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.dao.DAOManager;
import iotlab.core.beans.entity.mote.Sink;
import iotlab.core.inputStream.InputStream;
import iotlab.core.inputStream.InvalidDataException;
import iotlab.core.inputStream.SinkStream;
import iotlab.core.inputStream.strategy.DoubleDefaultStrategy;
import iotlab.module.monitoring.CommandManager;
import iotlab.module.monitoring.LogManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Stateless
@ServerEndpoint(value = "/liveStream/{type}", configurator = SessionAwareConfig.class)
public class WebSocket {

	private static Queue<Session> queue = new ConcurrentLinkedQueue<>();

	@EJB
	private LogManager logManager;
	@EJB
	private AccountManager authentification;
	@EJB
	private DAOManager dao;

	//prevent from opening session on a specific type if the user is not logged in
	private final List<Integer> protectedTypes = Arrays.asList(new Integer[] {CommandManager.TYPE });

	@OnOpen
	public void openConnection(Session session, EndpointConfig config,
			@PathParam("type") final int type) {

		HttpSession httpSession = (HttpSession) config.getUserProperties().get(
				"httpSession");

		if (protectedTypes.contains(type)
				&& !authentification.isLoggedIn(httpSession)) {
			logManager.warning(httpSession, "Websocket [type=" + type + "]",
					"openConnection : authorization required",
					(String) httpSession.getAttribute("ip"));
			session.getAsyncRemote().sendObject(
					Json.createObjectBuilder()
							.add("unauthorized", "Authorization required")
							.build());
		} else {
			session.getUserProperties().put("type", type);
			queue.add(session);
			logManager.info(httpSession, "Websocket [type=" + type + "]",
					"connection opened",
					(String) httpSession.getAttribute("ip"));

			if (type == CommandManager.TYPE) {
				CommandManager.resetCommandVersionNumber();
				CommandManager.sendCommandSinkInfo();
			}
		}
	}

	public static void send(int type, JsonObject json) {
		// try {
		// System.out.println("Send on WS ("+queue.size()+" client connected)");
		for (Session session : queue) {
			if (session.isOpen()
					&& (int) (session.getUserProperties().get("type")) == type) {
				// System.out.println("Send on WS "+type);
				session.getAsyncRemote().sendObject(json);
			}
		}
		/*
		 * } catch (IOException | EncodeException e) { logManager.warning(null,
		 * "Websocket [type=" + type + "]",e.getMessage()); }
		 */
	}

	public static void sendToSink(String mac, JsonObject json) {
		try {
			boolean found = false;
			for (Session session : queue) {
				if (session.isOpen()
						&& ((String) session.getUserProperties().get("sink"))
								.equals(mac)) {
					session.getBasicRemote().sendObject(json);
					found = true;
					break;
				}
			}
			if (!found) {
				// logManager.warning(null,
				// "Websocket","No WS session found associated with sink " +
				// mac);
			}
		} catch (IOException | EncodeException e) {
			// logManager.warning(null, "Websocket", e.getMessage());
		}
	}

	@OnMessage
	public void onMessage(final Session session, EndpointConfig config,
			String msg) {
		try {

			InputStream<Sink, Double> sinkStream = new SinkStream(msg, dao);
			sinkStream.convert(DoubleDefaultStrategy.class);

			if (sinkStream.getDataList().size() == 1) {
				String mac = sinkStream.getDataList().get(0).getMac();
				session.getUserProperties().put("sink", mac);
				System.out.println("Associated the sink " + mac
						+ " with session " + session.getId());
			}
		} catch (DAOException | InvalidDataException e) {
			System.out.println("[WS] " + e.getMessage());
		}
	}

	@OnClose
	public void onClose(Session session, EndpointConfig config,
			CloseReason closeReason) {
		HttpSession httpSession = (HttpSession) config.getUserProperties().get(
				"httpSession");
		logManager.info(
				httpSession,
				"WebSocket",
				String.format("Session %s closed because of %s",
						session.getId(), closeReason),
				(String) httpSession.getAttribute("ip"));
	}

	public static int getConnectedGatewayNumber() {
		int cnt = 0;
		for (Session s : queue) {
			if (s.getUserProperties().get("type") != null
					&& (int) s.getUserProperties().get("type") == CommandManager.TYPE)
				cnt++;
		}
		return cnt;
	}
}
