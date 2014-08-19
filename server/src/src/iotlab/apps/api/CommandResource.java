/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps.api;

import iotlab.module.monitoring.CommandManager;
import iotlab.module.monitoring.LogManager;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Thierry Duhal
 *
 */
@Path("sendCommand")
public class CommandResource extends RestResource {

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("application/json")
	public String add(@FormParam("data") String command) {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		String message = "";
		Boolean success = false;
		
		success = CommandManager.sendCommand(command);
		if(success)
			message = "Command has been successfully forwarded";
		else
			message = "Could not find any connected gateway";
		
		logManager.info(request.getSession(), "Command", message,LogManager.getIP(request));

		builder.add("success", success).add("message", message);
		return builder.build().toString();
	}
}
