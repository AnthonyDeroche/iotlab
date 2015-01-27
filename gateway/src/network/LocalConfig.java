package network;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class LocalConfig implements Config {

	private HashMap<String, String> map = new HashMap<>();
	private String file;
	private Properties prop = new Properties();
	private InputStream input = null;
	private boolean displayResponse;
	private String host;
	private int port;
	private boolean tls;
	private String context;
	private int typeOffset;
	private List<Integer> allowForwarding = new ArrayList<>();;
	private List<Integer> allowForwardingOnWs = new ArrayList<>();;
	private String uri;
	private String commandWsUrl;
	private String checkLoginUrl;
	private String password;
	private String username;
	private String loginUrl;
	private boolean isValidCommand = false;
	private boolean isValidLogin = false;
	private boolean isValid = false;
	private int tlsPort;
	private String tlsUri;

	public LocalConfig(String file) {
		prop = new Properties();
		input = null;
		this.file = file;
	}

	public void read() {
		try {

			input = new FileInputStream(file);

			// load a properties file
			prop.load(input);

			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				map.put(key, prop.getProperty(key));
			}
			 System.out.println(map);

		} catch (IOException ex) {
			System.err.println("Missing or malformed file " + file);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String get(String key) {
		return map.get(key);
	}

	public void put(String key, String value) {
		map.put(key, value);
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	public boolean containsValue(String value) {
		return map.containsValue(value);
	}

	public void missingParameter(String parameter) {
		this.missingParameter(parameter, "");
	}

	public void missingParameter(String parameter, String message) {
		System.err
				.println("Either parameter '"
						+ parameter
						+ "' is missing, or the format is wrong. Check it in properties file");
		if (message.length() > 0)
			System.err.println(parameter + " " + message);
		System.exit(1);
	}

	public void validate() {

		if (this.isValid)
			return;

		if (!this.containsKey(Config.HOST))
			missingParameter(Config.HOST);
		else
			this.host = this.get(Config.HOST);

		try {
			if (!this.containsKey(Config.PORT))
				throw new NumberFormatException();
			this.port = Integer.parseInt(this.get(Config.PORT));
			if (port > 65535)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			missingParameter(Config.PORT, "must be an integer < 65536");
		}

		if (!this.containsKey(Config.CONTEXT))
			missingParameter(Config.CONTEXT);
		else {
			this.context = this.get(Config.CONTEXT);
			if (context.charAt(0) != '/')
				this.context = "/" + context;
		}

		if (!this.containsKey(Config.TLS))
			missingParameter(Config.TLS);
		else
			this.tls = Boolean.parseBoolean(this.get(Config.TLS));

		if (!this.containsKey(Config.TLS_PORT) && this.tls)
			missingParameter(Config.TLS_PORT);
		else
			this.tlsPort = Integer.parseInt(this.get(Config.TLS_PORT));

		this.uri = host + ":" + port + context;
		this.tlsUri = host + ":"+ tlsPort + context;

		if (this.containsKey(Config.DISPLAY_RESPONSE))
			this.displayResponse = Boolean.parseBoolean(this
					.get(Config.DISPLAY_RESPONSE));
		else
			missingParameter(Config.DISPLAY_RESPONSE);

		if (this.containsKey(Config.TYPE_OFFSET))
			try {
				String typeOffsetStr = this.get(Config.TYPE_OFFSET);
				this.typeOffset = Integer.parseInt(typeOffsetStr);
				if (this.typeOffset < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				missingParameter(Config.TYPE_OFFSET, "must be an integer");
			}
		else
			missingParameter(Config.TYPE_OFFSET);

		String af = this.get(Config.ALLOW_FORWARDING);
		this.allowForwarding = new ArrayList<>();
		if (this.containsKey(Config.ALLOW_FORWARDING)) {
			String split[] = af.split(",");

			for (int i = 0; i < split.length; i++) {
				try {
					allowForwarding.add(Integer.parseInt(split[i]));
				} catch (NumberFormatException e) {

				}
			}
		} else {
			missingParameter(Config.ALLOW_FORWARDING);
		}

		this.isValid = true;

	}

	@Override
	public void validateLogin() {

		if (this.isValidLogin)
			return;

		if (!this.containsKey(Config.LOGIN_URL))
			missingParameter(Config.LOGIN_URL);
		else
			this.loginUrl = this.get(Config.LOGIN_URL);

		if (!this.containsKey(Config.USERNAME))
			missingParameter(Config.USERNAME);
		else
			this.username = this.get(Config.USERNAME);

		if (!this.containsKey(Config.PASSWORD))
			missingParameter(Config.PASSWORD);
		else
			this.password = this.get(Config.PASSWORD);

		if (!this.containsKey(Config.CHECK_LOGIN_URL))
			missingParameter(Config.CHECK_LOGIN_URL);
		else
			this.checkLoginUrl = this.get(Config.CHECK_LOGIN_URL);

		this.isValidLogin = true;
	}

	@Override
	public void validateCommands() {

		if (this.isValidCommand)
			return;

		String afows = this.get(Config.ALLOW_FORWARDING_ON_WS);
		this.allowForwardingOnWs = new ArrayList<>();
		if (this.containsKey(Config.ALLOW_FORWARDING_ON_WS)) {
			String split[] = afows.split(",");
			for (int i = 0; i < split.length; i++) {
				try {
					allowForwardingOnWs.add(Integer.parseInt(split[i]));
				} catch (NumberFormatException e) {
					System.err.println(e.getMessage());
				}
			}
		}

		if (!this.containsKey(Config.COMMAND_WS_URL))
			missingParameter(Config.COMMAND_WS_URL);
		this.commandWsUrl = this.get(Config.COMMAND_WS_URL);

		this.isValidCommand = true;

	}

	public boolean isTls() {
		return tls;
	}

	public int getTypeOffset() {
		return typeOffset;
	}

	public String getCheckLoginUrl() {
		return checkLoginUrl;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public String getCommandWsUrl() {
		return commandWsUrl;
	}

	public boolean getDisplayResponse() {
		return displayResponse;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public boolean getTLS() {
		return tls;
	}

	public String getContext() {
		return context;
	}

	public List<Integer> getAllowForwarding() {
		return allowForwarding;
	}

	public List<Integer> getAllowForwardingOnWs() {
		return allowForwardingOnWs;
	}

	public String getUri() {
		return uri;
	}

	public int getTlsPort() {
		return tlsPort;
	}
	
	public String getTlsUri(){
		return tlsUri;
	}
	

}
