/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.core.authentification;

import java.io.IOException;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author Anthony Deroche
 *
 */
@WebFilter("/rest/*") 
public class ApiFilter implements Filter {
	
	//all urls are protected on POST requests except those in this table
	private String[] exceptions = {"/account/login"}; 

	public ApiFilter() {

	}

	@EJB
	private AccountManager authentification;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession();

		String message = "Authentification required";
		boolean success = false;
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("success", success).add("message", message);

		if (!this.isException(request) && request.getMethod().equals("POST")
				&& !authentification.isLoggedIn(session)) {
			response.setContentType("application/json");
			response.getWriter().print(builder.build().toString());
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isException(HttpServletRequest request) {
		String path = request.getPathInfo();
		for (int i = 0; i < exceptions.length; i++) {
			if (path.equals(exceptions[i]))
				return true;
		}
		return false;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
