package network;

import java.util.List;

public interface Config {

	public void read();

	public String get(String key);

	public void put(String key, String value);

	public boolean containsKey(String key);

	public boolean containsValue(String value);

	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String CONTEXT = "context";
	public static final String TLS = "TLS";
	public static final String TLS_PORT = "TLSport";

	public static final String DISPLAY_RESPONSE = "displayResponse";

	public static final String TYPE_OFFSET = "typeOffset";
	public static final String ALLOW_FORWARDING = "allowForwarding";

	public static final String ALLOW_FORWARDING_ON_WS = "allowForwardingOnCommandFlow";
	public static final String COMMAND_WS_URL = "commandWsUrl";

	public static final String LOGIN_URL = "loginURL";
	public static final String CHECK_LOGIN_URL = "checkLoginUrl";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	public boolean getDisplayResponse();

	public String getHost();

	public int getPort();

	public boolean getTLS();

	public String getContext();

	public List<Integer> getAllowForwarding();

	public List<Integer> getAllowForwardingOnWs();

	public String getUri();

	public String getCheckLoginUrl();

	public String getPassword();

	public String getUsername();

	public String getLoginUrl();

	public int getTlsPort();

	public void validate();

	public void validateLogin();

	public void validateCommands();

	public String getTlsUri();
}
