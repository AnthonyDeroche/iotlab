/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.module.admin;

import iotlab.core.beans.dao.DAOException;
import iotlab.core.beans.dao.DAOManager;
import iotlab.core.beans.entity.Error;
import iotlab.core.beans.entity.Label;
import iotlab.core.beans.entity.mote.Mote;
import iotlab.module.monitoring.MonitoringRule;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * @author Anthony Deroche - Thierry Duhal - Arthur Garnier
 *
 */
@WebServlet(urlPatterns = { "/admin/*" })
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String VIEW = "/WEB-INF/index.jsp?page=admin";
	
	@EJB
	private DAOManager dao;
	
	@EJB
	private Stats stats;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Admin() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		String success = "{}";
		if(request.getParameter("select_mote") != null && request.getParameter("select_sensor") != null && request.getParameter("min") != null && request.getParameter("max") != null){
			Mote m;
			try {
				m = dao.getMoteDAO().find(Integer.parseInt(request.getParameter("select_mote")));
				Label l;
				l = dao.getLabelDAO().find(Integer.parseInt(request.getParameter("select_sensor")));
				MonitoringRule mr = new MonitoringRule(m, l, Double.parseDouble(request.getParameter("min")), Double.parseDouble(request.getParameter("max")));
				dao.getMonitoringRuleDAO().persist(mr);
				response.sendRedirect(request.getHeader("referer"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (DAOException e) {
				e.printStackTrace();
			}
			
		} else if(request.getParameter("idDel") != null){
			MonitoringRule mr = dao.getMonitoringRuleDAO().find(Integer.parseInt(request.getParameter("idDel")));
			try {
				dao.getMonitoringRuleDAO().remove(mr);
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response.sendRedirect(request.getHeader("referer"));
		}
		else {
			// no parameters
			Error error = new Error("HTTP POST", "POST Parameters are missing");
			dao.getErrorDAO().persist(error);
			success = "{\"success\":\"0\",\"message\":\"Application error : "
					+ error.getMessage() + " (#" + error.getId() + ")\"}";
		}
		out.print(success);

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		if (request.getParameter("load") != null) {
			String load = request.getParameter("load");
			String view = VIEW;
			switch (load) {
			case "rules":
				List<Mote> listMote = dao.getMoteDAO().getAll("ipv6", true);
				List<Label> listLabel = dao.getLabelDAO().getAll("label", true, -1, "o.label_id IN (Select l.label.label_id from Filter l where l.strategy.id <> 1)", null);
				List<MonitoringRule> listMR = dao.getMonitoringRuleDAO().getAll("id", true);
				request.setAttribute("listMote", listMote);
				request.setAttribute("listLabel", listLabel);
				request.setAttribute("listMR", listMR);
				view = "/WEB-INF/admin/rules.jsp";
			default:
				break;
			}
			this.getServletContext().getRequestDispatcher(view)
					.forward(request, response);
		} else {
			this.getServletContext().getRequestDispatcher(VIEW)
				.forward(request, response);
	}
}
}
