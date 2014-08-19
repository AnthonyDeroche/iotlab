/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.apps;

import iotlab.core.beans.dao.DAOManager;
import iotlab.module.admin.Config;

import java.io.IOException;

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
@WebServlet("/live")
public class LiveData extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	private DAOManager dao;
	
	@EJB
	private Config config;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LiveData() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String view = "/WEB-INF/index.jsp?page=data";
		
		int nb=20;
		if (request.getParameter("nb") != null)
			try {
				nb = Integer.parseInt(request.getParameter("nb"));
			} catch (NumberFormatException e) {
				
			}

		request.setAttribute("nb", nb);
		request.setAttribute("live", true);

		this.getServletContext().getRequestDispatcher(view)
				.forward(request, response);
	}

}
