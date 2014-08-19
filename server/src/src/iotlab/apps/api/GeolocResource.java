/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps.api;

import iotlab.core.beans.dao.DAOException;
import iotlab.core.inputStream.InvalidDataException;
import iotlab.core.inputStream.strategy.DoubleDefaultStrategy;
import iotlab.module.geolocation.GeolocStream;
import iotlab.module.geolocation.GeolocationData;
import iotlab.module.geolocation.GeolocationManager;
import iotlab.module.geolocation.algorithm.AlgorithmTester;
import iotlab.module.geolocation.calibration.CalibrationData;
import iotlab.module.geolocation.calibration.CalibrationMeasure;
import iotlab.module.monitoring.LogManager;
import iotlab.utils.Conversion;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Anthony Deroche
 *
 */
@Path("geo")
public class GeolocResource extends RestResource {

	@EJB
	private GeolocationManager manager;

	@EJB
	private AlgorithmTester optimizer;

	@GET
	@Produces("application/json")
	@Path("services")
	public String getServices() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("services", Conversion.listToJson(manager.getServices()));
		return builder.build().toString();
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("application/json")
	@Path("services/enable")
	public String enable(@FormParam("service") String service) {
		boolean success;
		String message;
		if (manager.enable(service)) {
			success = true;
			message = "Service successfully enabled";
			logManager.info(request.getSession(), "Geolocation",
					"Enabled service " + service, LogManager.getIP(request));
		} else {
			success = false;
			message = "Unknown service";
			logManager.info(request.getSession(), "Geolocation", message,
					LogManager.getIP(request));
		}
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("application/json")
	@Path("services/disable")
	public String disable(@FormParam("service") String service) {
		boolean success;
		String message;
		if (manager.disable(service)) {
			success = true;
			message = "Service successfully disabled";
			logManager.info(request.getSession(), "Geolocation",
					"Disabled service " + service, LogManager.getIP(request));
		} else {
			success = false;
			message = "Unknown service";
			logManager.info(request.getSession(), "Geolocation", message,
					LogManager.getIP(request));
		}
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("application/json")
	public String add(@FormParam("data") String dataStr,
			@Context HttpServletRequest req) {
		String message = "";
		Boolean success = false;

		if (dataStr != null) {
			try {

				if (manager.isEnabled(GeolocationManager.GEOLOCATION)) {
					GeolocStream gstream = new GeolocStream(dataStr, dao);
					List<GeolocationData> values = gstream
							.convert(DoubleDefaultStrategy.class);
					dao.getGeolocationDataDAO().merge(values);
					JsonObjectBuilder builder = Json.createObjectBuilder();

					/************ real-time virtual calibration + localization *******************/

					manager.executeProcess(builder, values,
							gstream.getTimestamp());
					WebSocket.send(10, builder.build());

					/************ end calibration + localization *******************/
					success = true;
					message = "Geo Data has been successfully saved and forwarded";
				} else {
					success = true;
					message = "Data have been received but the service is disabled";
				}

			} catch (InvalidDataException | DAOException e) {
				success = false;
				message = e.getMessage();
			}
		} else {
			success = false;
			message = "Missing parameters";
		}
		// logManager.info(request.getSession(), "Geolocation",
		// message,LogManager.getIP(request));
		JsonObject json = Json.createObjectBuilder().add("success", success)
				.add("message", message).build();

		return json.toString();
	}

	@GET
	@Produces("application/json")
	@Path("{nb : (\\d+)}{s1:/?}{src : (\\d+\\.\\d+(-\\d+\\.\\d+)*)?}{s2:/?}{dest : (\\d+\\.\\d+(-\\d+\\.\\d+)*)?}{s3:/?}{from : (\\d+)?}{s4:/?}{to : (\\d+)?}")
	public String get(@PathParam("nb") int nb, @PathParam("src") String src,
			@PathParam("dest") String dest, @PathParam("from") long from,
			@PathParam("to") long to) {

		List<GeolocationData> data = new ArrayList<>();

		JsonObjectBuilder builder = Json.createObjectBuilder();
		try {
			data = dao.getGeolocationDataDAO().getMeans(false, nb, src, dest,
					from, to);
			builder.add("data", Conversion.listToJson(data));
		} catch (DAOException e) {
			// mote not found
		}
		logManager.info(request.getSession(), "Geolocation",
				"GET " + request.getPathInfo(), LogManager.getIP(request));

		return builder.build().toString();
	}

	@GET
	@Produces("application/json")
	@Path("{filter : (last)}{s1:/?}{src : (\\d+\\.\\d+(-\\d+\\.\\d+)*)?}{s2:/?}{dest : (\\d+\\.\\d+(-\\d+\\.\\d+)*)?}")
	public String get(@PathParam("filter") String filter,
			@PathParam("src") String src, @PathParam("dest") String dest,
			@PathParam("from") long from, @PathParam("to") long to) {

		List<GeolocationData> data = new ArrayList<>();

		data = dao.getGeolocationDataDAO().getLastDataForEachMote(src, dest);

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("data", Conversion.listToJson(data));
		logManager.info(request.getSession(), "Geolocation",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return builder.build().toString();
	}

	@GET
	@Produces("application/json")
	@Path("calibration/{filter : (last)}{s1:/?}{nb : (\\d+)}")
	public String getLastCalibration(@PathParam("filter") String filter,
			@PathParam("nb") int nb) {

		List<CalibrationData> data = new ArrayList<>();

		data = dao.getCalibrationDataDAO().getLastDataForEachMote(nb);

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("data", Conversion.listToJson(data));
		logManager.info(request.getSession(), "Geolocation",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return builder.build().toString();
	}

	@GET
	@Produces("application/json")
	@Path("calibration/measures")
	public String getCalibrationMeasures() {

		List<CalibrationMeasure> data = new ArrayList<>();

		data = dao.getCalibrationMeasureDAO().getAll();

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("measures", Conversion.listToJson(data));
		logManager.info(request.getSession(), "Geolocation",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return builder.build().toString();
	}

	@GET
	@Produces("application/json")
	@Path("tester/status")
	public String getOptimizerStatus() {
		boolean started;
		started = optimizer.isOptimizerStarted();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("started", started);
		return builder.build().toString();
	}

	@POST
	@Produces("application/json")
	@Path("tester/startOptimizer")
	public String startOptimizer() {
		boolean success;
		String message;

		optimizer.start();
		success = true;
		message = "Optimizer successfully started";

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("success", success).add("message", message);
		logManager.info(request.getSession(), "Geolocation",
				"Started geolocation's optimizer", LogManager.getIP(request));
		return builder.build().toString();
	}

	@POST
	@Produces("application/json")
	@Path("tester/stopOptimizer")
	public String stopOptimizer() {
		boolean success;
		String message;

		optimizer.stop();
		success = true;
		message = "Optimizer successfully stopped";

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("success", success).add("message", message);
		logManager.info(request.getSession(), "Geolocation",
				"Stopped geolocation's optimizer", LogManager.getIP(request));
		return builder.build().toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("tester/weights")
	public String setAlgoWeights(String json) {

		JsonArray array = Json.createReader(new StringReader(json)).readArray();
		for (int i = 0; i < array.size(); i++) {
			JsonObject algo = array.getJsonObject(i);
			manager.setWeight(
					algo.get("name").toString(),
					new double[] {
							Double.parseDouble(algo.get("xweight").toString()),
							Double.parseDouble(algo.get("yweight").toString()) });
		}
		logManager.info(request.getSession(), "Info",
				"Updated geolocation algo weights", LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", true)
				.add("message", "Weights successfully received").build()
				.toString();
	}
}
