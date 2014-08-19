package network;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;

public class SessionAwareConfig extends ClientEndpointConfig.Configurator {

	private List<HttpCookie> cookies;

	public SessionAwareConfig(List<HttpCookie> cookies) {
		this.cookies = cookies;
	}

	@Override
	public void beforeRequest(Map<String, List<String>> headers) {

		List<String> cookiesList = new ArrayList<>();
		for (HttpCookie c : cookies) {
			cookiesList.add(c.getName() + "=" + c.getValue());
		}

		headers.put("cookie", cookiesList);
	}

}