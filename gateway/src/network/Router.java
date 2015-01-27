package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import collect.SensorData;

public class Router {

	private int typeOffset;
	private Config config;
	private boolean display;
	private List<Integer> allowForwarding;
	private List<Integer> allowForwardingOnWs;
	private CookieManager ckman;
	private List<HttpCookie> cookies;
	private boolean login = false;
	private String uri;
	private boolean TLS;
	private String tlsUri;

	public Router(Config config) {
		this.config = config;
	}

	public void init() {
		config.read();
		config.validate();

		this.uri = config.getUri();
		this.tlsUri = config.getTlsUri();
		this.TLS = config.getTLS();
		
		this.allowForwarding = config.getAllowForwarding();
		this.allowForwardingOnWs = config.getAllowForwardingOnWs();
		this.display = config.getDisplayResponse();
		this.typeOffset = config.getTypeOffset();
		
		ckman = new CookieManager();
		CookieHandler.setDefault(ckman);
		if (login) {
			config.validateLogin();
			try {
				this.login();
			} catch (IOException | LoginException e) {
				System.err.println("Trying to connect to " + getUri()
						+ this.getConfig().getLoginUrl() + " : "
						+ e.getMessage());
				System.exit(1);
			}
			testLogin();
		}
	}

	private void testLogin() {
		(new Thread() {
			private final int delay = 10000;

			public void run() {
				while (true) {
					try {
						Thread.sleep(delay);
						checkLogin();
					} catch (InterruptedException e) {
					}
				}

			}

			private void checkLogin() {
				StringBuilder builder = new StringBuilder();
				try {
					int resp = get(config.get(Config.CHECK_LOGIN_URL), builder);
					String response = builder.toString();
					JsonReader jsonReader = Json.createReader(new StringReader(
							response));
					JsonObject object = jsonReader.readObject();
					jsonReader.close();

					if (object.get("success") != null
							&& !Boolean.parseBoolean(object.get("success")
									.toString())) {
						System.out
								.println("You are logged out, we retry to log in every "
										+ delay / 1000 + "s");
						retryLogin(delay);
					}
				} catch (IOException e) {
					System.out
							.println("You are logged out, we retry to log in every "
									+ delay / 1000 + "s");
					retryLogin(delay);
				}
			}

			private void retryLogin(int delay) {
				try {
					Thread.sleep(delay);
					login();
				} catch (InterruptedException | IOException | LoginException e) {
					retryLogin(delay);
				}
			}
		}).start();
	}

	public void forward(SensorData sensorData) throws IOException,
			LoginException {
		sendPost(sensorData.toString(), sensorData.getValue(typeOffset));
		sendWs(sensorData.toString(), sensorData.getValue(typeOffset));
	}

	public void login() throws IOException, LoginException {
		String loginURL = config.get(Config.LOGIN_URL);
		String username = config.get(Config.USERNAME);
		String password = config.get(Config.PASSWORD);

		StringBuilder responseBuilder = new StringBuilder();

		int responseCode = post(loginURL, "username=" + username + "&password="
				+ password, responseBuilder);

		String response = responseBuilder.toString();

		if (responseCode != 200) {
			System.err.println("Login : server returned a http status code "
					+ response);
		} else {
			JsonReader jsonReader = Json
					.createReader(new StringReader(response));
			JsonObject object = jsonReader.readObject();
			jsonReader.close();
			if (!Boolean.parseBoolean(object.get("success").toString()))
				throw new LoginException(
						"Wrong username or password, gateway cannot login");
			else
				System.out.println("The gateway is logged in.");
		}
	}

	public int sendPost(String sensorData, int offset) throws IOException,
			LoginException {

		if (!allowForwarding.contains(offset)) {
			return 401;
		}

		String url = config.get(new Integer(offset).toString());

		String urlParameters = "data=" + sensorData;
		System.out.println("Sending on " + config.get(offset + "") + " : "
				+ sensorData);
		StringBuilder responseBuilder = new StringBuilder();
		int responseCode = post(url, urlParameters, responseBuilder);
		return responseCode;

	}

	public int sendWs(String sensorData, int offset) throws IOException {

		if (!allowForwardingOnWs.contains(offset)) {
			return 401;
		}
		WebSocket.send(sensorData);
		System.out.println("Sending on WS : " + sensorData);
		return 200;
	}

	private int post(String urlStr, String data, StringBuilder responseBuilder)
			throws IOException {
		String protocol = "http://";
		if (TLS)
			protocol = "https://";
		URL url = new URL(protocol + getUri() + urlStr);
		HttpURLConnection con;
		if (TLS)
			con = (HttpsURLConnection) url.openConnection();
		else
			con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null && cookies.size() > 0) {
			String cookStr = "";
			for (int i = 0; i < cookies.size(); i++) {
				cookStr += cookies.get(i).getName() + "="
						+ cookies.get(i).getValue() + " ; ";
			}
			con.setRequestProperty("Cookie", cookStr);
		}
		con.setConnectTimeout(2000);

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		if (display)
			System.out
					.print("Server response (HTTP code=" + responseCode + ")");
		if (responseCode < 400) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer responseBuffer = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				responseBuffer.append(inputLine);
			}
			in.close();

			if (responseBuilder != null)
				responseBuilder.append(responseBuffer.toString());
			if (display)
				System.out.print(responseBuffer.toString());
		}
		if (display)
			System.out.println();

		cookies = ckman.getCookieStore().getCookies();
		return responseCode;
	}

	public int get(String urlStr, StringBuilder responseBuilder)
			throws IOException {

		String protocol = "http://";
		if (TLS)
			protocol = "https://";
		URL url = new URL(protocol + getUri() + urlStr);
		
		HttpURLConnection con;
		if (TLS)
			con = (HttpsURLConnection) url.openConnection();
		else
			con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("GET");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null && cookies.size() > 0) {
			String cookStr = "";
			for (int i = 0; i < cookies.size(); i++) {
				cookStr += cookies.get(i).getName() + "="
						+ cookies.get(i).getValue() + " ; ";
			}
			con.setRequestProperty("Cookie", cookStr);
		}
		con.setConnectTimeout(2000);

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		if (responseBuilder != null)
			responseBuilder.append(response.toString());
		return responseCode;
	}

	public Config getConfig() {
		return config;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public String getUri() {
		// TODO Auto-generated method stub
		return TLS ? tlsUri : uri;
	}
}
