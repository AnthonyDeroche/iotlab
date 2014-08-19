/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps.api;

import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.entity.Filter;
import iotlab.core.beans.entity.Label;
import iotlab.core.beans.entity.Strategy;
import iotlab.core.beans.entity.Type;
import iotlab.core.beans.entity.mote.Mote;
import iotlab.core.beans.entity.mote.Sender;
import iotlab.core.beans.entity.mote.Sink;
import iotlab.core.inputStream.InvalidDataException;
import iotlab.core.inputStream.SinkStream;
import iotlab.core.inputStream.strategy.DoubleDefaultStrategy;
import iotlab.module.admin.Config;
import iotlab.module.admin.Stats;
import iotlab.module.geolocation.Anchor;
import iotlab.module.monitoring.Log;
import iotlab.module.monitoring.LogManager;
import iotlab.utils.Conversion;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArray;
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
@Path("info")
public class InfoResource extends RestResource {

	public InfoResource() {
		// TODO Auto-generated constructor stub
	}

	/**************************** MOTES **********************************/

	@GET
	@Produces("application/json")
	@Path("motes")
	public String getMotes() {
		List<Sender> senders = dao.getSenderDAO().getAll();
		List<Sink> sinks = dao.getSinkDAO().getAll();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("motesNb", senders.size() + sinks.size());
		builder.add("sender", Conversion.listToJson(senders));
		builder.add("sink", Conversion.listToJson(sinks));
		JsonObject json = builder.build();
		logManager.info(request.getSession(), "Info",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return json.toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("motes/sink/add")
	public String addSink(@FormParam("data") String dataStr) {
		String message = "";
		Boolean success = false;
		try {
			SinkStream sinkStream = new SinkStream(dataStr, dao);
			sinkStream.convert(DoubleDefaultStrategy.class);
			success = true;
			message = "Sink has been successfully saved";
		} catch (DAOException | InvalidDataException e) {
			success = false;
			message = e.getMessage();
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("motes/remove")
	public String removeMote(@FormParam("mote_id") int mote_id) {
		String message = "";
		Boolean success = false;
		JsonObjectBuilder builder = Json.createObjectBuilder();
		try {
			dao.getMoteDAO().remove(dao.getMoteDAO().getReference(mote_id));
			builder.add("mote_id", mote_id);
			success = true;
			message = "Mote has been successfully removed";
		} catch (DAOException e) {
			success = false;
			message = e.getMessage();
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return builder.add("success", success).add("message", message).build()
				.toString();
	}

	@GET
	@Produces("application/json")
	@Path("motes/anchor")
	public String getAnchors() {
		List<Anchor> anchors = dao.getAnchorDAO().getAll();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("anchorsNb", anchors.size());
		builder.add("anchor", Conversion.listToJson(anchors));
		JsonObject json = builder.build();
		logManager.info(request.getSession(), "Info",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return json.toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("motes/anchor/add")
	public String addAnchor(@FormParam("mote_id") int id) {
		String message = "";
		Boolean success = false;
		JsonObjectBuilder builder = Json.createObjectBuilder();
		try {
			Mote mote = dao.getMoteDAO().find(id);
			dao.getAnchorDAO().merge(new Anchor(mote));
			builder.add("mote_id", id);
			success = true;
			message = "Anchor has been successfully added";
		} catch (DAOException e) {
			success = false;
			message = e.getMessage();
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return builder.add("success", success).add("message", message).build()
				.toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("motes/anchor/remove")
	public String removeAnchor(@FormParam("mote_id") int id) {
		String message = "";
		Boolean success = false;
		JsonObjectBuilder builder = Json.createObjectBuilder();
		try {
			dao.getAnchorDAO().remove(dao.getAnchorDAO().getReference(id));
			builder.add("mote_id", id);
			success = true;
			message = "Anchor has been successfully removed";
		} catch (DAOException e) {
			success = false;
			message = e.getMessage();
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return builder.add("success", success).add("message", message).build()
				.toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("motes")
	public String setMotesPosition(String json) {

		JsonArray array = Json.createReader(new StringReader(json)).readArray();
		for (int i = 0; i < array.size(); i++) {
			JsonObject mote = array.getJsonObject(i);
			Mote moteToUpdate;
			try {
				moteToUpdate = dao.getMoteDAO().find(mote.getInt("id"));
				if (moteToUpdate != null) {
					moteToUpdate.setLat(mote.getInt("lat"));
					moteToUpdate.setLon(mote.getInt("lon"));
					dao.getMoteDAO().merge(moteToUpdate);
				}
			} catch (DAOException e) {
				// mote not found
			}

		}
		logManager.info(request.getSession(), "Info",
				"Updated motes' position", LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", true)
				.add("message", "Positions successfully saved").build()
				.toString();
	}

	/**************************** LABELS **********************************/

	@GET
	@Produces("application/json")
	@Path("labels")
	public String getLabels() {
		List<Label> labels = dao.getLabelDAO().getAll("label_id", true);
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("labelsNb", labels.size());
		builder.add("labels", Conversion.listToJson(labels));
		JsonObject json = builder.build();
		logManager.info(request.getSession(), "Info",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return json.toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("labels/add")
	public String addLabel(@FormParam("label") String label) {

		boolean success;
		String message;
		try {
			dao.getLabelDAO().find(label);
			success = false;
			message = "Label " + label
					+ " already exists ! Labels must be unique.";
		} catch (DAOException e) {
			// no result so we can persist the new one
			dao.getLabelDAO().persist(new Label(label));
			success = true;
			message = "Label " + label + " has been successfully added !";
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("labels/delete")
	public String deleteLabel(@FormParam("label_id") int label_id) {

		boolean success = false;
		String message = "";

		try {
			Label lab = dao.getLabelDAO().find(label_id);
			dao.getLabelDAO().remove(lab);
			message = "Label '" + lab.getLabel() + "' has been deleted";
			success = true;
		} catch (DAOException e) {
			message = e.getMessage();
			success = false;
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	/**************************** FILTERS **********************************/

	@GET
	@Produces("application/json")
	@Path("filters")
	public String getFilters() {
		List<Filter> filters = dao.getFilterDAO().getAll("type,o.offset", true);
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("filtersNb", filters.size());
		builder.add("filters", Conversion.listToJson(filters));
		JsonObject json = builder.build();
		logManager.info(request.getSession(), "Info",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return json.toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("filters/add")
	public String addFilter(@FormParam("offset") int offset,
			@FormParam("strategy") int strategy_id,
			@FormParam("label") int label_id, @FormParam("type") int type_id) {

		boolean success;
		String message;

		try {
			Type t = dao.getTypeDAO().find(type_id);

			try {
				dao.getFilterDAO().find(offset, t);
				success = false;
				message = "Filter with offset "
						+ offset
						+ "and type "
						+ t.getId()
						+ " already exists ! An offset can be used only once for a given type.";
			} catch (DAOException e) {
				// no result so we can persist the new one
				Strategy s;
				try {
					t = dao.getTypeDAO().getReference(type_id);
					s = dao.getStrategyDAO().getReference(strategy_id);
					Label l = dao.getLabelDAO().getReference(label_id);
					System.out.println("type_id :" + t.getId());
					dao.getFilterDAO().merge(new Filter(offset, s, l, t));
					success = true;
					message = "The Filter has been successfully added !";
				} catch (DAOException e1) {
					success = false;
					message = e1.getMessage();
				}

			}
		} catch (DAOException e) {
			success = false;
			message = e.getMessage();
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("filters/delete")
	public String deleteFilter(@FormParam("filter_id") int filter_id) {

		boolean success = false;
		String message = "";

		try {
			Filter f = dao.getFilterDAO().find(filter_id);
			dao.getFilterDAO().remove(f);
			message = "Filter with offset '" + f.getOffset() + " and type "
					+ f.getType().getId() + "' has been deleted";
			success = true;
		} catch (DAOException e) {
			message = e.getMessage();
			success = false;
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	/**************************** STRATEGIES **********************************/

	@GET
	@Produces("application/json")
	@Path("strategies")
	public String getStrategies() {
		List<Strategy> strategies = dao.getStrategyDAO().getAll("id", true);
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("strategiesNb", strategies.size());
		builder.add("strategies", Conversion.listToJson(strategies));
		JsonObject json = builder.build();
		logManager.info(request.getSession(), "Info",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return json.toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("strategies/add")
	public String addStrategy(@FormParam("className") String className) {

		boolean success;
		String message;
		try {
			dao.getStrategyDAO().find(className);
			success = false;
			message = "Strategy associated with the class " + className
					+ " already exists ! Strategies must be unique.";
		} catch (DAOException e) {
			// no result so we can persist the new one
			dao.getStrategyDAO().persist(new Strategy(className));
			success = true;
			message = "Strategy " + className
					+ " has been successfully added !";
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("strategies/delete")
	public String deleteStrategy(@FormParam("id") int id) {

		boolean success = false;
		String message = "";

		try {
			Strategy s = dao.getStrategyDAO().find(id);
			dao.getStrategyDAO().remove(s);
			message = "Strategy associated with class '" + s.getClassName()
					+ "' has been deleted";
			success = true;
		} catch (DAOException e) {
			message = e.getMessage();
			success = false;
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	/**************************** TYPES **********************************/
	@GET
	@Produces("application/json")
	@Path("types")
	public String getTypes() {
		List<Type> types = dao.getTypeDAO().getAll("id", true);
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("typesNb", types.size());
		builder.add("types", Conversion.listToJson(types));
		JsonObject json = builder.build();
		logManager.info(request.getSession(), "Info",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return json.toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("types/add")
	public String addType(@FormParam("type_id") int type_id,
			@FormParam("description") String description,
			@FormParam("streamName") String streamName,
			@FormParam("minDataNumber") int minDataNumber) {

		boolean success;
		String message;
		try {
			dao.getTypeDAO().find(type_id);
			success = false;
			message = "Type " + type_id
					+ " already exists ! Types must be unique.";
		} catch (DAOException e) {
			// no result so we can persist the new one
			dao.getTypeDAO().persist(new Type(type_id,description,streamName,minDataNumber));
			success = true;
			message = "Type " + type_id + " has been successfully added !";
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("types/delete")
	public String deleteType(@FormParam("type_id") int type_id) {

		boolean success = false;
		String message = "";

		try {
			Type type = dao.getTypeDAO().find(type_id);
			dao.getTypeDAO().remove(type);
			message = "Type '" + type.getId() + "' has been deleted";
			success = true;
		} catch (DAOException e) {
			message = e.getMessage();
			success = false;
		}
		logManager.info(request.getSession(), "Info", message,
				LogManager.getIP(request));
		return Json.createObjectBuilder().add("success", success)
				.add("message", message).build().toString();
	}

	/**************************** LOGS **********************************/

	@GET
	@Produces("application/json")
	@Path("logs{s1 : /?}{nb : (\\d+)?}")
	public String getLogs(@PathParam("nb") int nb) {
		List<Log> logs = dao.getLogDAO().getAll("datetime", false, nb);
		// logManager.info(request.getSession(), "Info", "GET " +
		// request.getPathInfo());
		return Conversion.listToJson(logs).toString();
	}

	/**************************** DBSTATS **********************************/

	@EJB
	private Stats dbstats;

	@GET
	@Produces("application/json")
	@Path("dbstats")
	public String getDbStats() {
		dbstats.calculate();
		return Conversion.listToJson(dbstats).toString();
	}

	/**************************** STATS **********************************/

	@GET
	@Produces("application/json")
	@Path("stats/{experiment : \\d+}{s1:/?}{mote : (\\d+\\.\\d+(-\\d+\\.\\d+)*)?}{s2:/?}{from : (\\d+)?}{s3:/?}{to : (\\d+)?}")
	public String getStats(@PathParam("experiment") int experiment,
			@PathParam("mote") String mote, @PathParam("from") long from,
			@PathParam("to") long to) {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObjectBuilder abuilder = Json.createObjectBuilder();

		HashMap<String, Double> lossRatios = dao.getDataDAO().getLossRatio(
				experiment, mote, from, to);
		for (Entry<String, Double> e : lossRatios.entrySet()) {
			abuilder.add(e.getKey(), String.format("%.2f", e.getValue()));
		}

		return builder.add("stats", abuilder.build()).build().toString();
	}

	/**************************** CONF **********************************/

	@EJB
	private Config config;

	@GET
	@Produces("application/json")
	@Path("conf")
	public String getConf() {
		logManager.info(request.getSession(), "Info",
				"GET " + request.getPathInfo(), LogManager.getIP(request));
		return Conversion.listToJson(new ArrayList<>(config.values()))
				.toString();
	}

}
