/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps.api;

import iotlab.core.authentification.AccountException;
import iotlab.core.authentification.AccountManager;
import iotlab.core.authentification.Member;
import iotlab.module.monitoring.LogManager;
import iotlab.utils.Conversion;

import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Path("account")

public class AccountResource extends RestResource {

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("application/json")
	@Path("login")
	public Response login(@FormParam("username") String username,
			@FormParam("password") String password) {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		String message = "";
		Boolean success = false;

		ResponseBuilder response;
		try {

			accountManager.tryLogin(request.getSession(), username, password,
					LogManager.getIP(request));
			message = "Authentification successful";
			success = true;
			builder.add("success", success).add("message", message);
			
			logManager.info(request.getSession(), "Account", message,
					LogManager.getIP(request));

		} catch (AccountException e) {
			message = e.getMessage();
			success = false;
			builder.add("success", success).add("message", message);
			logManager.warning(request.getSession(), "Account", e.getMessage(),
					LogManager.getIP(request));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {

			}
		}
		response = Response.ok().entity(builder.build().toString());
		return response.build();
	}

	@GET
	@Produces("application/json")
	@Path("logout")
	public Response logout() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		String message = "You have successfully logged out";
		Boolean success = true;
		builder.add("success", success).add("message", message);
		logManager.info(request.getSession(), "Account", message,
				LogManager.getIP(request));
		request.getSession().invalidate();
		ResponseBuilder response = Response.ok().entity(
				builder.build().toString());
		return response.build();
	}

	@GET
	@Produces("application/json")
	@Path("isLoggedIn")
	public Response isLoggedIn() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		boolean success;
		String username;

		if (accountManager.isLoggedIn(request.getSession())) {
			success = true;
			username = ((Member) request.getSession().getAttribute(
					AccountManager.USER_SESSION)).getUsername();
		} else {
			success = false;
			username = "";
		}

		return Response
				.ok()
				.entity(builder.add("success", success)
						.add("username", username).build().toString()).build();
	}
	
	@GET
	@Produces("application/json")
	@Path("accounts")
	public String getAccounts(){
		JsonObjectBuilder builder = Json.createObjectBuilder();
		List<Member> members = dao.getMemberDAO().getAll("username",true);
		return builder.add("accounts", Conversion.listToJson(members)).build().toString();
		
	}

	@POST
	@Produces("application/json")
	@Path("register")
	public String register(@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("email") String email, @FormParam("admin") boolean admin) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		boolean success;
		String message;

		try {
			accountManager.tryRegister(username, password, email, admin);
			success = true;
			message = "Registration successful";
		} catch (AccountException e) {
			success = false;
			message = e.getMessage();
		}

		return builder.add("success", success).add("message", message).build()
				.toString();
	}

	@POST
	@Produces("application/json")
	@Path("remove")
	public String removeAccount(@FormParam("username") String username) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		boolean success;
		String message;
		try {
			accountManager.remove(username);
			success = true;
			message = "This account has been removed";
		} catch (AccountException e) {
			success = false;
			message = e.getMessage();
		}

		return builder.add("success", success).add("message", message).build()
				.toString();
	}

	@POST
	@Produces("application/json")
	@Path("update")
	public String update(@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("email") String email, @FormParam("admin") boolean admin) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		boolean success;
		String message;

		try {
			accountManager.update(username, password, email, admin);
			success = true;
			message = "Account has been successfully updated";
		} catch (AccountException e) {
			success = false;
			message = e.getMessage();
		}

		return builder.add("success", success).add("message", message).build()
				.toString();
	}
}
