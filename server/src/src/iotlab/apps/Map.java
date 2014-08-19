/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps;

import iotlab.core.authentification.AccountManager;
import iotlab.core.beans.dao.ActiveExperimentException;
import iotlab.core.beans.dao.DAOManager;
import iotlab.core.beans.entity.Experiment;
import iotlab.core.beans.entity.mote.Mote;
import iotlab.module.admin.Config;
import iotlab.module.data.Data;
import iotlab.module.data.DataStream;
import iotlab.utils.Conversion;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * @author Arthur Garnier
 *
 */
@WebServlet("/map")
public class Map extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	private DAOManager dao;
	@EJB
	private Config config;
	@EJB
	private AccountManager accountManager;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */

	public static String imgMap;

	public Map() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	
		if (request.getParameterMap().size() == 0
				|| request.getParameter("config") == null) {
			Collection<Experiment> listExp = dao.getExperimentDAO().getAll(
					"id", true);
			request.setAttribute("listExp", listExp);
			if (request.getParameter("mode") == null) {
				response.sendRedirect(request.getContextPath() + "/index.html");
			} else
				switch (request.getParameter("mode")) {
				case "date":
					request.getServletContext()
							.getRequestDispatcher("/WEB-INF/index.jsp?page=map-config-date")
							.forward(request, response);
					break;
				case "compare":
					request.getServletContext()
							.getRequestDispatcher(
									"/WEB-INF/index.jsp?page=map-config-compare")
							.forward(request, response);
					break;
				default:
					request.getServletContext()
							.getRequestDispatcher("index.html")
							.forward(request, response);
				}
		} else {
			switch (request.getParameter("config")) {
			case "live":
				try {
					live(request, response);
				} catch (ActiveExperimentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "date":
				specificDate(request, response);
				break;
			case "compare":
				compare(request, response);
				break;
			default:
				request.getServletContext().getRequestDispatcher("/index.html")
						.forward(request, response);
			}
		}
	}

	/**
	 * Méthode réalisant le traitement dans le cas d'une comparaison
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void compare(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Recherche de la plage de données souhaité, si aucune date n'est
		// précisé, toutes les données de l'expérience sont choisies
		long time = (request.getParameter("date") == null) ? 0L : Conversion
				.timeToTimestampLong(request.getParameter("date"));
		long timeEnd = (request.getParameter("dateEnd") == null) ? Long.MAX_VALUE
				: Conversion.timeToTimestampLong(request
						.getParameter("dateEnd"));
		// Création de la condition en fonction des expériences sélectionnées
		String[] experiments = request.getParameterValues("experiment");
		String c = "AND (";
		for (String s : experiments) {
			c += "o.experiment.id=" + s + " OR ";
		}
		c = c.substring(0, c.length() - 3);
		c += ")";
		if (experiments.length == 0) {
			c = "";
		}
		List<Experiment> listExp = dao.getExperimentDAO().getAll("id", true);
		// Récupération de la date de départ de chaque expérience
		List<Data> firstTimebyExp = dao
				.getDataDAO()
				.getAll("id",
						false,
						-1,
						"1=1 "
								+ c
								+ " group by o.experiment having o.timestamp = min(o.timestamp)",
						null);
		long tempsMax = -1;

		ArrayList<JsonObject> firstbyExpJson = DataStream.fromBDD(firstTimebyExp);
		ArrayList<Data> lastTimebyExp = new ArrayList<Data>();
		List<ArrayList<JsonObject>> listGlobal = new ArrayList<>();
		// Pour chaque expérience on récupère les données souhaitées
		for (String s : experiments) {
			int idExp = Integer.parseInt(s);
			long fTime = 0L;
			for (Data d : firstTimebyExp) {
				if (d.getExperiment().getId() == idExp)
					fTime = d.getTimestamp();
			}
			List<Data> listMes = dao.getDataDAO().getAll(
					"id",
					false,
					-1,
					"o.experiment.id="
							+ idExp
							+ " and o.timestamp>"
							+ (fTime + time)
							+ " and o.timestamp<"
							+ (timeEnd == Long.MAX_VALUE ? timeEnd : fTime
									+ timeEnd)
							+ " and o.label.label='temperature'", null);
			Data maxTmp = null;
			// Recherche de la date de fin de l'expérience
			for (Data d : listMes) {
				if (maxTmp == null || d.getTimestamp() > maxTmp.getTimestamp())
					maxTmp = d;
			}
			lastTimebyExp.add(maxTmp);
			listGlobal.add(DataStream.fromBDD(listMes));
		}
		// Grâce aux dates de début et fin de chaque expérience on peut
		// déterminer la plage de valeur du curseur.
		for (Data d : firstTimebyExp) {
			for (Data d2 : lastTimebyExp) {
				if (d.getExperiment().getId() == d2.getExperiment().getId()) {
					if (d2.getTimestamp() - d.getTimestamp() > tempsMax)
						tempsMax = d2.getTimestamp() - d.getTimestamp();
				}
			}
		}

		c = c.replace("o.", "e."); // Prépare la condition pour la sous requête
		List<Mote> list = dao
				.getMoteDAO()
				.getAll("ipv6",
						true,
						-1,
						"o.id in (select distinct e.mote.mote.id from Data e where e.label.label='temperature'"
								+ c + ")", null);// Liste des motes dans les
													// expériences
		ArrayList<JsonObject> ret = new ArrayList<JsonObject>();
		for (Mote m : list) {
			JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
			dataBuilder.add("ip", m.getIpv6());
			dataBuilder.add("lat", m.getLat());
			dataBuilder.add("lon", m.getLon());
			JsonObject jsonObject = dataBuilder.build();
			ret.add(jsonObject);
		}
		request.setAttribute("motes", ret);
		request.setAttribute("firstByExp", firstbyExpJson);
		request.setAttribute("listGlobal", listGlobal);
		request.setAttribute("listExp", listExp);
		request.setAttribute("tempsMax", tempsMax);
		request.getServletContext()
				.getRequestDispatcher("/WEB-INF/index.jsp?page=map-compare")
				.forward(request, response);
	}

	/**
	 * Méthode réalisant le traitement des données pour la page de visualisation
	 * sur une plage de date pour une seule expérience
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void specificDate(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Définition des dates de début et de fin voulue par l'utilisation
		// Si aucune date n'est précisé alors toute l'expérience est prise en
		// compte
		Timestamp timeBegin = new Timestamp(Long.MIN_VALUE);
		Timestamp timeEnd = new Timestamp(Long.MAX_VALUE);
		try {
			if (request.getParameter("date") != null)
				timeBegin = Conversion.dateToTimestamp(request
						.getParameter("date"));
			if (request.getParameter("dateEnd") != null)
				timeEnd = Conversion.dateToTimestamp(request
						.getParameter("dateEnd"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// Récupération de l'expérience souhaitée
		int experiment = Integer.parseInt(request.getParameter("experiment"));
		// Si aucune date de départ n'est précisé alors il n'y a pas de
		// condition dessus...
		String condBegin = (timeBegin.getTime() == Long.MIN_VALUE) ? "1=1"
				: "o.timestamp>=" + timeBegin.getTime();
		List<Data> dataList = dao.getDataDAO().getAll(
				"id",
				false,
				-1,
				condBegin + " AND o.timestamp<=" + timeEnd.getTime()
						+ " AND o.experiment.id=" + experiment
						+ " AND o.label.label='temperature'", null);
		// Recherche de la première et dernière date des données afin de
		// configurer le curseur
		long max = Long.MIN_VALUE, min = Long.MAX_VALUE;
		for (Data d : dataList) {
			if (d.getTimestamp() < min)
				min = d.getTimestamp();
			if (d.getTimestamp() > max)
				max = d.getTimestamp();
		}
		ArrayList<JsonObject> dataJson = DataStream.fromBDD(dataList);
		// Récupération de la liste de mote concernée par l'expérience choisie
		List<Mote> list = dao.getMoteDAO().getAll(
				"ipv6",
				true,
				-1,
				"o.id in (select distinct e.mote.mote.id from Data e where e.experiment.id="
						+ experiment + " and e.label.label='temperature')",
				null);
		ArrayList<JsonObject> ret = new ArrayList<JsonObject>();
		// Création du Json pour les motes
		for (Mote m : list) {
			JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
			dataBuilder.add("ip", m.getIpv6());
			dataBuilder.add("lat", m.getLat());
			dataBuilder.add("lon", m.getLon());
			JsonObject jsonObject = dataBuilder.build();
			ret.add(jsonObject);
		}
		request.setAttribute("motes", ret);
		request.setAttribute("minTime", min);
		request.setAttribute("maxTime", max);
		request.setAttribute("lastData", dataJson);
		request.getServletContext()
				.getRequestDispatcher("/WEB-INF/index.jsp?page=map-date")
				.forward(request, response);
	}

	/**
	 * Méthode préparant les données pour une vue en directe.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @throws ActiveExperimentException
	 */
	private void live(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ActiveExperimentException {
		// Liste des motes concernée par l'expérience en cours
		List<Mote> list = dao.getMoteDAO().getAll(
				"ipv6",
				true,
				-1,
				"o.id in (select distinct e.mote.mote.id from Data e where e.experiment.id="
						+ dao.getExperimentDAO().getActiveExperiment().getId()
						+ " and e.label.label='temperature')", null);
		ArrayList<JsonObject> ret = new ArrayList<JsonObject>();
		for (Mote m : list) {
			JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
			dataBuilder.add("ip", m.getIpv6());
			dataBuilder.add("lat", m.getLat());
			dataBuilder.add("lon", m.getLon());
			JsonObject jsonObject = dataBuilder.build();
			ret.add(jsonObject);
		}
		request.setAttribute("motes", ret);
		request.setAttribute("lastData", new ArrayList<>());
		request.getServletContext()
				.getRequestDispatcher("/WEB-INF/index.jsp?page=map-live")
				.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if(accountManager.isLoggedIn(request.getSession())){
			// On enregistre la position des capteurs envoyé par la page Live&Edit
			// dans la BDD
			String o = request.getParameter("tab_mote");
			//System.out.println(o);
			String[] tab_mote = o.split(",");
			List<Mote> list = dao.getMoteDAO().getAll("id", true);
			for (String mote : tab_mote) {
				String[] data = mote.split(":");
				String ip = data[0];
				String x = data[1];
				String y = data[2];
				for (Mote m : list) {
					if (m.getIpv6().equals(ip)) {
						m.setLat(Double.parseDouble(y));
						m.setLon(Double.parseDouble(x));
						dao.getMoteDAO().merge(m);
						break;
					}
				}
			}
		}
		response.sendRedirect("map?config=live");
	}

}
