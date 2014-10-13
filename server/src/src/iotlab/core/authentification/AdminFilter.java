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
@WebFilter(urlPatterns={ //ADMIN ACCESS REQUIRED FOR THESE URLS
		"/admin/*",
		"/rest/sendCommand",
		"/rest/info/conf",
		"/rest/info/dbstats",
		"/rest/info/logs",
		"/rest/info/logs/*",
		"/rest/account/accounts",
		"/rest/account/register",
		"/rest/account/remove",
		"/rest/account/update",
		"/rest/geo/disable",
		"/rest/geo/enable",
		"/rest/geo/services"
	}) 
public class AdminFilter implements Filter {
	
	public AdminFilter() {

	}

	@EJB
	private AccountManager authentification;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession();

		if(!authentification.isLoggedIn(session)){
			if(request.getRequestURI().indexOf("/rest/")>-1){
				String message = "Authentification required";
				boolean success = false;
				JsonObjectBuilder builder = Json.createObjectBuilder();
				builder.add("success", success).add("message", message);
				String content = builder.build().toString();
				response.setContentLength(content.length());
				response.setContentType("application/json");
				response.getWriter().print(content);
			}else{
				session.setAttribute("redirect", request.getRequestURI().replaceFirst(request.getContextPath()+"/", ""));
				request.getRequestDispatcher("/WEB-INF/index.jsp?page=login").forward(request, response);
			}	
		}else if (!authentification.isAdmin(session)) {
			if(request.getRequestURI().indexOf("/rest/")>-1){
				String message = "Authentification required";
				boolean success = false;
				JsonObjectBuilder builder = Json.createObjectBuilder();
				builder.add("success", success).add("message", message);
				String content = builder.build().toString();
				response.setContentLength(content.length());
				response.setContentType("application/json");
				response.getWriter().print(content);
			}else{
				response.setStatus(403);
				request.getRequestDispatcher("/WEB-INF/index.jsp?page=403").forward(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}
	}
	


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
