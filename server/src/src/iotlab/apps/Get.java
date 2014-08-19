/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps;

import iotlab.core.beans.dao.DAOManager;
import iotlab.core.beans.entity.Experiment;
import iotlab.core.beans.entity.mote.Sender;
import iotlab.module.admin.Config;
import iotlab.module.data.Data;
import iotlab.module.data.DataStream;
import iotlab.utils.Conversion;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.json.JsonObject;
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
@WebServlet("/get")
public class Get extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB
	private DAOManager dao;
	@EJB
	private Config config;
	private int nb;

	private static final String VIEW = "/WEB-INF/index.jsp?page=data";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Get() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		// System.out.println(request.getHeader("referer"));
		nb = -1;
		// S'il n'y a pas de paramètres, on redirige vers la page de
		// configuration pour les graphiques
		if (request.getParameterMap().size() == 0) {
			Collection<Experiment> listExp = dao.getExperimentDAO().getAll(
					"id", true);
			request.setAttribute("listExp", listExp);
			this.getServletContext()
					.getRequestDispatcher("/WEB-INF/index.jsp?page=home")
					.forward(request, response);
		} else {
			String condition = "1=1 ";
			try {
				// Préparation de la condition pour la requête en fonction des
				// paramètres reçus
				for (String s : request.getParameterMap().keySet()) {
					switch (s) {
					case "experiment":
						if (request.getParameterMap().get(s).length == 1)
							condition += "AND o.experiment.id="
									+ request.getParameterMap().get(s)[0] + " ";
						else {
							condition += "AND (";
							for (String st : request.getParameterMap().get(s)) {
								condition += "o.experiment.id=" + st + " OR ";
							}
							condition = condition.substring(0,
									condition.length() - 3);
							condition += ")";
						}

						break;
					case "label":
						if (request.getParameterMap().get(s).length == 1)
							condition += "AND o.label.label='"
									+ request.getParameterMap().get(s)[0]
									+ "' ";
						else {
							condition += "AND (";
							for (String st : request.getParameterMap().get(s)) {
								condition += "o.label.label='" + st + "' OR ";
							}
							condition = condition.substring(0,
									condition.length() - 3);
							condition += ")";
						}
						break;
					case "date_deb":
						condition += "AND o.timestamp>='"
								+ Conversion.dateToTimestamp(request
										.getParameterMap().get(s)[0]) + "' ";
						break;
					case "date_fin":
						condition += "AND o.timestamp<='"
								+ Conversion.dateToTimestamp(request
										.getParameterMap().get(s)[0]) + "' ";
						break;
					default:
						break;
					}
				}
				List<Data> lastData = new ArrayList<Data>();
				// Récupération des données (triées pour la légende) pour le
				// graphique
				lastData = dao.getDataDAO().getAll("mote.id, o.id", false, nb,
						condition, null);
				Collections.reverse(lastData);
				if (request.getParameter("mode") != null
						&& request.getParameter("mode").equals("compare")) {
					System.out.println("Mode comparaison");
					lastData = traitementCompareMode(lastData, request);
				}
				ArrayList<JsonObject> list_tmp = DataStream.fromBDD(lastData);
				request.setAttribute("nb", nb);
				request.setAttribute("lastData", list_tmp);
				this.getServletContext().getRequestDispatcher(VIEW)
						.forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Méthode traitant les données pour le mode comparaison
	 * 
	 * @param list
	 *            Liste des données
	 * @param request
	 * @return Une liste de Data traité pour la comparaison
	 */
	private List<Data> traitementCompareMode(List<Data> list,
			HttpServletRequest request) {
		// -3600*1000 Est causé par le décalage horaire UTC de timestamp sur
		// highcharts
		Timestamp timeBegin = new Timestamp(0 - 3600 * 1000);
		Timestamp timeEnd = new Timestamp(Long.MAX_VALUE - 3600 * 1000);
		String[] cond = request.getParameterValues("experiment");
		// Préparation de la condition pour récupérer les dates de début de
		// chaque expériences concernées
		String c = "AND (";
		for (String s : cond) {
			c += "o.experiment.id=" + s + " OR ";
		}
		c = c.substring(0, c.length() - 3);
		c += ")";
		if (cond.length == 0) {
			c = "";
		}
		if (request.getParameter("beginCompare") != null) {
			timeBegin = new Timestamp(Conversion.timeToTimestampLong(request
					.getParameter("beginCompare")) - 3600 * 1000);
		}
		if (request.getParameter("endCompare") != null) {
			timeEnd = new Timestamp(Conversion.timeToTimestampLong(request
					.getParameter("endCompare")) - 3600 * 1000);
		}
		List<Data> newL = new ArrayList<Data>();
		List<Data> lExp = dao
				.getDataDAO()
				.getAll("id",
						true,
						-1,
						"1=1 "
								+ c
								+ " group by o.experiment having o.timestamp = min(o.timestamp)",
						null);
		System.out.println(lExp);
		// Pour chaque données pour concactène son Ip avec le nom d'expérience
		// pour les différencier sur le graphique
		for (Data d : list) {
			for (int i = 0; i < lExp.size(); i++) {
				if (d.getExperiment().getId() == lExp.get(i).getExperiment()
						.getId()) {
					d.setTimestamp(new Timestamp(d.getTimestamp()
							- lExp.get(i).getTimestamp() - 3600 * 1000));
					Sender m = new Sender(d.getMote().getIpv6(), d.getMote()
							.getIpv6());
					m.setIpv6(d.getMote().getIpv6() + ":"
							+ d.getExperiment().getComments());
					d.setMote(m);
					break;
				}
			}
			// Si la données est dans les bornes de temps demandé, on l'ajoute à
			// la liste des données à afficher
			if (d.getTimestamp() > timeBegin.getTime()
					&& d.getTimestamp() < timeEnd.getTime())
				newL.add(d);
		}
		return newL;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Traitement des champs post reçu pour ajouter une nouvelle mesure
		// (expérience dans la BDD)
		List<Experiment> l = dao.getExperimentDAO().getAll("id", true, -1,
				"o.active=true", null);
		for (Experiment e : l) {
			e.setActive(false);
			dao.getExperimentDAO().merge(e);
		}
		Experiment e = new Experiment();
		e.setActive(true);
		e.setComments(request.getParameter("label"));
		e.setDescription(request.getParameter("desc"));
		dao.getExperimentDAO().persist(e);
		nb = Integer.MAX_VALUE;
		Collection<Experiment> listExp = dao.getExperimentDAO().getAll("id",
				true);
		request.setAttribute("listExp", listExp);
		// this.getServletContext().getRequestDispatcher("/WEB-INF/home.jsp")
		// .forward(request, response);
		response.sendRedirect(request.getHeader("referer"));

	}

}
