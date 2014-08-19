/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps.api;

import iotlab.core.beans.dao.DAOException;
import iotlab.core.inputStream.InvalidDataException;
import iotlab.core.inputStream.strategy.DoubleDefaultStrategy;
import iotlab.module.data.Data;
import iotlab.module.data.DataStream;
import iotlab.module.monitoring.LogManager;
import iotlab.utils.Conversion;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Anthony Deroche - Thierry Duhal
 *
 */
@Path("data")
public class DataResource extends RestResource {

	public DataResource() {
	}

	/**
	 * 
	 * @param experiment
	 *            The experiment
	 * @param label
	 *            The label or labels list (separated with -)
	 * @param nb
	 *            The number of data expected
	 * @param mote
	 *            The mote or motes list (separated with -)
	 * @param from
	 *            The lower bound for timestamp
	 * @param to
	 *            The upper bound for timestamp
	 * @return The json response
	 */
	@GET
	@Produces("application/json")
	@Path("{experiment : \\d+}/{label}/{nb : (\\d+)}{s1:/?}{mote : (\\d+\\.\\d+(-\\d+\\.\\d+)*)?}{s2:/?}{from : (\\d+)?}{s3:/?}{to : (\\d+)?}")
	public String get(@PathParam("experiment") int experiment,
			@PathParam("label") String label, @PathParam("nb") int nb,
			@PathParam("mote") String mote, @PathParam("from") long from,
			@PathParam("to") long to) {

		List<Data> data = new ArrayList<Data>();

		data = dao.getDataDAO().getMeans(false, nb, experiment, label, mote,
				from, to);

		logManager.info(request.getSession(), "Data",
				"GET " + request.getPathInfo(), LogManager.getIP(request));

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("data", Conversion.listToJson(data));
		return builder.build().toString();
	}

	/**
	 * 
	 * @param experiment
	 *            The experiment
	 * @param label
	 *            The label or labels list (separated with -)
	 * @param filter
	 *            first or last data for each mote
	 * @param mote
	 *            The mote or motes list (separated with -)
	 * @return The json response
	 */
	@GET
	@Produces("application/json")
	@Path("{experiment : \\d+}/{label}/{filter : (last|first)}{s1:/?}{mote : (\\d+\\.\\d+(-\\d+\\.\\d+)*)?}{s2:/?}{from : (\\d+)?}{s3:/?}{to : (\\d+)?}")
	public String get(@PathParam("experiment") int experiment,
			@PathParam("label") String label,
			@PathParam("filter") String filter, @PathParam("mote") String mote) {

		List<Data> data = new ArrayList<Data>();
		if (filter.equals("last")) {
			data = dao.getDataDAO().getLastDataForEachMote(experiment, label,
					mote);

		} else if (filter.equals("first")) {
			data = dao.getDataDAO().getFirstDataForEachMote(experiment, label,
					mote);

		}

		logManager.info(request.getSession(), "Data",
				"GET " + request.getPathInfo(), LogManager.getIP(request));

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("data", Conversion.listToJson(data));
		return builder.build().toString();
	}

	/**
	 * Add data to database and send it through a live stream (websocket)
	 * 
	 * @param dataStr
	 *            Data
	 * @return The JSON response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("application/json")
	public String add(@FormParam("data") String dataStr) {
		String message = "";
		Boolean success = false;

		if (dataStr != null) {
			try {
				DataStream data = new DataStream(dataStr, dao);
				List<Data> values = data.convert(DoubleDefaultStrategy.class);

				// Analyse données pour l'envoi d'alerte
				/*for (Data d : values) {
					dataAnalyser.analyse(d);
				}*/

				WebSocket.send(0, data.toJSON());
				dao.getDataDAO().persist(values);
				success = true;
				message = "Data has been successfully saved and forwarded";
				//logManager.info(request.getSession(), "Data", message,LogManager.getIP(request));
			} catch (InvalidDataException | DAOException e) {
				success = false;
				message = e.getMessage();
				logManager.info(request.getSession(), "Data", message,
						LogManager.getIP(request));
			}
		} else {
			success = false;
			message = "Missing parameters";
			logManager.info(request.getSession(), "Data", message,
					LogManager.getIP(request));
		}

		JsonObject json = Json.createObjectBuilder().add("success", success)
				.add("message", message).build();

		return json.toString();
	}

}